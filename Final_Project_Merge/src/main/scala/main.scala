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
import neu.edu.scala.actors.MainActor
import neu.edu.nlp._
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

/**
 * @author yangnan
 */
object main {
    
  def main(args: Array[String]): Unit = {
      
    // login & authentication  
    val accessToken = "EAACEdEose0cBAHwMm4MlJmR8VsDDGSMnTAq1ZB7jAdA1mDMCymWKifJ79ZAKJLsKvmzbFITZAtMM1npvPdnPzqPNuqfD0bERhgG5ChZCVTs3IKnZBU8PZC7yEtzIZCzT9wi6tKZARAFAWVs3q4ZASZCJxL1TFQge8Es8xfLoMZCdIG8CwZDZD"
    
    
    
    val fbClient = new DefaultFacebookClient(accessToken)
   
    
    // loading file if folder is empty
    val filePath = "/home/chenxi/Documents/fb_output"
    val fileData = new File(filePath);   
    if(!(fileData.isDirectory() && fileData.list().length > 1)) loadingData(fbClient) // in case of .DSstore file
    
    
    System.setProperty("hadoop.home.dir", "/usr/local/bin/hadoop")
  
    val conf = new SparkConf().setMaster("local").setAppName("td").set("spark.executor.memory","4g").set("spark.driver.allowMultipleContexts", "true") 
  
    val sc = new SparkContext(conf)
    
    // analyse & get similary rank
    val id = "me"
    val topArray = TFIDF.tfidf(id,sc,fbClient,filePath)
    
   // topArray.foreach(println)
    
    val scoreArray = topArray.map { x => x._1}
    val idArray = topArray.map { x => x._2}
    
    // generate graph
    val name = "name"
   // likes_graph.createGraph(id, name, fbClient)
    
    //get similarity count from graph
    val idCountArray = idArray.map { x => likes_graph.getSimCount(id,x)+1 }

    

   
    val toplist = for 
                   {
      
      
                        idc  <-  idCountArray
       
                         top   <- topArray
      
                         val a = (parseDouble(top._1))
                       
                         val weightedScore = a.get * idc
      
                          val tuple = (weightedScore,top._2)
                          
                    } yield tuple
      
              
                val sublist = topArray.filter{ p => {parseDouble(p._1) == None}}
                    
                    
                    
                    
    toplist.toList.sortWith(_._1 > _._1)

    
    val out_list = (toplist++sublist).map(f => f._2)
    
   
    
    
    val out_tup = (out_list.map { x => (x,fb_recommend.fetchUser(id, fbClient)) }).toList
    toplist.foreach(println)
    sublist.foreach(println)
    
    sc.stop()
    
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