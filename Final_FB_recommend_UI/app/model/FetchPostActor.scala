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
import com.restfb.types.Post
import com.restfb.Connection
import scala.collection.JavaConversions._
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.ActorContext
import akka.actor.Props
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.io.File
import java.io.FileWriter
import java.io.BufferedWriter

class FetchPostActor(writeActor : ActorRef,fbClient:DefaultFacebookClient) extends  Actor{
  
  def receive:Receive  = {
    
    case msg:String => 

      writeActor ! new Id_number(msg)
      
      fetch_Post(msg,fbClient)
      
      
    
  }
  
  
  def fetch_Post(id:String,fbClient:DefaultFacebookClient) = {
  
    println(id)
    
     val result = fbClient.fetchConnection(id+"/posts", classOf[Post])
     
      val rt = Future(result.toList)
      
      val a = rt.map { x => x.flatten }
     
     val b = a.map { x => x.map { x => x.getMessage } }
     
     b.onComplete {
       
       case Success(o) => writeActor ! o
       
       case Failure(ex) => println("duang: "+ex)
       
     }
     
  }
  
}