package scala

import org.apache.spark.{SparkConf, SparkContext}

object Test2 {
  def main(args:Array[String]): Unit = {
    //创建
    val conf = new SparkConf().setMaster("local").setAppName("my app")
    val sc = new SparkContext(conf)
    //关闭
    //sc.stop();
    val input = sc.textFile("aa")
    val words = input.flatMap(x=>x.split(","))
    val counts = words.map
    input.persist();//缓存rdd。
  }
}
