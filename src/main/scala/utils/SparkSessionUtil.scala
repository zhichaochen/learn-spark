package utils

import org.apache.spark.sql.SparkSession

object SparkSessionUtil {
  def getInstance(appName:String): SparkSession ={
    val sparkSession = SparkSession
      .builder()
      .appName(appName)
      .master("local[*]")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()
    return sparkSession;
  }
}
