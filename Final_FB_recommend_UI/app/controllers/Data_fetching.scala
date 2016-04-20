package controllers



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
import akka.actor.ActorSystem
import akka.actor.ActorContext
import akka.actor.Props
import akka.actor.Actor.Receive
import akka.actor.DeadLetter
import model._

object Data_fetching {
    
val accessToken = "EAACEdEose0cBAPsyLgRCQHgmaPLZA1SU02R9LDZBNZArJiabMqK11OXtsGN8ZCZAStyZB7S84YACGRl7pZBQBbx7WvnmfbmrIG3grRWZANWkO5ZBKIk8ZA4VAbzEYVMcbwk1BoPaCO17pOCt1KHDbwOKvoBf0NX2QDzGT2vNUEqTlycgZDZD"




val fbClient = new DefaultFacebookClient(accessToken)

  
val system = ActorSystem("fb_fetch_system")
  
 val m_actor = system.actorOf(Props(new MainActor(fbClient)),name = "m_actor")
    

 val listener = system.actorOf(Props(new Listener(m_actor)), name="l_actor")
  
  system.eventStream.subscribe(listener, classOf[DeadLetter])
  
  
  
  
  
  
  def fetch_data() = {
  
  val result_1 = fbClient.fetchConnection("me/likes", classOf[Page])

  val me_list_page = result_1.toIterator.toList

  val like_list = for{
      
              mlp <- me_list_page
      
              ml  <- mlp
              
    }yield ml.getId

  
    
    
     like_list.foreach { x =>  m_actor ! x}
                         
                         
                         
                         
  }
  
  
  def stop_fetch() = {
      
      
  system.stop(m_actor)
  
  system.stop(listener)
  
  system.shutdown()
      
  }
    
    
}