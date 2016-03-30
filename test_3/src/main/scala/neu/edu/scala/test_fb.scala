package neu.edu.scala

import facebook4j.Facebook
import facebook4j.FacebookFactory
import facebook4j.auth.AccessToken
import java.util.Date


object test_fb {
  

  val accessToken = "CAAOjG469TEoBANLV3rqcLDiMUlGouUmPRlbef8wFc40BIZCURZAm51y3Pjj8Wpbr9RzP8kYHCALsIiJfDqh09EjZCBPiDWOxfJT4Ola5ZCYvIQ0vXPRRs87iJvvZC7bZAu8mbCGpY9Dwjv9WTc2uEqnFyBjatPKeE4ratZBszmKznuO5W2eeuy2fJzqoJuE3bt9nFueuKf6ZAVhNr5X8VLDv"

    
    
  val facebook  = new FacebookFactory().getInstance()
  
  facebook.setOAuthAppId("1023763684346954", "de490b66bfb7092632d85447703052a1");
  
  facebook.setOAuthAccessToken(new AccessToken(accessToken, null));
  
  val extendedToken = facebook.extendTokenExpiration(accessToken);
  
  def main(args: Array[String]): Unit = {
    println(extendedToken.getToken)
    println(extendedToken.getExpires())
  }
  
}

