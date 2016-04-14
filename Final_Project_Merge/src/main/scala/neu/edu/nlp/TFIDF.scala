
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.{ SparseVector => SV }
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.feature.IDF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.classification.NaiveBayesModel
import scala.collection.JavaConversions._
import java.util.{Iterator => JIterator}
import com.restfb.types.Post
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import breeze.linalg._
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Await
import com.restfb.types.User
import com.restfb.DefaultFacebookClient
import neu.edu.scala.fb_recommend


object TFIDF {
  
  def getTfidf(fbClient:DefaultFacebookClient,path : String, id:String)={
    val sc = initialSpark
    val idArray = tfidf(id, sc,fbClient,path)
    idArray
  }
  
  def initialSpark : SparkContext = {
  
    System.setProperty("hadoop.home.dir", "/Users/yangnan/Documents/hadoop-2.7.1")
  
    val conf = new SparkConf().setMaster("local").setAppName("td").set("spark.executor.memory","4g") 
  
    val sc = new SparkContext(conf)
  
    sc
  }
  
  def tfidf(id:String, sc:SparkContext,fbClient:DefaultFacebookClient,filePath:String) : Array[(String,String)]= {

  val path = filePath
  
  val rdd = sc.wholeTextFiles(path).cache()

  val text = rdd.map{case (file,text) => text}  
  
  val file_path = rdd.map{case (file,text) => 
    
  val a = file.split("/").takeRight(1)
    
  a.head.replaceAll(".txt","")
  
  }

  val tokens = text.map(doc => tf_idf_imp.tokenize(doc))
  
  
  val feature_word = tf_idf_imp.extract_Key_Word(tokens)
  
  val c = tf_idf_imp.ExtractFeatures(text)
 
  val group = feature_word.collect().zipWithIndex.toMap
   
  val zipped = feature_word.zip(c)
   

   
   val zip_id_vector = file_path.zip(c);
   
   
   val train = zipped.map{ case(label,vector) => LabeledPoint(group(label),vector)}
  
  train.cache()
  
   val model = NaiveBayes.train(train, lambda = 0.1)  

   val id_word_rdd = feature_word.zip(file_path)
 //<------------------------------------------------------------> 
  
     val recommend =  {
       if(id == null) new fb_recommend("me", fbClient) //new fb_recommend("me", fbClient)
       else new fb_recommend(id, fbClient) //new fb_recommend("me", fbClient)
  }

     val rt = Future(recommend.result_1.toList)
   
     val a = rt.map { x => x.flatten } //x include all posts
     
     val b = a.map { x => x.map { x => x.getMessage } } // To string
     
     val keke =b.map { x => doSome(sc,fbClient, x, zip_id_vector, model, id_word_rdd) } // NLP processing
       
     val waiting = Await.result(keke, scala.concurrent.duration.Duration.Inf) //Wait for Future result

     
     keke.onComplete {
       
       case Success(o) => o
       case Failure(ex) => println("Exception: "+ex)

     }
     
     val array = keke.value.get.get.asInstanceOf[Array[(String,String)]]
     array
}


 def scalaIterator[T](it: JIterator[T]) = new Iterator[T] {
  override def hasNext = it.hasNext
  override def next() = it.next()
} 

def doSome(sc: SparkContext,fbClient:DefaultFacebookClient, o:List[String], zip_id_vector : RDD[(String, SV)] , model:NaiveBayesModel,  id_word_rdd:RDD[(String, String)]) : Array[(String,String)] = {
   
  val post_list = o.mkString(" ")
  
  val post_list_1 = List(post_list,"")
  
  val rdd_post = sc.parallelize(post_list_1)
  
  val al = rdd_post.filter { x => x != "" }
  
  val post_tokens = al.map { x => tf_idf_imp.tokenize(x) }
  
  val fb_feature_word = tf_idf_imp.extract_Key_Word(post_tokens)
  
  val fb_vector = tf_idf_imp.ExtractFeatures(rdd_post)
    

//<-------------------calculate similarity--------------------------> 
  
  val duang = zip_id_vector.map{case (id,vector) => (id,new SparseVector(vector.indices,vector.values,vector.size))}
      
  val first = fb_vector.first()

  val sample = new SparseVector(first.indices,first.values,first.size)
  
  val du = tf_idf_imp.extract_associative_user_rdd(duang,sample)


//<------------------------------------------------------------------>
   
    
    
    
      val fb_group = fb_feature_word.collect().zipWithIndex.toMap
      
      val fb_zipped = fb_feature_word.zip(fb_vector)
       
      val test_fb = fb_zipped.map { case (topic, vector) => LabeledPoint(fb_group(topic), vector) }
       
      val predictionAndLabel = test_fb.map(p => (model.predict(p.features), p.label))

      val predict_value =  predictionAndLabel.first._2.intValue()

      val word_outcome  =  fb_group.filter{ p => p._2 == predict_value}.head._1

      val associative_user_rdd = id_word_rdd.filter{f => f._1.equals(word_outcome) }.toArray()

      val final_user_list = du++associative_user_rdd     // List of (sim/keyword,ID)
      
      final_user_list.foreach( f => println(fb_recommend.fetchUser(f._2,fbClient))) //f._2 is ID of 
      
      val newIdlist = final_user_list.map(f => (f._1.toString(), f._2)) // Nan Pass it to graph
      
      newIdlist
   
}

}
