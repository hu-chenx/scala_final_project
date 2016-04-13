package neu.edu.scala

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

  def neo4jStoreDir = "/Users/yangnan/Downloads/tmp/temp-fb"
  var simSet = Set("null")
  
def fetch_likes(id:String, fbClient: DefaultFacebookClient, count:Integer):Boolean = {
  if(count == 0) return true
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
 
    return true
}

def fetch_likes_graph(user:Node, fbClient: DefaultFacebookClient, count:Integer):Node = {
  if(count == 0) return user
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
  //val nodes = Map(like_list map {s => (s, "topic")} : _*)
  //val nodes = Map(like_list map {s => (s, "topic")} : _*)
  //val nodes:Map[Page, String] = like_list.map(t => (t, "topic"))(breakOut)
  val nodeMap = withTx {
    implicit neo => 
      val nodelist = scala.collection.mutable.MutableList[Node]()
    //var nodelist : List[Node] = Nil
      for(n <- like_list){
      val node = {

         val result = getAllNodesWithLabel(n.getId).iterator
          if(result.hasNext){
            println(n.getName + n.getId)
            val existNode = result.next()
            nodelist += existNode
            //nodelist = nodelist.::(existNode)
            existNode
        }else{
          val newNode = createNode(GraphNode(n.getName,n.getId,"topic"),n.getId)
          nodelist += newNode
          //nodelist = nodelist.::(newNode)
          newNode
        }
      }
      startNode --> "LIKES" --> node
    }
    val nodeMap = Map(nodelist map {s => (s.getProperty("name"), s)} : _*)
      //val nodes = Map(like_list map {s => (s, "topic")} : _*)
      //val nodeMap = for ((userLike, topic) <- nodes) yield (userLike.getName, createNode(GraphNode(userLike.getName,userLike.getId,"topic")))  
      /*
    nodeMap.foreach{ _ match{
      case (name, node) => startNode --> "LIKES" --> node
      case _ => throw new Exception("graph")
    }
    }*/
      nodeMap
  }
    
    like_list.foreach { x => {
      println(x.getName + "" + x.getId)
      fetch_likes_graph(nodeMap(x.getName), fbClient,count-1)
      } 
    } 
     println("*************End****************")
     println()
    return startNode
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

def findSimilarity(originalId:String, targetId:String) : Int = {
  if(simSet.contains(targetId)) return 0
  simSet += targetId
  var countVar = 0
  val countTx = withTx {
        implicit neo => {
             
    val query = "MATCH (me { id: '"+originalId+"' })-[r1:LIKES]->(like)<-[r2:LIKES]-(person { id: '"+targetId+"' })"+
                "WITH me,count(DISTINCT r1) AS H1,count(DISTINCT r2) AS H2,person "+            
                "MATCH (me)-[r1:LIKES]->(like)<-[r2:LIKES]-(person) "+
                "RETURN like, (H1+H2)/2 AS similarity"
    val typedResult = query.execute
    if(!typedResult.hasNext) {
      println("No Same Like")
      return 0
    }
    
    
    val result = while(typedResult.hasNext){
      val result = typedResult.next()
      val likes = result.get("like").iterator
      if(countVar == 0 ) {
        val sim = result.get("similarity").get.toString();
        countVar += Integer.valueOf(sim).toInt
        println("Similarity Count: " + sim)
      }
      val count = {
        val node = likes.next().asInstanceOf[Node]
        println(node.getProperty("name")) 
        val newTargetId = node.getProperty("id").toString()
        val simcount = {
          if(originalId == newTargetId) return 0
          else findSimilarity(originalId,newTargetId)
        }
        simcount
      }
      countVar += count
    }
    println(countVar)
        }
        return countVar
  }
   countVar = countTx
   countVar
}
  

override def main(args: Array[String]): Unit = {
    
    
  //val accessToken = "CAACEdEose0cBAMsoDZBZCQPbHb4vLZBamCArUiY78rZCOx8SOB4sXGw5PWu0guqx1K654i0EfE9DQsJCO3FFkI3wkLn76jtbKUZCF681W2ubGwN2xrZCXCKDghC1XSnjvfAvwQAjc9TjvZBd07ZBtoCdC3bKClypZBWbFron0OyyHOFNHVSMBlja3VC2TA0q8Bd6jmBRczMbkwApokMLuA9j2"
  
  //val fbClient = new DefaultFacebookClient(accessToken)

  //val startNode = fetch_likes_graph(null, fbClient, 3)
  
  //val nodeAmount = print_graph(startNode)
  
  val originalId = "185923878109545"
  val targetId = "249113725102662"
  simSet = Set("null")
  simSet += originalId
  val count = findSimilarity(originalId,targetId) //635
  println("findSimilarity "+ count)

  }
}