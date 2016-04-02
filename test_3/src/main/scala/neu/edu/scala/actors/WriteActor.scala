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




// val f = new File("D://test_fb.txt")
//  val fw = new FileWriter(f.getAbsoluteFile());
//			 val bw = new BufferedWriter(fw);
//			bw.write(d.toString());
//			bw.close();


class WriteActor extends Actor {
  
  val b= new StringBuilder
  
  val id = new StringBuilder
  
  def receive:Receive ={
    
  // case "begin" =>  b.clear();id.clear()
//   
//   case msg : String => b.++=(msg)
//     
  case msg : Id_number => id.clear(); id.++=(msg.id.toString());//println(msg.id )
   
   case msg : List[String] => 
     
                  println(id.toString())
                   val f = new File("""D://fb_output/"""+id.toString()+".txt");
                   val fw = new FileWriter(f.getAbsoluteFile());
			             val bw = new BufferedWriter(fw);
		              	bw.write(msg.toString());
		               	bw.close();           
   
 }
  
  
}