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
import scala.collection.mutable.StringBuilder
import java.io.File
import java.io._
import com.restfb.types.Post
import com.fasterxml.jackson.databind.ObjectMapper

class WriteActor extends Actor {
  
  val b= new StringBuilder
  
  val id = new StringBuilder
  
  def receive:Receive ={
    
  case msg : Id_number => id.clear(); id.++=(msg.id.toString());
   
  case msg : List[String] => 
     
                  println(id.toString())
                   val f = new File("/home/chenxi/Documents/bbb/"+id.toString()+".txt"); //Nan Modify folder path if change PC
                   val fw = new FileWriter(f.getAbsoluteFile());
			             val bw = new BufferedWriter(fw);
		              	bw.write(msg.toString());
		               	bw.close();           
   
 }

}