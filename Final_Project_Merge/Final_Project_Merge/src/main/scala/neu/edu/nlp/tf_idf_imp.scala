package neu.edu.nlp
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.mllib.linalg.{ SparseVector => SV }
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.feature.IDF
import breeze.linalg._
import org.apache.spark.mllib.linalg.{SparseVector => SV}
import org.apache.spark.mllib.linalg.{SparseVector => SV}
 




 

object tf_idf_imp {
  
  
    val regex = """[^0-9]*""".r



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

  

def tokenize(line: String): Seq[String] = {
    line.split("""\W+""")
    .map(_.toLowerCase)
    .filter(token => regex.pattern.matcher(token).matches)
    .filterNot(token => stopwords.contains(token))
    .filter(token => token.size >= 2)
    .filter(token => regex.pattern.matcher(token).matches)
    .toSeq
}
   
def ExtractFeatures( wholeTextFile: RDD[String]) = { // Generate vectors


val tokens = wholeTextFile.map(doc => tokenize(doc))


  val dim = math.pow(2, 8).toInt
  val hashingTF = new HashingTF(dim)

  val tf = hashingTF.transform(tokens)
  tf.cache

  val idf = new IDF().fit(tf)

  val tfidf = idf.transform(tf)

  tfidf.map { v =>
    v.asInstanceOf[SV]
  }


}



def GetTF(single:List[String]) = { // get topic key word
  
   val sum = single.length
 
   val d = single.groupBy(x => x).map {ele => ele._1 -> ele._2.length * 1.0/sum }

   d
}


def GetIDF( wholeFiles : RDD[List[String]]) = {
 
  
   val count_files = wholeFiles.count
   
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


def extract_Key_Word(tokens :  RDD[Seq[String]]) = // call getTF & get IDF and get Top key word

{
  
   val a = tokens.map{s => s.toList}
    
   val out_1 = a.map { x =>  tf_idf_imp.GetTF(x)}
    
    
   val out_2 = tf_idf_imp.GetIDF(a).collectAsMap()

   val outcome = out_1.map{m => m.map{case(k,v) => (k,v * out_2.getOrElse(k,3.76))}}

   val feature_word_tup = outcome.map( m =>m.reduce((m1,m2) => if(m1._2 >= m2._2) m1 else m2))

   feature_word_tup.map(m => m._1)
  
}


def extract_associative_user_rdd( duang : RDD[(String, SparseVector[Double])], sample:SparseVector[Double])={ // Calculate Similarity of vectors

  val duan = duang.map{case (id,vector) => (id, vector.dot(sample) / ( norm(vector) * norm(sample)    ) )}

  val dua =  duan.filter( f => f._2 > 0.0).map{ case (topic,simi) => (simi,topic)}
 
  val du = dua.sortByKey(false).take(10) //Nan need filter
 
  du
  
}





}