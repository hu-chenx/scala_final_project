package neu.edu.scala.actors
import akka.actor.ActorRef
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.ActorContext
import akka.actor.Props
import akka.actor.Actor.Receive
import com.restfb._
import scala.util.{Success, Failure}
import java.util.{Iterator => JIterator}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import com.restfb.types.Page
import com.restfb.Connection
import scala.collection.JavaConversions._



class FetchLikesActor( fetchPostActor :ActorRef) extends  Actor{
  
  def receive:Receive  = {
    
    case msg:String => 
      
      fetchPostActor ! msg
    
      fetch_likes(msg)
    
  }
  
  
  def fetch_likes(id:String) = {
    
      val accessToken = "EAACEdEose0cBADjIzMeDgZCmT5AuQI90q1VcxgzghGaZCrIYizUDn3mqC1sLe1GIZAB7PIohkbIex0m8hzsvjmulZA3IQLAfXO9Vzg82H2wxbC6NbsoinbBiuRBj1MJOlgJB6cQWksDwF13kengjihzOccg6kF9Jmu73hh9pHAZDZD"
        
        
        
      val fbClient = new DefaultFacebookClient(accessToken)
    
       val result = fbClient.fetchConnection(id+"/likes", classOf[Page])
      
      val rt = result.toIterator
      
      val la =Future.traverse(rt.toTraversable){i => 
    
      val a = Future(i.toList)
     
      val b = a.map { x => x.map { x => x.getId } }
   
   
 
      b.onComplete { 
     
       case Success(o) => sender ! o
     
       case Failure(ex) => println(ex+" duang") 
     
   }
   
   b
  } 
  
  
  }
  
  
  def scalaIterator[T](it: JIterator[T]) = new Iterator[T] {
  override def hasNext = it.hasNext
  override def next() = it.next()
} 
  
}