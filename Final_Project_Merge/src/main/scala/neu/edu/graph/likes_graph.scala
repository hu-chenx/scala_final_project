
import com.restfb.json.JsonObject
import com.restfb.DefaultFacebookClient
import com.restfb.FacebookClient
import com.restfb.types.Post
import com.restfb.types.Likes
import com.restfb.types.User
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.Await
import com.restfb.Connection
import java.util.{Iterator => JIterator}
import scala.concurrent.ExecutionContext.Implicits.global
import com.restfb.Parameter
import scala.collection.JavaConversions._
import scala.concurrent.duration.Duration
import scala.util.{Success, Failure}
import com.restfb.types.Page
import java.io.File
import akka.actor.Actor
import sys.ShutdownHookThread
import eu.fakod.neo4jscala.{TypedTraverser, SingletonEmbeddedGraphDatabaseServiceProvider, Neo4jWrapper,Cypher}
import collection.JavaConversions._
import org.neo4j.graphdb._
import org.neo4j.graphdb.index._
import org.neo4j.tooling.GlobalGraphOperations
import scala.collection.breakOut
import org.neo4j.cypher._


object likes_graph extends App with Neo4jWrapper with SingletonEmbeddedGraphDatabaseServiceProvider with TypedTraverser with Cypher {


  ShutdownHookThread {
      shutdown(ds)
    }

  def neo4jStoreDir = "/home/chenxi/Documents/graph_1"
  //val simSet = Set("null")
  
def fetch_likes(id:String, fbClient: DefaultFacebookClient, count:Integer):Boolean = {
  if(count == 0)  true
  val result_1 = fbClient.fetchConnection("me/likes", classOf[Page])
  val me_list_page = result_1.toIterator.toList  
  val like_list = for{
              mlp <- me_list_page      
              ml  <- mlp              
    }yield ml
    // print likes name
    like_list.foreach { x => {
      println(x.getName)
      
      fetch_likes(x.getId, fbClient,count-1)
      } 
    } 
 
    
    true
}

def fetch_likes_graph(user:Node, fbClient: DefaultFacebookClient, count:Integer):Node = {
  if(count == 0)  user
  println("*************Layer "+count+"****************")
  def result = {
    if(user == null) fbClient.fetchConnection("me/likes", classOf[Page])
    else {
      withTx {
        implicit neo => fbClient.fetchConnection(user.getProperty("id").toString()+"/likes", classOf[Page])    
      }
    }
  }
  val me_list_page = result.toIterator.toList  
  val like_list = for{
              mlp <- me_list_page      
              ml  <- mlp              
    }yield ml    
    
    /**
   * creating nodes and associations
   */
  val startNode : Node = withTx {
      implicit neo => val start = {
      if(user == null) createNode("start")
      else user
      }
      start
    }

  val nodeMap = withTx {
    implicit neo => 
      val nodelist = List[Node]()
      for(n <- like_list){
      val node = {

         val result = getAllNodesWithLabel(n.getId).iterator
          if(result.hasNext){
            println(n.getName + n.getId)
            val existNode = result.next()
            nodelist :+ existNode
            existNode
        }else{
          val newNode = createNode(GraphNode(n.getName,n.getId,"topic"),n.getId)
          nodelist :+ newNode
          newNode
        }
      }
      startNode --> "LIKES" --> node
    }
    val nodeMap = Map(nodelist map {s => (s.getProperty("id"), s)} : _*)
    
      nodeMap
  }
    
    like_list.foreach { x => {
      println(x.getName + "" + x.getId)
      fetch_likes_graph(nodeMap(x.getId), fbClient,count-1)
      } 
    } 
     println("*************End****************")
     println()
     startNode
}

def print_graph(startNode:Node): Integer = {
  withTx {
        implicit neo => 
         
              
  val erg1 = startNode.doTraverse[NodeBase](follow ->- "LIKES") {
    case _ => false
  } {
    case (x: GraphNode, tp) if (tp.depth == 1) => x.name.length > 5
  }.toList.sortWith(_.name< _.name)
   println("Relations LIKES, sorted by name and depth == 1: " + erg1)
   
   val node_count = getAllNodes.size//ds.gds.getAllNodes.iterator().toList.size
   println("node amount: "+ node_count)
   node_count
  }
  
}

def findSimilarity(originalId:String, targetId:String, simSet:Array[String]) : Int = {
  if(simSet.contains(targetId))  0
 val sim = simSet :+ targetId
     
  
  val countVar = Array[Int]()
  val countSum = withTx {
        implicit neo => 
             
    val query = "MATCH (me { id: '"+originalId+"' })-[r1:LIKES]->(like)<-[r2:LIKES]-(person { id: '"+targetId+"' })"+
                "WITH me,count(DISTINCT r1) AS H1,count(DISTINCT r2) AS H2,person "+            
                "MATCH (me)-[r1:LIKES]->(like)<-[r2:LIKES]-(person) "+
                "RETURN like, (H1+H2)/2 AS similarity"
    val typedResult = query.execute
    if(!typedResult.hasNext) {
      println("No Same Like")
      
     // simSet.toArray
      
       0
    }
    typedResult.toIterator
    
    while(typedResult.hasNext){
      val result = typedResult.next()
      val likes = result.get("like").iterator
      if(countVar.length == 0 ) {
        val sim = result.get("similarity").get.toString();
      val a =  countVar :+ Integer.valueOf(sim).toInt
        println("Similarity Count: " + sim)
      }
      val count = {
        val node = likes.next().asInstanceOf[Node]
        println(node.getProperty("name")) 
        val newTargetId = node.getProperty("id").toString()
        val simcount = {
          if(originalId == newTargetId) {0}
          else {findSimilarity(originalId,newTargetId,sim)}
        }
        simcount
      }
   val b =  countVar :+ count
      println(countVar)
    }
    println("test")
    val array = countVar.toArray
    println("test1")
    val sum = array.reduce(_+_)
    println("test3")
    println(sum)
    sum
  }
//   countVar = countTx
//   countVar
  //(countTx)
  println("test2")
  countSum
}


def createGraph(id:String, name:String, fbClient: DefaultFacebookClient): Unit = {

  val node = withTx {
    implicit neo => 
      if(id == "me") {
        val meNode = createNode(GraphNode(name,id,"topic"),id)
        meNode
        }else{
          val query = "MATCH(node { id: '"+id+"' }) RETURN node"
          val typedResult = query.execute
          if(!typedResult.hasNext) {
            println("No Node Found")
            val newNode = createNode(GraphNode(name,id,"topic"),id)
            newNode
          }else{
            val result = if(typedResult.hasNext){
              val node = typedResult.next().asInstanceOf[Node]
              node
            }
           result.asInstanceOf[Node]}
      }
  }
  
  val startNode = fetch_likes_graph(node, fbClient, 3)

  val nodeAmount = print_graph(startNode)  

  }

def getSimCount(originalId:String, targetId:String) : Int = {
  
 val simSet = Array[String]()
  
 val sim = simSet:+""
 
 
 val sim1 = simSet :+ originalId
  
  val count = findSimilarity(originalId,targetId,sim1)
  
  println("findSimilarity "+ count)
  
  count
  
}
  
/*
override def main(args: Array[String]): Unit = {
    

  //val startNode = fetch_likes_graph(null, fbClient, 3)
  
  //val nodeAmount = print_graph(startNode)
  
  val originalId = "185923878109545"
  val targetId = "249113725102662"
  simSet = Set("null")
  simSet += originalId
  val count = findSimilarity(originalId,targetId) //635
  println("findSimilarity "+ count)

  }*/
}
