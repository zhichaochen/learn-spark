package com.wangweimin.learnspark.java

import org.apache.spark.{SparkConf, SparkContext}

/**
  * spark-submit--class com.wangweimin.learnspark.java.SparkWordCount --master spark://spark1:7077/ C:\git\learn-spark\target\bitask-dev.jar
  */
object SparkWordCount {
  def FILE_NAME: String = "word_count_results"

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("my test").setMaster("local")
    val sc = new SparkContext(conf)
    val textFile = sc.textFile("C:\\bigdata\\spam_ham.txt")
    val wordCounts = textFile.flatMap(line => line.split(" "))
      .map(word => (word, 1)).reduceByKey((a, b) => a + b)
    wordCounts.saveAsTextFile("hdfs://word_count_results" + System.currentTimeMillis())
    println("Word Count program running results are successfully saved.")
  }
}