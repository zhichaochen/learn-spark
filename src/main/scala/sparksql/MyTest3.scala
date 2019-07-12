package sparksql

import org.apache.spark.sql.SparkSession

object MyTest3 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("mytest")
      .enableHiveSupport()
      .getOrCreate()

    import spark.sql

    sql("show databases").show()
    val apiDF = spark.sql(
      """
      |select to_date(date) t,count(1)
      |from data.api"
      |where to_date(date)>'2019-02-13'
      |group by to_date(date)
      |order by t
      """.stripMargin
    ).show()
  }
}
