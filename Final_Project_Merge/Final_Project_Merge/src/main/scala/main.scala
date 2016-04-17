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
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.ActorContext
import akka.actor.Props
import akka.actor.Actor.Receive
import com.fasterxml.jackson.databind.ObjectMapper
import neu.edu.scala.actors.MainActor
import scala.util.parsing.json.JSONObject
import scala.util.parsing.json.JSONArray
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter
import neu.edu.nlp._

/**
 * @author yangnan
 */
object main {
    
  def main(args: Array[String]): Unit = {
      
    // login & authentication  
    val accessToken = "EAACEdEose0cBAMZBdqgqVbywZArkwajdBL96LZB5UpOlKCSpXkcS5Y19hZBf7aGNRLLHAHVAK9tnqOb0UGKpjPDKXg2VjhrBqPYlMO4tu2fPeUxjL9lNtDjMUJBNYRZBkqqLJg5e44aJxvXpDJRjQaCmG1wi1HZAmCeOponnVVjQZDZD"
      
    val fbClient = new DefaultFacebookClient(accessToken)
   
    
    // loading file if folder is empty
    val filePath = "/home/chenxi/Documents/fb_output"
    val fileData = new File(filePath);   
    if(!(fileData.isDirectory() && fileData.list().length > 1)) loadingData(fbClient) // in case of .DSstore file
    
    
    // analyse & get similary rank
    val id = "69025400418"
    val topArray = TFIDF.getTfidf(fbClient,filePath,id)
    
   // topArray.foreach(println)
    
    val scoreArray = topArray.map { x => x._1}
    val idArray = topArray.map { x => x._2}
    
    // generate graph
    val name = "name"
    likes_graph.createGraph(id, name, fbClient)
    
    //get similarity count from graph
    val idCountArray = idArray.map { x => likes_graph.getSimCount(id,x)+1 }
    //idCountArray.foreach(println)
    // get final similariy rank by combining two factors together
    val toplist = scala.collection.mutable.MutableList[(Double,String)]()
    val sublist = scala.collection.mutable.MutableList[(Any,String)]()
    for ( i <- 0 to (idCountArray.length - 1)) {
         if(parseDouble(topArray(i)._1) != None) {
           val score = parseDouble(topArray(i)._1).get
           val weightedScore = score.doubleValue() * idCountArray(i)
         //  println(weightedScore)
           val tuple = (weightedScore,topArray(i)._2)
           toplist += tuple
         }else{
           sublist += topArray(i)
         }
      }
    toplist.toList.sortWith(_._1 > _._1)

    
    val out_list = (toplist++sublist).map(f => f._2)
    
      //  out_list.foreach { x => println(x) }
    
    
    val out_tup = (out_list.map { x => (x,fb_recommend.fetchUser(id, fbClient)) }).toList
   // toplist.foreach(println)
   // sublist.foreach(println)
    
  }
  
  def loadingData(fbClient:DefaultFacebookClient){
    
    val system = ActorSystem("fbfetchsystem")
    
    val m_actor = system.actorOf(Props(new MainActor(fbClient)),name = "m_actor")
  
    val result_1 = fbClient.fetchConnection("me/likes", classOf[Page])
  
    val me_list_page = result_1.toIterator.toList
  
    val like_list = for{
        
                mlp <- me_list_page
        
                ml  <- mlp
                
      }yield ml.getId
  
      
       like_list.foreach { x =>  m_actor ! x  }
  }
  
  def parseDouble(s: String) = try { Some(s.toDouble) } catch { case _ => None }

}