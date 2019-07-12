package com.wangweimin.learnspark.first_example

import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.sql.SparkSession

object FeaturesTest1 {
  def main(args: Array[String]): Unit = {

    // 0.构建 Spark 对象
    val spark = SparkSession
      .builder()
      .master("local") // 本地测试，否则报错 A master URL must be set in your configuration at org.apache.spark.SparkContext.
      .appName("test")
      .enableHiveSupport()
      .getOrCreate() // 有就获取无则创建

    spark.sparkContext.setCheckpointDir("C:\\LLLLLLLLLLLLLLLLLLL\\BigData_AI\\sparkmlTest") //设置文件读取、存储的目录，HDFS最佳

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
    ).toDF("text") // scala 版本为 2.11+ 才可以，否则报错：No TypeTag available
    documentDF.show(false)
    /**
      * +-----------------------------------------------------------------------------------------------------------------+
      * |text                                                                                                             |
      * +-----------------------------------------------------------------------------------------------------------------+
      * |[I, love, you]                                                                                                   |
      * |[There, is, nothing, to, do]                                                                                     |
      * |[Work, hard, and, you, will, success]                                                                            |
      * |[We, love, each, other]                                                                                          |
      * |[Where, there, is, love, , there, are, always, wishes]                                                            |
      * |[I, love, you, not, because, who, you, are,but, because, who, I, am, when, I, am, with, you]                     |
      * |[Never, frown,even, when, you, are, sad,because, youn, ever, know, who, is, falling, in, love, with, your, smile]|
      * |[Whatever, is, worth, doing, is, worth, doing, well]                                                             |
      * |[The, hard, part, isn’t, making, the, decision., It’s, living, with, it]                                         |
      * |[Your, happy, passer-by, all, knows, , my, distressed, there, is, no, place, hides]                               |
      * |[When, the, whole, world, is, about, to, rain, , let’s, make, it, clear, in, our, heart, together]                |
      * +-----------------------------------------------------------------------------------------------------------------+
      **/

    // 2. word2Vec
    val word2VecModel = new Word2Vec()
      .setInputCol("text") // 要求输入的数据，单位是数组
      .setOutputCol("result")
      .setVectorSize(3)
      .setMinCount(0)
      .fit(documentDF)

    // 3. 文档的向量化表示
    val result = word2VecModel.transform(documentDF)
    result
      .select("result","text")
      .show(false)

    /**
      * +--------------------------------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
      * |result                                                              |text                                                                                                             |
      * +--------------------------------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
      * |[-0.05712633579969406,0.01896375169356664,-0.021923241515954334]    |[I, love, you]                                                                                                   |
      * |[0.006795959174633027,-0.05859951674938202,-0.02231040205806494]    |[There, is, nothing, to, do]                                                                                     |
      * |[-0.01718233898282051,-0.044684726279228926,0.022707909112796187]   |[Work, hard, and, you, will, success]                                                                            |
      * |[0.014710488263517618,0.04914409201592207,-0.0535422433167696]      |[We, love, each, other]                                                                                          |
      * |[0.056647833436727524,-0.013540415093302727,-0.007903479505330324]  |[Where, there, is, love, , there, are, always, wishes]                                                            |
      * |[-0.012073692482183962,0.0068947237587588675,-0.007010678075911368] |[I, love, you, not, because, who, you, are,but, because, who, I, am, when, I, am, with, you]                     |
      * |[-0.009022715939756702,0.007438146413358695,-0.00402127337806365]   |[Never, frown,even, when, you, are, sad,because, youn, ever, know, who, is, falling, in, love, with, your, smile]|
      * |[-0.007301235804334283,-0.025249323691241443,0.05116166779771447]   |[Whatever, is, worth, doing, is, worth, doing, well]                                                             |
      * |[0.055422113192352386,0.04088194024833766,-0.008757691322402521]    |[The, hard, part, isn’t, making, the, decision., It’s, living, with, it]                                         |
      * |[0.0017315041817103822,0.026252828383197386,-0.004247877125938733]  |[Your, happy, passer-by, all, knows, , my, distressed, there, is, no, place, hides]                               |
      * |[-0.013085987884551287,-3.071942483074963E-4,-0.0029873197781853378]|[When, the, whole, world, is, about, to, rain, , let’s, make, it, clear, in, our, heart, together]                |
      * +--------------------------------------------------------------------+-----------------------------------------------------------------------------------------------------------------+
      **/

  }
}
