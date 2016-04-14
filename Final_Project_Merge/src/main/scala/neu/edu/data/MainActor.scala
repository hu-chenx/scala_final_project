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




class MainActor(fbClient:DefaultFacebookClient) extends Actor{
  
  val WriteActor:ActorRef = context.actorOf(Props[WriteActor],name="x_actor")
  
  val fetchPostActor:ActorRef = context.actorOf(Props(new FetchPostActor(WriteActor,fbClient)),name="fp_actor")
  
  val fetchLikeActor:ActorRef = context.actorOf(Props(new FetchLikesActor(fetchPostActor,fbClient)),name = "fl_actor") 
  
 def receive:Receive ={
   
   case msg : List[String] =>  {msg.foreach { x =>  fetchLikeActor ! x}}
   
   case msg : String => fetchLikeActor ! msg
   
 }
  
}