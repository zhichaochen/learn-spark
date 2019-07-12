package sparksql

import utils.SparkSessionUtil

/**
  * Dataset/Dataframe基本操作
  * 参考文章：https://blog.csdn.net/zhaolq1024/article/details/88551107
  */
object MyTest {
  /**
    * 总结：
    * 1、需要导入sparkSession.implicits._
    * 1、涉及到计算的需要使用符号$
    * 3、如果一个列使用了，另一个列也需要使用。
    * @param arg
    */
  def main(arg:Array [String]): Unit ={
    val sparkSession = SparkSessionUtil.getInstance("test1")

    val sc = sparkSession.sparkContext
    //设置日志级别
    sc.setLogLevel("error")
    //用于隐式转换，将RDD转为DataFrame
    // 切记，一定要导入这个，否则下面的$符号，以及下面涉及到计算的都不能识别。
    import sparkSession.implicits._

    //下面两种方式都可以
    //val df = sparkSession.read.format("json").load("C:\\bigdata\\sourcedata\\student.json");
    val df = sparkSession.read.json("file:/bigdata/sourcedata/student.json")
    df.show()
    /**
      *当将JSON文件读入Spark时_corrupt_record错误
      * 需要每行数据都是一个json对象
      */

    //以树的形式打开schema，类型为推断出来的。
    df.printSchema();
    //    root
    //    |-- age: long (nullable = true)
    //    |-- id: long (nullable = true)
    //    |-- name: string (nullable = true)
    //    |-- sex: string (nullable = true)

    //查找某些列
    df.select("name","age").show()

    df.select($"name",$"age"+1).show()

    df.filter($"age" > 20).show()

    df.groupBy("sex").count.show()

    //==============================================
    /**
      * 以上是df的操作，接下来我们的sql的方式与df进行交互
      */
    // 创建全局临时视图，就是表名，默认库位global_temp
    df.createOrReplaceTempView("student")


    sparkSession.sql("select * from student").show();

    //【创建ds】，和rdd类似，有两种途径，一种df转，一种集合转
    val ds1 = Seq(1,2,3).toDS()
    ds1.show()

    val stuDs = sparkSession.read
      //.option("multiLine", true).option("mode", "PERMISSIVE")
      .json("C:\\bigdata\\sourcedata\\student.json").as[Student]
    stuDs.show()
  }
  case class Student(id:Long,name:String,age:Long,sex:String)
}
