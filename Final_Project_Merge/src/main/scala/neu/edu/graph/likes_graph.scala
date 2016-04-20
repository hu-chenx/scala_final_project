
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

  def neo4jStoreDir = "/home/chenxi/Documents/graph"
  //val simSet = Set("null")
  
def fetch_likes(id:String, fbClient: DefaultFacebookClient, count:Integer):Boolean = {
  
  val result_1 = fbClient.fetchConnection("me/likes", classOf[Page])
  val me_list_page = result_1.toIterator.toList  
  val like_list = for{
              mlp <- me_list_page      
              ml  <- mlp              
    }yield ml

    like_list.foreach { x => {
      println(x.getName)
      
      fetch_likes(x.getId, fbClient,count-1)
      } 
    } 
 
    
    true
}
  
  
  
  
  
  
  

def fetch_likes_graph(user:Node, fbClient: DefaultFacebookClient, count:Integer):Node = {
 
  val result = Option(user) match {
    
    case None => fbClient.fetchConnection("me/likes", classOf[Page])
    
    case _ => withTx {
        implicit neo => fbClient.fetchConnection(user.getProperty("id").toString()+"/likes", classOf[Page])    
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
      implicit neo => val start = Option(user) match {
        
        case None => createNode("start")
        case _ => user
      }
      start
    }

  val nodeMap = withTx {
    implicit neo => 
      
   
      
      val nodelist = for {
                            n <- like_list
                                
                            val result = getAllNodesWithLabel(n.getId).iterator
                            
                            val node = Option(result.next()) match {
                              
                              case None => createNode(GraphNode(n.getName,n.getId,"topic"),n.getId)
                              
                              case Some(op) => op
                              
                            
                                     }
                       
                         
                         }yield node
      
      
      
      
      for(n <- like_list){
      val node = {
        
         val result = getAllNodesWithLabel(n.getId).iterator
         val op_node = Option(result.next()) match {
                              
                              case None => createNode(GraphNode(n.getName,n.getId,"topic"),n.getId)
                              
                              case Some(op) => op
                              
                            
                                     }
         op_node
         
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
   
   val node_count = getAllNodes.size
   println("node amount: "+ node_count)
   node_count
  
  }
  
}








def findSimilarity(originalId:String, targetId:String, simSet:Array[String]) : Int = {
  if(simSet.contains(targetId))  {0}
 val sims = simSet :+ targetId
     
 
 val query = "MATCH (me { id: '"+originalId+"' })-[r1:LIKES]->(like)<-[r2:LIKES]-(person { id: '"+targetId+"' })"+
                "WITH me,count(DISTINCT r1) AS H1,count(DISTINCT r2) AS H2,person "+            
                "MATCH (me)-[r1:LIKES]->(like)<-[r2:LIKES]-(person) "+
                "RETURN like, (H1+H2)/2 AS similarity"
    val typedResult = query.execute
    
    

  val countSum = Option(typedResult.next()) match {
   
   case None =>    withTx {
        implicit neo => 
  
    val countVar = for 
    {
      
      result <- typedResult.toIterator
      
       val likes = result.get("like").iterator
      
       val sim = result.get("similarity").get.toString();
      
      val a =   Integer.valueOf(sim).toInt
      
       val count = {
       val node = likes.toList.head.asInstanceOf[Node]
       val newTargetId = node.getProperty("id").toString()
        
       val simcount = originalId match 
          {
          
          case newTargetId => 0
          
          case _ => findSimilarity(originalId,newTargetId,sims)
          
          }
       
       simcount
      }
      
      
    }yield(List(a,count))

    val array = countVar.flatten.toArray

    val sum = array.foldLeft(1)(_+_)

    println(sum)
    sum
  }
     
   case _ =>   withTx {
        implicit neo => 0
     }
  }
 countSum
}









def createGraph(id:String, name:String, fbClient: DefaultFacebookClient): Unit = {

  val node = withTx {
    implicit neo => 
      
      
      
      val meNode = id match {
        
        case "me" => createNode(GraphNode(name,id,"topic"),id)
        
        
        case _ => {
                    
                    val query = "MATCH(node { id: '"+id+"' }) RETURN node"
                    val typedResult = query.execute
                    val result = Option(typedResult.next()) match {
                         
                         case None => createNode(GraphNode(name,id,"topic"),id)
            
                         case Some(re) => typedResult.next().asInstanceOf[Node]
         
                     }
          
          
                  result
          
                   }
                }
    meNode
   
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
  




}
