package com.wangweimin.learnspark.wordcount

import org.apache.spark.{SparkConf, SparkContext}

/**
  * spark-submit--class com.wangweimin.learnspark.java.SparkWordCount --master spark://spark1:7077/ C:\git\learn-spark\target\bitask-dev.jar
  */
object SparkWordCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    val sc = new SparkContext(conf)
    val textFile = sc.textFile("hdfs://localhost:9000/user/sourcedata/thankful.txt")
    val wordCounts = textFile
      .flatMap(line => line.split(" "))
      .map(word => (word, 1))
      .reduceByKey(_+_)

    wordCounts.saveAsTextFile("hdfs://localhost:9000/user/result/wordcount_thankful.txt")
    println("Word Count program running results are successfully saved.")
  }



  /**
    * 解释：
    * word => (word, 1)等同于java版本的匿名内部类中，传入一个String参数，返回一个为<String,Integer>类型的对象
    */
}