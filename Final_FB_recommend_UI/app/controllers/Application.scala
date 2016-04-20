package controllers

import play.api.mvc._
import com.restfb.DefaultFacebookClient
import com.restfb.FacebookClient
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
import model._
import play.api.data.Form
import play.api.data.Forms._
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf


object Application extends Controller {
    
    
      val accessToken = "EAACEdEose0cBAPsyLgRCQHgmaPLZA1SU02R9LDZBNZArJiabMqK11OXtsGN8ZCZAStyZB7S84YACGRl7pZBQBbx7WvnmfbmrIG3grRWZANWkO5ZBKIk8ZA4VAbzEYVMcbwk1BoPaCO17pOCt1KHDbwOKvoBf0NX2QDzGT2vNUEqTlycgZDZD"
   
   
    val fbClient = new DefaultFacebookClient(accessToken) 
  
  val filePath = "/home/chenxi/Documents/fb_output"

  def index = Action {
    Ok(views.html.index(""))
    //Ok(views.html.neo_query())
  }


def start = Action {
    
    
    Data_fetching.fetch_data()
    Ok(views.html.data_loading())
    
}



def stop = Action {
    
    Data_fetching.stop_fetch()
    
    Ok(views.html.index("duang"))
    
}






def tfidf = Action { implicit request =>

//   val accessToken = "EAACEdEose0cBAMZBdqgqVbywZArkwajdBL96LZB5UpOlKCSpXkcS5Y19hZBf7aGNRLLHAHVAK9tnqOb0UGKpjPDKXg2VjhrBqPYlMO4tu2fPeUxjL9lNtDjMUJBNYRZBkqqLJg5e44aJxvXpDJRjQaCmG1wi1HZAmCeOponnVVjQZDZD"
    
//     val fbClient = new DefaultFacebookClient(accessToken) 

System.setProperty("hadoop.home.dir", "/usr/local/bin/hadoop")
  
    val conf = new SparkConf().setMaster("local").setAppName("td").set("spark.executor.memory","4g") 
  
    val sc = new SparkContext(conf)



  
// val filePath = "/home/chenxi/Documents/fb_output"

val id = Map_id.bindFromRequest().get.id

val topArray = TFIDF.getTfidf(fbClient,filePath,id,sc)

 sc.stop()

val output_id = graph_analysis.g_analysis(topArray,fbClient,id)

    
    val output_tup = (output_id.map { x => (x,fb_recommend.fetchUser(x, fbClient)) }).toList
    
    
   
    
    Ok(views.html.output(output_tup))
    
}


def parseDouble(s: String) = try { Some(s.toDouble) } catch { case _ => None }

// def graph = Action { implicit request =>
    
   
//   val id = Map_id.bindFromRequest().get.id
//   val topArray = Array(("Zara", "Nuha"), ("Ayan","a"))
//     val output_id = graph_analysis.g_analysis(topArray,fbClient,id)
//     Ok(views.html.index("graph is ready"))
    
// }



  def Map_id: Form[ID] =Form {
    mapping (
      "id" ->text
    )(ID.apply)(ID.unapply)
  }
  
  
  
  
  def Map_kw: Form[KW] =Form {
    mapping (
      "kw" ->text
    )(KW.apply)(KW.unapply)
  }
  
  
  
  
  
  def kw_User = Action { implicit request => 
      
  
      val kw = Map_kw.bindFromRequest().get.kw
      
      System.setProperty("hadoop.home.dir", "/usr/local/bin/hadoop")
  
    val conf = new SparkConf().setMaster("local").setAppName("td").set("spark.executor.memory","4g") 
  
    val sc = new SparkContext(conf)
    
        val a = TFIDF.get_user_kw(kw, sc, fbClient, filePath)
        
        sc.stop()
        
       val b = (a.map { x => (fb_recommend.fetchOfficial(x._2, fbClient),fb_recommend.fetchUser(x._2, fbClient)) }).toList
        
        
        
        if(b.size == 0)
        {
          Ok(views.html.no_result("no search result"))
        }else{
        
        Ok(views.html.output(b))
        }
  }
  
  
  
  def graph  = Action {
      //likes_graph.getJSON
      Ok(views.html.graph())
  }
}
