package sparkbase

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 计算平均年龄
  * 其中：第一列为Id，第二列为age
  */
object AvgAgeCalculator {
  def file_name:String = "avg_age_result"
  def main(args: Array[String]): Unit = {
    if (args.length < 1){
      println("Usage:AvgAgeCalculator datafile")
      System.exit(1)
    }
    val conf = new SparkConf().setAppName("avg_age_calculator").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val dataFile = sc.textFile(args(0))
    val count = dataFile.count()
    val ageRDD = dataFile.map(line=>line.split(" ")(1))
    /**
      * ageRDD.filter(age=> age==null || age=="")
      * 会报错java.lang.UnsupportedOperationException: empty.reduceLeft
      * 原因是把数据全给过滤了。
      */

    val totalAge = ageRDD.filter(age=> age!=null && age != "")
      .map(age=>age.toInt).collect().reduce((a,b)=>a+b)
    //345.0
    println("Total Age:" + totalAge + ";Number of People:" + count )
    val avgAge = totalAge.toDouble/count.toDouble
    println("Average Age is " + avgAge)
    //38.333333333333336
    val resultRDD = sc.parallelize(List(totalAge,avgAge))
    resultRDD.saveAsTextFile("/user/result/"+file_name)
  }

  /**
    * spark-submit --class sparkbase.AvgAgeCalculator --master local[*] C:\git\learn-spark\src\main\scala\learn-spark.jar  hdfs://localhost:9000/user/sourcedata/agedata.txt
    */
}
