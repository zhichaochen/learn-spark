package sparkbase

import org.apache.spark.{SparkConf, SparkContext}

/**
  * 参考文章：https://blog.csdn.net/u010675669/article/details/81744386
  */
object WordCount {
  def file_name:String = "word_count_result"
  def main(args: Array[String]): Unit = {
   if(args.length < 1){
      println("usage:wordCount fileName")
      System.exit(1);
    }
    val conf = new SparkConf().setMaster("local[*]").setAppName("word_count")
    val sc = new SparkContext(conf)

    //切记：如果这里使用本地目录，会报错误，
    //Exception in thread "main" java.lang.IllegalArgumentException: Pathname /C:/bigdata/sourcedata/spam_ham.txt
    // from hdfs://localhost:9000/C:/bigdata/sourcedata/spam_ham.txt is not a valid DFS filename.
    //原因是：在spark上运行任务，是去hdfs上找相关文件。
    val textFile = sc.textFile(args(0))
    val wordCount = textFile
      .flatMap(line=>line.split(" "))
      .map(word =>(word,1))
      .reduceByKey((a,b) => a + b)

    println("Word Count program running results:");
    wordCount.collect().foreach(x=>{
      val (k,v) = x
      println("k="+k,"v="+v)
    })
    wordCount.saveAsTextFile("/user/result/"+file_name+System.currentTimeMillis())
    println("Word Count program running results are successfully saved.")
  }

  /**
    * 提交到集群执行
    * spark-submit --class sparkbase.WordCount --master local[*] C:\git\learn-spark\src\main\scala\learn-spark.jar hdfs://localhost:9000/user/sourcedata/spam_ham.txt
    *
    * 1、--class：所在的类。
    * 2、--master：local[*]
    * 3、本地jar包地址
    * 4、传入main函数的参数。
    *
    */
}
