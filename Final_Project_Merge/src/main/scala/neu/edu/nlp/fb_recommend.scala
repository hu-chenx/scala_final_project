package neu.edu.scala

import com.restfb.DefaultFacebookClient
import com.restfb.types.Post
import com.restfb.types.User
import java.util.{Iterator => JIterator}
import scala.collection.JavaConversions._
import com.restfb.types.Post
import com.restfb.types.User
import java.util.{Iterator => JIterator}
import com.restfb.types.Post
import com.restfb.types.User
import java.util.{Iterator => JIterator}





class fb_recommend(id : String, fbClient:DefaultFacebookClient) {
 
 val result_1 = fb_recommend.getClient(fbClient,id)

}

object fb_recommend{
  
  
  def getClient(fbClient : DefaultFacebookClient, id : String) = {
    
    fbClient.fetchConnection(id+"/posts", classOf[Post])
    
  }
  
  
   
  def scalaIterator[T](it: JIterator[T]) = new Iterator[T] {
  override def hasNext = it.hasNext
  override def next() = it.next()
} 
  
  
  def apply(id : String,fbClient : DefaultFacebookClient) = {
    
    new fb_recommend(id,fbClient)
    
  }
  
  
  
  def fetchUser(id : String, fbClient : DefaultFacebookClient) = {
    
    val user =   fbClient.fetchObject(id, classOf[User])
    
    user.getName()
    
  }

  def getPost(me_list_post :  List[java.util.List[Post]]) = {

    me_list_post
    
  }
  

}