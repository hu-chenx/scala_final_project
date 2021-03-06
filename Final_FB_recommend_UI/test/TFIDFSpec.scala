
import org.scalatest._
import com.restfb.DefaultFacebookClient
import org.apache.spark.mllib.linalg.{ SparseVector => SV }
import breeze.linalg._
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._


class TFIDFSpec extends FlatSpec{
  
   val accessToken = "EAACEdEose0cBAPm8dFIqEjaiFlK2xlu2aWEK0pebuthkWRzdrZCyqX3aiai52YY46YZCNnZBl16EYSrKVbBysUtckmJoqqknv90iYpT5B09ZBJirFo4ePqzd3q97Tgvtk5xDMtF1wGxZA7Lus9X6U4ZB3fsdYCiGwFZCy0VJSFLgwZDZD"
   
   val fbClient = new DefaultFacebookClient(accessToken)
   val filePath = "/home/chenxi/Documents/bbb"
   val id = "me"

   val topArray = TFIDF.getTfidf(fbClient,filePath,id)
   
   
   """ratio for tf of "celtics" and "heat"""" should "2.0" in {
    
     
     val outcome = tf_idf_imp.GetTF(List("celtics","celtics","celtics","celtics","heat","heat","warrior"))
     val m_1 = outcome.get("celtics")
     val m_2 = outcome.get("heat")
     val ratio = for {
       
                       r_1  <-  m_1
       
                       r_2  <-  m_2
                       
                     }yield r_1/r_2
                     
       assert(ratio.getOrElse(0.0) ===  2.0)
    
    
   }
   
 
   
    """words with most tf"""" should "be celtics" in {
    
     
     val omp = tf_idf_imp.GetTF(List("celtics","celtics","celtics","celtics","heat","heat","warrior"))
     val om = omp.reduce((m1,m2) => if(m1._2 >= m2._2) m1 else m2)
     assert(om._1 === "celtics")
    
    
   }
   
   
   
   
   "key word in sample file " should "be duang, ramen" in {
   // val topArray = TFIDF.getTfidf(fbClient,filePath,id)
    assert(topArray.head._1 ===  "duang")
    assert(topArray.tail.head._1 ===  "ramen")
    
   }
   
   
   
   "tf idf value test of sample file" should "get value pre-calcualted" in {
  //  val topArray = TFIDF.getTfidf(fbClient,filePath,id)
    assert(topArray.head._2 ===  0.1857547722785421)
    assert(topArray.tail.head._2 ===  0.0914453359337557)
    
   }
    

   
    "calculate similarity " should "get 1.0 when compared with same vector" in {
 
      System.setProperty("hadoop.home.dir", "/usr/local/bin/hadoop")
      val conf = new SparkConf().setMaster("local").setAppName("td").set("spark.executor.memory","4g") 
      val sc = new SparkContext(conf)
      val rdd = sc.wholeTextFiles(filePath)
      val text = rdd.map{case (file,text) => text}
      val c = tf_idf_imp.ExtractFeatures(text).map { x => ("a",new SparseVector(x.indices,x.values,x.size)) }
      val sample = c.first()._2
      val d = tf_idf_imp.extract_associative_user_rdd(c,sample)
      sc.stop()
      assert(d.head._1 == 1.0)
    
    }
    
    
    
    
    
    "tokenizing line: warrior win 73 games this season " should "warrior,win,games,season" in {
 
     val line = "warrior win 73 games this season" 
    
     val ts = tf_idf_imp.tokenize(line);
    
     assert(ts.mkString(",") === "warrior,win,games,season")
    
    }
    
    
    
  
     
     "tokenizing line: the and to a for of s in you on graph" should "get graph" in {
 
     val line = "the and to a for of s in you on graph" 
    
     val ts = tf_idf_imp.tokenize(line);
    
     assert(ts.mkString(",") === "graph")
    
    }
    
   
   
     
     "tokenizing line: the,and,to,a,graph,theory" should "get graph,theory" in {
 
     val line = "the,and,to,a,graph,theory" 
    
     val ts = tf_idf_imp.tokenize(line);
    
     assert(ts.mkString(",") === "graph,theory")
    
    }
     
     
     
     "tokenizing line: the+and@to!a,graph:theory" should "get graph,theory" in {
        val line = "the+and,to!a,graph:theory" 
    
        val ts = tf_idf_imp.tokenize(line);
    
        assert(ts.mkString(",") === "graph,theory")
     }
     
    
     
     "fetch user name from facebook " should """be "Chenxi Hu"  """ in {
       
       
       val fbn = fb_recommend.fetchUser(id, fbClient)
       assert(fbn === "Chenxi Hu")
       
     }
   
}