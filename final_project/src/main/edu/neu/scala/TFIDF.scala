package edu.neu.scala
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD




object TFIDF {
  
  System.setProperty("hadoop.home.dir", "/usr/local/bin/hadoop")
  
  val conf = new SparkConf().setMaster("local").setAppName("td")
  
  val sc = new SparkContext(conf)
  
  val path = "/home/chenxi/Documents/fb_output"
  
  val rdd = sc.wholeTextFiles(path)
  
  val text = rdd.map{case (file,text) => text}
  
  
  def main(args: Array[String]): Unit = {
    
    println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+text.count())
    
    
  }
  
}