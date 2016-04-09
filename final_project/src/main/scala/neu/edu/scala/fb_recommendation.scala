package neu.edu.scala
import com.restfb.DefaultFacebookClient
import com.restfb.FacebookClient
import com.restfb.types.Post
import com.restfb.types.Likes
import com.restfb.types.User
import java.util.{Iterator => JIterator}
import com.restfb.types.Page
import scala.collection.JavaConversions._





object fb_recommendation {
  
  
  
   val accessToken = "EAACEdEose0cBAJjZBZCxYSTrHYzEHKsq9sxbUMQBSeKE3t4C3sQ97Egl3mOuw5WA2av9ajySUds5qjzoa5CHwS3KRE1dfJJCYZCQi4QPGgEbTDR3TCoiDTX2qUVQK6Bxx7DQPzeACUICbUxjou0Ud9lCvqOzWPDziVirxsS3AZDZD"
     
 val fbClient = new DefaultFacebookClient(accessToken)
  
  
  
  def scalaIterator[T](it: JIterator[T]) = new Iterator[T] {
  override def hasNext = it.hasNext
  override def next() = it.next()
} 
  
//  def main(args: Array[String]): Unit = {
    
  val result_1 = fbClient.fetchConnection("358247354245303/posts", classOf[Post])
    
    val me_list_post = result_1.toIterator.toList
    
    
   val output=    for{
      
              mlp <- me_list_post
      
              ml  <- mlp
              
    }yield ml.getMessage
    

//  }
}