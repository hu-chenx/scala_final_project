package model

import akka.actor.{ Actor, DeadLetter, Props }
import akka.actor.ActorRef
 
class Listener(a:ActorRef) extends Actor {
  def receive:Receive = {
    case d: DeadLetter =>   println(d); a!d      ;
  }
}