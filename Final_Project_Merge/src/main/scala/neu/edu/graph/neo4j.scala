
import sys.ShutdownHookThread
import eu.fakod.neo4jscala.{TypedTraverser, SingletonEmbeddedGraphDatabaseServiceProvider, Neo4jWrapper}
import com.restfb.json.JsonObject
import com.restfb.DefaultFacebookClient
import com.restfb.FacebookClient
import com.restfb.types.Post
import com.restfb.types.Likes
import com.restfb.types.User
import com.restfb.types.Page
import com.restfb.Parameter
import com.restfb.Connection

trait MatrixBase {
  val name: String
  //val id: String
  //val user: 
  val topic: String
}

case class Matrix(name: String,topic: String) extends MatrixBase

case class NonMatrix(name: String,topic: String) extends MatrixBase

/**
 * The Matrix Example
 * http://wiki.neo4j.org/content/The_Matrix
 */

object neo4j extends App with Neo4jWrapper with SingletonEmbeddedGraphDatabaseServiceProvider with TypedTraverser {

  
  ShutdownHookThread {
    shutdown(ds)
  }


  def neo4jStoreDir = "/home/chenxi/Documents/graph_1"

  /**
   * defining nodes
   */
  final val nodes = Map("Neo" -> "Hacker",
    "Morpheus" -> "Hacker",
    "Trinity" -> "Hacker",
    "Cypher" -> "Hacker",
    "Agent Smith" -> "Program",
    "The Architect" -> "Whatever")

  /**
   * creating nodes and associations
   */
  val nodeMap = withTx {
    implicit neo =>
      val nodeMap = for ((name, prof) <- nodes) yield (name, createNode(Matrix(name, prof)))

      nodeMap("Neo") --> "KNOWS" --> nodeMap("Trinity")
      nodeMap("Neo") --> "KNOWS" --> nodeMap("Morpheus") --> "KNOWS" --> nodeMap("Trinity")
      nodeMap("Morpheus") --> "KNOWS" --> nodeMap("Cypher") --> "KNOWS" --> nodeMap("Agent Smith")
      nodeMap("Agent Smith") --> "CODED_BY" --> nodeMap("The Architect")
      nodeMap
  }

  // list of Nodes of type: List[Node]
  val startNodes = nodeMap("Neo") :: nodeMap("Morpheus") :: nodeMap("Trinity") :: Nil


  /**
   * traverse starting with all Nodes in startNodes, returning:
   * - direction and type (Incoming "KNOWS")
   * - all Nodes assignable to type MatrixBase
   * - all of type Matrix and depth==1 and MatrixBase.name.length > 2
   * - none of type NonMatrix
   *
   * The resulting List is sorted by name
   *
   */

  val erg1 = startNodes.doTraverse[MatrixBase](follow -<- "KNOWS") {
    case _ => false
  } {
    case (x: Matrix, tp) if (tp.depth == 1) => x.name.length > 2
    case (x: NonMatrix, _) => false
  }.toList.sortWith(_.name < _.name)

  println("Relations KNOWS, sorted by name and depth == 1: " + erg1)
}