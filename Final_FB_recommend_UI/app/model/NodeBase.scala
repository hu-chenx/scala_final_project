package model
/**
 * @author yangnan
 */
import com.restfb.types.Page

abstract class NodeBase {
  val name : String
  val id : String
  val topic: String
}

//case class GraphNode(name : String, id : String,topic: String) extends NodeBase
case class GraphNode(name : String, id : String,topic: String) extends NodeBase