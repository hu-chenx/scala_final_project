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

import com.fasterxml.jackson.databind.ObjectMapper


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
     
     case Success(o) => o.foreach { println }
     
     case Failure(ex) => println(ex+" duang") 
     
   }
   
   b
  } 
  
  

  
}






def fetch_Post( result : Connection[Post]) = {
  
  val mapper = new ObjectMapper();
  mapper.writeValue(new File("D:\\result.json"), result)
  
 //  val rt = result.toIterator
    
  
   
   
  
//  val la =Future.traverse(rt.toTraversable){i => 
//    
//   val a = Future(i.toList)
//     
//    val b = a.map { x => x.map { x => x.getMessage } }
//   
//   
//
//   b.onComplete { 
//     
//     case Success(o) => o.foreach { println }
//     
//     case Failure(ex) => println(ex+" duang") 
//     
//   }
//   
//   b
//  } 
  
  
}
  

  
def main(args: Array[String]): Unit = {
    
    
  val accessToken = "CAACEdEose0cBAHPQZBvm9WvP5rtPurmb9sJUdtYIZAZBcaLLB1yt5wI74F8EHZAUX5z98dHL8Jrk0j17n3GE6k0IBRUyBRgtjYZBJLGdddBClFZBs83X16ZCrFPq8zL1ERR37B95OZADtFZBBq9OCYSSeoD3MBuHPZBEeVV2rt7lM96NEkIBdZBEUarCWmSFCIhauad9f3fdw5fOm3ZA6XmZAYDBC"
    
    
    
 val fbClient = new DefaultFacebookClient(accessToken)
  
 
 val result = fbClient.fetchConnection("64637653943/posts", classOf[Post])



   
  fetch_Post(result)
  
  

  
// val result_1 = fbClient.fetchConnection("me/likes", classOf[Page])
//
//val me_list_page = result_1.toIterator.toList
//
//  val like_list = for{
//      
//              mlp <- me_list_page
//      
//              ml  <- mlp
//              
//    }yield ml.getId
//
//  
//    
//    
//     like_list.foreach { x =>  val r = fbClient.fetchConnection(x+"/likes", classOf[Page]) 
//      
//                                   fetch_likes(r)
//    
//                         }
    
    

 
  
  
  
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