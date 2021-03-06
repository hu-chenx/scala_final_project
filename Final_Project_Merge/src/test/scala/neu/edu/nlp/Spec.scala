package neu.edu.nlp
import org.scalatest._
import com.restfb.DefaultFacebookClient
import org.apache.spark.mllib.linalg.{ SparseVector => SV }
import breeze.linalg._
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._


class Spec extends FlatSpec{
  
//   val accessToken = "EAACEdEose0cBAGb0NRYvBwKvTqXxZARTZBfPhW2N9Lz1qySJgU3TfTTQfD3yWPZA8AO4hZBj2xmykDrxKBvvm4DLOTirJ8wwYXgvhp98PRXrCZBXfXoR01ctFkO7RqaBLGMtZCxm2PEdFrduWkO3BFqB9gmmiXU1GXCFkAZBm1qiAZDZD"
//   
//     
//   val fb = new DefaultFacebookClient(accessToken)
   val filePath = "/home/chenxi/Documents/ddd"
   val id = "me"

   
   
//  System.setProperty("hadoop.home.dir", "/usr/local/bin/hadoop")
//    val conf_1 = new SparkConf().setMaster("local").setAppName("td_1").set("spark.executor.memory","4g").set("spark.driver.allowMultipleContexts", "true") 
//    val sc = new SparkContext(conf_1)
   
   
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
   
   
   
   

    

   
    "calculate similarity " should "get 1.0 when compared with same vector" in {
 
      
      System.setProperty("hadoop.home.dir", "/usr/local/bin/hadoop")
      val conf_3 = new SparkConf().setMaster("local").setAppName("td_3").set("spark.executor.memory","4g").set("spark.driver.allowMultipleContexts", "true")
      val sc = new SparkContext(conf_3)
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
     
    
     

   
}