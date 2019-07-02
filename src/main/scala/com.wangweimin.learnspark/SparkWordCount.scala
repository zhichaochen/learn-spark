package com.wangweimin.learnspark
import org.apache.spark.{SparkConf, SparkContext}

/**
  * author:weimin.wang
  * desc:统计一个或多个词出现的频率
  * 参考：https://blog.csdn.net/u010675669/article/details/81744386
  **/
object SparkWordCount {
   def FILE_NAME: String  = "word_count_results"
   def main(args: Array[String]): Unit = {
   val conf = new SparkConf().setAppName("my test").setMaster("local")
   val sc = new SparkContext(conf)
   val textFile = sc.textFile("C:\\bigdata\\spam_ham.txt")
   val wordCounts = textFile.flatMap(line=>line.split(" "))
   .map(word=>(word,1)).reduceByKey((a,b) => a+b)
  wordCounts.saveAsTextFile("hdfs://word_count_results"+System.currentTimeMillis())
   println("Word Count program running results are successfully saved.")
   }
}
//spark-submit --class SparkWordCount --master spark://127.0.0.1:7077 --num-executors 3 --driver-memory 6g --executor-memory 2g --executor-cores 2  C:\git\learn-spark\target\bitask-dev.jar hdfs://hadoop036166:9000/user/fams/*.txt
