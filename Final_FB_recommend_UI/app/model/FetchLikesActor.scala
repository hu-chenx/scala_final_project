package model
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



class FetchLikesActor( fetchPostActor:ActorRef,fbClient:DefaultFacebookClient) extends  Actor{
  
  def receive:Receive  = {
    
    case msg:String => 
      
      fetchPostActor ! msg
    
      fetch_likes(msg, fbClient)
    
  }
  
  
  def fetch_likes(id:String, fbClient:DefaultFacebookClient) = {
    
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