package com.wangweimin.learnspark

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object HelloWorkd {
  def main(arg : Array[String]):Unit = {
    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    conf.set("spark.default.parallelism","5")
    val sc = new SparkContext(conf)
    val array = Array(1,2,3)
    val arrayRDD: RDD[Int] = sc.parallelize(array,2);
    println(arrayRDD.getNumPartitions)
  }
}
