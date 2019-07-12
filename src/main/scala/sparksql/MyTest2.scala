package sparksql

import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import utils.SparkSessionUtil

/**
  * RDD转Dataset/Dataframe
  */
object MyTest2 {
  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSessionUtil.getInstance("test1")

    val sc = sparkSession.sparkContext
    //设置日志级别
    sc.setLogLevel("error")

    import sparkSession.implicits._


    //1.使用反射将RDD[Class]转为df
    val df = sc.textFile("C:\\bigdata\\sourcedata\\student.txt")
      .map(_.split(","))
      .map(x=>Student(x(0).trim.toInt,x(1),x(2).trim.toInt,x(3))).toDF()
    df.show();
    //    +---+-----+---+---+
    //    | id| name|age|sex|
    //    +---+-----+---+---+
    //    |  1|  leo| 18| 男|
    //    |  2| jack| 19| 男|
    //    |  3|marry| 17| 女|
    //      +---+-----+---+---+
    df.createOrReplaceTempView("std")
    val betweenDF = sparkSession.sql("select name,age from std where age between 13 and 19")
    betweenDF.map(x=>"name:"+x(0)).show()//可以通过下标
  //    +----------+
  //    |     value|
  //    +----------+
  //    |  name:leo|
  //    | name:jack|
  //    |name:marry|
  //    +----------+
    betweenDF.map(x=>"name:"+x.getAs[String]("name")).show()// 也可以通过字段处理。

    /**
      * 执行时报错：java.lang.NumberFormatException: For input string: ""
      * 这个问题实质就是数组越界导致的。原因是读取的数据文件info.txt有问题，读取后，每一行无法解析为需要的格式。
      *
      */


    //2、编程方式指定schema
    val stdRDD = sparkSession.sparkContext.textFile("C:\\bigdata\\sourcedata\\student.txt")
    val rowRDD = stdRDD
      .map(_.split(","))
      //将rdd映射到rowRDD
      .map(x=>Row(x(0).trim.toInt,x(1).trim,x(2).trim.toInt,x(3).trim))

    val schema = StructType(
      List(
        StructField("id",IntegerType,true),
        StructField("name",StringType,true),
        StructField("age",IntegerType,true),
        StructField("sex",StringType,true)
      )
    )
    val df1 = sparkSession.createDataFrame(rowRDD,schema);

    df1.createOrReplaceTempView("t_students")
    val df2 = sparkSession.sql("select * from t_students order by age")
    df2.rdd.collect().foreach(row => println(row))
      //[3,marry,17,女]
      //[1,leo,18,男]
      //[2,jack,19,男]
  }
  case class Student(id:Int,name:String,age:Int,sex:String)
}
