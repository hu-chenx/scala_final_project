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

class FetchPostActor(writeActor : ActorRef) extends  Actor{
  
  def receive:Receive  = {
    
    case msg:String => 

   // writeActor ! "begin"
      
      writeActor ! new Id_number(msg)
      
      fetch_Post(msg)
      
      
    
  }
  
  
  def fetch_Post(id:String) = {
    
     val accessToken = "EAACEdEose0cBADjIzMeDgZCmT5AuQI90q1VcxgzghGaZCrIYizUDn3mqC1sLe1GIZAB7PIohkbIex0m8hzsvjmulZA3IQLAfXO9Vzg82H2wxbC6NbsoinbBiuRBj1MJOlgJB6cQWksDwF13kengjihzOccg6kF9Jmu73hh9pHAZDZD"
       
       
     val fbClient = new DefaultFacebookClient(accessToken)
  
     val result = fbClient.fetchConnection(id+"/posts", classOf[Post])
     
//      val mapper = new ObjectMapper();
//  mapper.writeValue(new File(  """D:\\fb_output\"""+id+".json"     ), result)
     
     var d= new StringBuilder
     
      val rt = Future(result.toList)
      
      val a = rt.map { x => x.flatten }
     
     val b = a.map { x => x.map { x => x.getMessage } }
     
     b.onComplete {
       
       case Success(o) => writeActor ! o
//                          val f = new File("""D://fb_output/"""+id.toString()+".txt")
//                          val fw = new FileWriter(f.getAbsoluteFile());
//                      		 val bw = new BufferedWriter(fw);
//                       		bw.write(rt.toString());
//                     			bw.close();
       
       case Failure(ex) => println("duang: "+ex)
       
     }
     
     
     
     
     
//      val rt = result.toIterator
//    
//  
//   
//   
//  
//   val la =Future.traverse(rt.toTraversable){i => 
//    
//   val a = Future(i.toList)
//     
//   val b = a.map { x => x.map { x => (x.getMessage+"\n") } }
//   
//   
//   val c = b.map { x => x.foreach { x => d.++=(x) }}
   
//   b.onComplete { 
//     
//     
//      case Success(o) => val f = new File("""D://fb_output/"""+id.toString()+".txt")
//                          val fw = new FileWriter(f.getAbsoluteFile());
//                      		 val bw = new BufferedWriter(fw);
//                      		 
//                     		 if (f.exists()) {
//				                                          bw.append(o.toString())
//			                                          }else{
//                      		 
//                       		bw.write(o.toString());
//			                                          }
//                     			bw.close();
//     
//       case Failure(ex) => println(ex+" duang") 
//     
//   }
   
   
  
//   
//   b
//  } 
  
//     la.onComplete { 
//       
//       
//       case Success(o) => val f = new File("""D://fb_output/"""+id.toString()+".txt")
//                          val fw = new FileWriter(f.getAbsoluteFile());
//                      		 val bw = new BufferedWriter(fw);
//                       		bw.write(d.toString());
//                     			bw.close();
//     
//       case Failure(ex) => println(ex+" duang") 
//     
//       
//       
//     }
     
     
     
  }
  
}