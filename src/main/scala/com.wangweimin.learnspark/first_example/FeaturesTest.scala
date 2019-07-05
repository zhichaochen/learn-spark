package com.wangweimin.learnspark.first_example

import org.apache.spark.sql.SparkSession
/**
  * 1、TF-IDF（词频-逆向文档频率）
  */
object FeaturesTest {
  /**
    * HashingTF是一个特征词集的转换器（Transformer），它可以将返些集合转换成固定长度
    * 的特征向量。HashingTF利用hashing trick，原始特征通过应用哈希函数映射到索引中。然
    * 后根据映射的索引计算词频。返种斱法避免了计算全局特征词对索引映射的需要，返对于大
    * 型诧料库来说可能是昂贵的，但是它具有潜在的哈希冲突，其中丌同的原始特征可以在散列
    * 乊后发成相同的特征词。为了减少碰撞的机会，我们可以增加目标特征维度，即哈希表的桶
    * 数。由于使用简单的模数将散列函数转换为列索引，建议使用两个幂作为特征维，否则丌会
    *
    * 将特征均匀地映射到列。默认功能维度为2^18=262144。可选的二迕制切换参数控制词频计
    * 数。当设置为true时，所有非零频率计数设置为1。返对于模拟二迕制而丌是整数的离散概率
    * 模型尤其有用。
    *
    * @param args
    */
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
    val sentenceData = spark.createDataFrame(
      Seq(
        Love(1L, "I love you", 1.0),
        Love(2L, "There is nothing to do", 0.0),
        Love(3L, "Work hard and you will success", 0.0),
        Love(4L, "We love each other", 1.0),
        Love(5L, "Where there is love, there are always wishes", 1.0),
        Love(6L, "I love you not because who you are,but because who I am when I am with you", 1.0),
        Love(7L, "Never frown,even when you are sad,because youn ever know who is falling in love with your smile", 1.0),
        Love(8L, "Whatever is worth doing is worth doing well", 0.0),
        Love(9L, "The hard part isn’t making the decision. It’s living with it", 0.0),
        Love(10L, "Your happy passer-by all knows, my distressed there is no place hides", 0.0),
        Love(11L, "When the whole world is about to rain, let’s make it clear in our heart together", 0.0)
      )
    ).toDF()
    sentenceData.show(false)

    /**
      * +---+-----------------------------------------------------------------------------------------------+-----+
      * |id |text                                                                                           |label|
      * +---+-----------------------------------------------------------------------------------------------+-----+
      * |1  |I love you                                                                                     |1.0  |
      * |2  |There is nothing to do                                                                         |0.0  |
      * |3  |Work hard and you will success                                                                 |0.0  |
      * |4  |We love each other                                                                             |1.0  |
      * |5  |Where there is love, there are always wishes                                                   |1.0  |
      * |6  |I love you not because who you are,but because who I am when I am with you                     |1.0  |
      * |7  |Never frown,even when you are sad,because youn ever know who is falling in love with your smile|1.0  |
      * |8  |Whatever is worth doing is worth doing well                                                    |0.0  |
      * |9  |The hard part isn’t making the decision. It’s living with it                                   |0.0  |
      * |10 |Your happy passer-by all knows, my distressed there is no place hides                          |0.0  |
      * |11 |When the whole world is about to rain, let’s make it clear in our heart together               |0.0  |
      * +---+-----------------------------------------------------------------------------------------------+-----+
      */

    // 2.参数设置：tokenizer、hashingTF、idf
    val tokenizer = new Tokenizer()
      .setInputCol("text")
      .setOutputCol("words")
    val hashingTF = new HashingTF()
      .setNumFeatures(20)
      .setInputCol(tokenizer.getOutputCol)
      .setOutputCol("rawFeatures")
    val idf = new IDF() // 通过CountVectorizer也可以获得词频向量
      .setInputCol(hashingTF.getOutputCol)
      .setOutputCol("features")

    val wordsData = tokenizer.transform(sentenceData)
    val featurizedData = hashingTF.transform(wordsData)
    val idfModel = idf.fit(featurizedData)

    // 3. 文档的向量化表示
    val rescaledData = idfModel.transform(featurizedData)
    rescaledData
      .select("label", "features")
      .show(false)

    /** 可见：句子越长，单词越多，则特征向量越多
      * +-----+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
      * |label|features                                                                                                                                                                                                                                                                                        |
      * +-----+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
      * |1.0  |(20,[0,5,9],[0.28768207245178085,0.4054651081081644,0.8754687373538999])                                                                                                                                                                                                                        |
      * |0.0  |(20,[1,4,8,11,14],[0.4054651081081644,1.3862943611198906,1.0986122886681098,1.0986122886681098,0.8754687373538999])                                                                                                                                                                             |
      * |0.0  |(20,[0,5,7,13],[0.28768207245178085,1.2163953243244932,1.3862943611198906,0.8754687373538999])                                                                                                                                                                                                  |
      * |1.0  |(20,[0,5,13,14],[0.28768207245178085,0.4054651081081644,0.8754687373538999,0.8754687373538999])                                                                                                                                                                                                 |
      * |1.0  |(20,[1,11,13,14,17,18,19],[0.4054651081081644,2.1972245773362196,0.8754687373538999,0.8754687373538999,0.6931471805599453,1.0986122886681098,1.0986122886681098])                                                                                                                               |
      * |1.0  |(20,[0,1,5,9,10,13,15,16,17,18],[0.28768207245178085,0.8109302162163288,1.2163953243244932,2.6264062120616996,0.8754687373538999,1.7509374747077997,0.8754687373538999,0.6931471805599453,1.3862943611198906,1.0986122886681098])                                                               |
      * |1.0  |(20,[0,1,2,3,5,6,9,10,14,16,17,18,19],[0.28768207245178085,0.4054651081081644,1.3862943611198906,0.8754687373538999,0.8109302162163288,1.3862943611198906,0.8754687373538999,1.7509374747077997,0.8754687373538999,0.6931471805599453,1.3862943611198906,1.0986122886681098,2.1972245773362196])|
      * |0.0  |(20,[0,1,3,15,17],[0.5753641449035617,0.8109302162163288,1.7509374747077997,0.8754687373538999,0.6931471805599453])                                                                                                                                                                             |
      * |0.0  |(20,[0,5,7,10,15,16,19],[0.5753641449035617,0.8109302162163288,1.3862943611198906,2.6264062120616996,0.8754687373538999,0.6931471805599453,1.0986122886681098])                                                                                                                                 |
      * |0.0  |(20,[1,2,3,6,8,9,11,16],[1.2163953243244932,1.3862943611198906,0.8754687373538999,2.772588722239781,1.0986122886681098,0.8754687373538999,2.1972245773362196,0.6931471805599453])                                                                                                               |
      * |0.0  |(20,[0,1,3,4,5,8,10,12,15,16,17],[0.28768207245178085,0.4054651081081644,0.8754687373538999,2.772588722239781,0.8109302162163288,1.0986122886681098,1.7509374747077997,1.791759469228055,0.8754687373538999,2.0794415416798357,0.6931471805599453])                                             |
      * +-----+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
      */

  }
}
