package neu.edu.scala
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.{ SparseVector => SV }
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.feature.IDF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.evaluation.MulticlassMetrics




object TFIDF {
  
  val SPARK_WORKER_MEMORY="4g"
  
  System.setProperty("hadoop.home.dir", "/usr/local/bin/hadoop")
  
  val conf = new SparkConf().setMaster("local").setAppName("td").set("spark.executor.memory","4g") 
  
  
 
  
  val sc = new SparkContext(conf)
  
  val path = "/home/chenxi/Documents/fb_output"
  
  val rdd = sc.wholeTextFiles(path)

  val text = rdd.map{case (file,text) => text}
  
  
 // val file_path = rdd.map{case (file,text) => file}
//  
//  val whileSpaceSplit = text.flatMap(t => t.split("""\W+""").map(_.toLowerCase()))
//  
  val regex = """[^0-9]*""".r
//val filterNumbers = whileSpaceSplit.filter(token => o
//regex.pattern.matcher(token).matches)



val stopwords = Set(
"the","and","to","a","for","of","s","in","you","on", "with", "at", 
"we", "our", "http", "is","this", "it", "your", "com", "it","be",
"are","from","all","null","out","tonight","will","us","t",
"today","have","i","day","new","by","that","or","come","up","www",
"get","here","ly","one","night","what","some","as","can","more","great",
"re","so","just","see","now","time","my","check","an","my","if","week",
"about","who","bit","was","open","tomorrow","but","do","year","good","back",
"make","their","join","not","off","last","best","they","his","friday","me",
"don","go","has","there","fisrt","ll","how","co","w","m","over","lunch",
"sunday","know","no","mfdulock","htm","serious","pres"

)

//val tokenCounts = filterNumbers.map(t => (t, 1)).reduceByKey(_ + _)




def tokenize(line: String): Seq[String] = {
line.split("""\W+""")
.map(_.toLowerCase)
.filter(token => regex.pattern.matcher(token).matches)
.filterNot(token => stopwords.contains(token))
.filter(token => token.size >= 2)
.filter(token => regex.pattern.matcher(token).matches)
.toSeq
}


  
  
def ExtractFeatures( wholeTextFile: RDD[String]) = {


val tokens = wholeTextFile.map(doc => tokenize(doc))


  val dim = math.pow(2, 8).toInt
val hashingTF = new HashingTF(dim)

val tf = hashingTF.transform(tokens)
tf.cache

val idf = new IDF().fit(tf)

val tfidf = idf.transform(tf)

tfidf.map { v =>
v.asInstanceOf[SV]}


}





val tokens = text.map(doc => tokenize(doc))



//val a = tokens.map{s => s.toList}



def GetTF(single:List[String]) = {
  
 val sum = single.length
 
 val d = single.groupBy(x => x).map {ele => ele._1 -> ele._2.length * 1.0/sum }
 

 
d
}





def GetIDF( wholeFiles : RDD[List[String]]) = {
 
  
   val count_files = wholeFiles.count
  
 //  println("file count"+count_files)
   
  val each_distinct = wholeFiles.map{list => list.distinct}
  

  
 val flattened_rdd = for
 {
      list <- each_distinct    
      
      token <- list
   
 }yield token
  
 
 val tups = flattened_rdd.map( s => (s,1)).reduceByKey(_+_)
 
 val tups_idf = tups.map{tup => (tup._1, Math.log(count_files * 1.0/(tup._2)))}
 
 tups_idf
}




 def main(args: Array[String]): Unit = {
    
 
         val a = tokens.map{s => s.toList}
    
   val out_1 = a.map { x =>  GetTF(x)}
    
    
   val out_2 = GetIDF(a).collectAsMap()

   val outcome = out_1.map{m => m.map{case(k,v) => (k,v * out_2.getOrElse(k,3.76))}}
   

   
  val feature_word_tup = outcome.map( m =>m.reduce((m1,m2) => if(m1._2 >= m2._2) m1 else m2))
  
  
  
  val feature_word = feature_word_tup.map(m => m._1)
  
 
  val c = ExtractFeatures(text)
 
   val group = feature_word.collect().zipWithIndex.toMap
   
   val zipped = feature_word.zip(c)
   
  // println("aaaaaaaaaa"+zipped.count())
   
   
   val train = zipped.map{ case(label,vector) => LabeledPoint(group(label),vector)}
  
  train.cache
  
  val model = NaiveBayes.train(train, lambda = 0.1)  
  
  
  
  
  
  
 //<------------------------------------------------------------> 
  val post_list = fb_recommendation.output.mkString(" ")
  
  val post_list_1 = List(post_list)
  
  val rdd_post = sc.parallelize(post_list_1)
  
  val post_tokens = rdd_post.map { x => tokenize(x) }
 

  
  val post_list_tokens = post_tokens.map { x => x.toList }
  
  val tf_post = post_list_tokens.map { x => GetTF(x) }
  
  val idf = GetIDF(post_list_tokens).collectAsMap()
  
 val tf_idf = tf_post.map{m => m.map{case(k,v) => (k,v * idf.getOrElse(k,3.76))}}
  

  
  val fb_feature = tf_idf.map( m =>m.reduce((m1,m2) => if(m1._2 >= m2._2) m1 else m2))
  
   val fb_feature_word = fb_feature.map(m => m._1)
   
   //println(fb_feature_word.count())
   
    val fb_vector = ExtractFeatures(rdd_post)
    
    
    
    
    
    
    val fb_group = fb_feature_word.collect().zipWithIndex.toMap
    
     val fb_zipped = fb_feature_word.zip(fb_vector)
     
     val test_fb = fb_zipped.map { case (topic, vector) => 
LabeledPoint(fb_group(topic), vector) }
     
  val predictionAndLabel = test_fb.map(p => (model.predict(p.features), 
p.label))



      val s =  predictionAndLabel.first._2.intValue()
      
   //   println(s)
         fb_group.foreach(f => if(f._2 == s ) println(f._1)   )
        
        
  }
  
}