package com.wangweimin.learnspark.first_example

import org.apache.spark.ml.feature.CountVectorizer
import org.apache.spark.sql.SparkSession

/**
  * 3、CountVectorizer
  * 获取词频
  */
object FeaturesTests {
  def main(args: Array[String]): Unit = {

    // 0.构建 Spark 对象
    val spark = SparkSession
      .builder()
      .master("local") // 本地测试，否则报错 A master URL must be set in your configuration at org.apache.spark.SparkContext.
      .appName("test")
      .enableHiveSupport()
      .getOrCreate() // 有就获取无则创建

    spark.sparkContext.setCheckpointDir("C:\\bigdata\\spark\\checkpoint") //设置文件读取、存储的目录，HDFS最佳

    // 1.训练样本
    val documentDF = spark.createDataFrame(
      Seq(
        "I love you".split(" "),
        "There is nothing to do".split(" "),
        "Work hard and you will success".split(" "),
        "We love each other".split(" "),
        "Where there is love, there are always wishes".split(" "),
        "I love you not because who you are,but because who I am when I am with you".split(" "),
        "Never frown,even when you are sad,because youn ever know who is falling in love with your smile".split(" "),
        "Whatever is worth doing is worth doing well".split(" "),
        "The hard part isn’t making the decision. It’s living with it".split(" "),
        "Your happy passer-by all knows, my distressed there is no place hides".split(" "),
        "When the whole world is about to rain, let’s make it clear in our heart together".split(" ")
      ).map(Tuple1.apply)
    ).toDF("words") // scala 版本为 2.11+ 才可以，否则报错：No TypeTag available
    documentDF.show(false)

     /*
      * +-----------------------------------------------------------------------------------------------------------------+
      * |words                                                                                                            |
      * +-----------------------------------------------------------------------------------------------------------------+
      * |[I, love, you]                                                                                                   |
      * |[There, is, nothing, to, do]                                                                                     |
      * |[Work, hard, and, you, will, success]                                                                            |
      * |[We, love, each, other]                                                                                          |
      * |[Where, there, is, love,, there, are, always, wishes]                                                            |
      * |[I, love, you, not, because, who, you, are,but, because, who, I, am, when, I, am, with, you]                     |
      * |[Never, frown,even, when, you, are, sad,because, youn, ever, know, who, is, falling, in, love, with, your, smile]|
      * |[Whatever, is, worth, doing, is, worth, doing, well]                                                             |
      * |[The, hard, part, isn’t, making, the, decision., It’s, living, with, it]                                         |
      * |[Your, happy, passer-by, all, knows,, my, distressed, there, is, no, place, hides]                               |
      * |[When, the, whole, world, is, about, to, rain,, let’s, make, it, clear, in, our, heart, together]                |
      * +-----------------------------------------------------------------------------------------------------------------+
      **/

    // 2. CountVectorizer
    val cvModel = new CountVectorizer()
      .setInputCol("words")
      .setOutputCol("features")
      .setVocabSize(3)
      .setMinDF(2)
      .fit(documentDF)


    // 3. 文档的向量化表示
    cvModel.transform(documentDF).show(false)

    /**
      * +-----------------------------------------------------------------------------------------------------------------+-------------------------+
      * |words                                                                                                            |features                 |
      * +-----------------------------------------------------------------------------------------------------------------+-------------------------+
      * |[I, love, you]                                                                                                   |(3,[1,2],[1.0,1.0])      |
      * |[There, is, nothing, to, do]                                                                                     |(3,[0],[1.0])            |
      * |[Work, hard, and, you, will, success]                                                                            |(3,[1],[1.0])            |
      * |[We, love, each, other]                                                                                          |(3,[2],[1.0])            |
      * |[Where, there, is, love, , there, are, always, wishes]                                                            |(3,[0],[1.0])            |
      * |[I, love, you, not, because, who, you, are,but, because, who, I, am, when, I, am, with, you]                     |(3,[1,2],[3.0,1.0])      |
      * |[Never, frown,even, when, you, are, sad,because, youn, ever, know, who, is, falling, in, love, with, your, smile]|(3,[0,1,2],[1.0,1.0,1.0])|
      * |[Whatever, is, worth, doing, is, worth, doing, well]                                                             |(3,[0],[2.0])            |
      * |[The, hard, part, isn’t, making, the, decision., It’s, living, with, it]                                         |(3,[],[])                |
      * |[Your, happy, passer-by, all, knows, , my, distressed, there, is, no, place, hides]                               |(3,[0],[1.0])            |
      * |[When, the, whole, world, is, about, to, rain, , let’s, make, it, clear, in, our, heart, together]                |(3,[0],[1.0])            |
      * +-----------------------------------------------------------------------------------------------------------------+-------------------------+
      **/

  }
}
