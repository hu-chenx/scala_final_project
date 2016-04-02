package neu.edu.scala

import facebook4j.Facebook
import facebook4j.FacebookFactory
import facebook4j.auth.AccessToken
import java.util.Date


object test_fb {
  

  val accessToken = "EAAOjG469TEoBAIJLlyq9YFG9E4mNWIP6aLXN1TaXBUjX0PQxgAXY8hmtOWYZCZCTk1xkbi5P7lVgO0DZB04QEhiC2FT9vVymeF1euQ4B8L6z9fYwZB5jiFDoMLdjvyGUvdiazPMCe7uFntCNMETCsG8reMsnOsO7mOcbsnhBfQZDZD"
    
  val facebook  = new FacebookFactory().getInstance()
  
  facebook.setOAuthAppId("1023763684346954", "de490b66bfb7092632d85447703052a1");
  
  facebook.setOAuthAccessToken(new AccessToken(accessToken, null));
  
  val extendedToken = facebook.extendTokenExpiration(accessToken);
  
  def main(args: Array[String]): Unit = {
    println(extendedToken.getToken)
    println(extendedToken.getExpires())
  }
  
}

