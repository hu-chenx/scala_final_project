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




object test_2_fb {
  
 
  //  val facebook  = new FacebookFactory().getInstance()
//  
//  facebook.setOAuthAppId("1023763684346954", "de490b66bfb7092632d85447703052a1");
//  
//  facebook.setOAuthAccessToken(new AccessToken(accessToken, null));
//  
//  val feed = facebook.getHome
  
  



//def fetch( df_client : DefaultFacebookClient):Future[Connection[Post]] = Future {
//  
// 
//  println("aaaaaaaaaaaaaaaaaaa")
//  
//  df_client.fetchConnection("me/feed", classOf[Post])
//    
//}



def scalaIterator[T](it: JIterator[T]) = new Iterator[T] {
  override def hasNext = it.hasNext
  override def next() = it.next()
} 



def outputPost(co_Post :java.util.List[Post]):Future[List[Post]] = Future{
  
  
  //println(co_Post.length)
  co_Post.toList
  
  
  
}


def outputPages(page : java.util.List[Page]):Future[List[Page]] = Future{
  page.toList
}
  



def PageToPost(pa : List[Post]):Future[Post] = Future{
  
  //println(pa.toString()+"ssssssssss")
  pa.iterator().next()
  
  
}




def fetch_likes(result:Connection[Page]) = {
  
  
  
  
  val rt = result.toIterator
    

    
    
  
  val la =Future.traverse(rt.toTraversable){i => 
    
  val a = Future(i.toList)
     
  val b = a.map { x => x.map { x => x.getId } }
   
   
 
   b.onComplete { 
     
     case Success(o) => println( "length: "+o.length) ;o.foreach { println }
     
     case Failure(ex) => println(ex+" duang") 
     
   }
   
   b
  } 
  
  

  
}






def fetch_Post( result : Connection[Post]) = {
  
//  val mapper = new ObjectMapper();
//  mapper.writeValue(new File("D:\\result.json"), result)
  
//  var d= new StringBuilder
  
 val rt = result.toIterator
    
  
   
   
  
   val la =Future.traverse(rt.toTraversable){i => 
    
   val a = Future(i.toList)
     
   val b = a.map { x => x.map { x => (x.getMessage+"\n") } }
   
   

   b.onComplete { 
     
     case Success(o) => o.foreach { println }
     
     case Failure(ex) => println(ex+" duang") 
     
   }
   
   b
  } 
  
  
// val f = new File("D://test_fb.txt")
//  val fw = new FileWriter(f.getAbsoluteFile());
//			 val bw = new BufferedWriter(fw);
//			bw.write(d.toString());
//			bw.close();
  
}
  

  
def main(args: Array[String]): Unit = {
    
    
 val accessToken = "EAACEdEose0cBADjIzMeDgZCmT5AuQI90q1VcxgzghGaZCrIYizUDn3mqC1sLe1GIZAB7PIohkbIex0m8hzsvjmulZA3IQLAfXO9Vzg82H2wxbC6NbsoinbBiuRBj1MJOlgJB6cQWksDwF13kengjihzOccg6kF9Jmu73hh9pHAZDZD"
   
   
 val fbClient = new DefaultFacebookClient(accessToken)
  
 
 //val result = fbClient.fetchConnection("79979913992/posts", classOf[Post])


  //fetch_Post(result)
  
  val system = ActorSystem("fb_fetch_system")
  
  val m_actor = system.actorOf(Props[MainActor],name = "m_actor")

  
  
  val result_1 = fbClient.fetchConnection("me/likes", classOf[Page])

  val me_list_page = result_1.toIterator.toList

  val like_list = for{
      
              mlp <- me_list_page
      
              ml  <- mlp
              
    }yield ml.getId

  
    
    
     like_list.foreach { x =>  m_actor ! x
      
    
                         }
    
   


  
  
  
//  val it = (1 to 10).iterator   
//
//  val la = Future.traverse(it.toTraversable){i => 
//    
//
//  val a =  Future{10*i}
//  
//  a.onComplete { x => println(x.get) }
//  
//  a
//    
//  }  
//  
//  Await.result(la, Duration(100, "millis") )


  
  //Await.result(la, Duration(100, "millis") )
  
  

  
  
  
}
}