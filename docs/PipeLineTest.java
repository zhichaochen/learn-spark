package com.wangweimin.learnspark.java;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.avro.ipc.specific.Contexts;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.feature.Bucketizer;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author weimin.wang
 * @Date 2019/6/26 15:59
 **/
//参考：文章https://blog.csdn.net/zbc1090549839/article/details/50935274
public class PipeLineTest {
    /*在此以Kaggle数据竞赛Display Advertising Challenge的数据集(该数据集为利用用户特征进行
    广告点击预测)开始，利用spark ml pipeline构建一个完整的机器学习工作流程。
    Display Advertising Challenge的这份数据本身就不多做介绍了，主要包括3部分，
    numerical型特征集、Categorical类型特征集、类标签。*/

    //首先，读入样本集，并将样本集划分为训练集与测试集：
    //使用file标记文件路径，允许spark读取本地文件
    String fileReadPath = "file:\\D:\\dac_sample\\dac_sample.txt";
    //使用textFile读入数据
    SparkContext sc = Contexts.sparkContext;
    RDD<String> file = sc.textFile(fileReadPath, 1);
    JavaRDD<String> sparkContent = file.toJavaRDD();
    JavaRDD<Row> sampleRow = sparkContent.map(new Function<String, Row>() {
        public Row call(String string) {
            String tempStr = string.replace("\t", ",");
            String[] features = tempStr.split(",");
            int intLable = Integer.parseInt(features[0]);
            String intFeature1 = features[1];
            String intFeature2 = features[2];
            String CatFeature1 = features[14];
            String CatFeature2 = features[15];
            return RowFactory.create(intLable, intFeature1, intFeature2, CatFeature1, CatFeature2);
        }
    });
    double[] weights = {0.8, 0.2};
    Long seed = 42L;
    JavaRDD<Row>[] sampleRows = sampleRow.randomSplit(weights, seed);

    //得到样本集后，构建出 DataFrame格式的数据供spark ml pipeline使用：
    List<StructField> fields = new ArrayList();
    fields.add(DataTypes.createStructField("lable", DataTypes.IntegerType, false));
    fields.add(DataTypes.createStructField("intFeature1", DataTypes.StringType, true));
    fields.add(DataTypes.createStructField("intFeature2", DataTypes.StringType, true));
    fields.add(DataTypes.createStructField("CatFeature1", DataTypes.StringType, true));
    fields.add(DataTypes.createStructField("CatFeature2", DataTypes.StringType, true));
    //and so on


    StructType schema = DataTypes.createStructType(fields);
    DataFrame dfTrain = Contexts.hiveContext.createDataFrame(sampleRows[0], schema);//训练数据
    dfTrain.registerTempTable("tmpTable1");
    DataFrame dfTest = Contexts.hiveContext.createDataFrame(sampleRows[1], schema);//测试数据
    dfTest.registerTempTable("tmpTable2");

    /*由于在dfTrain、dfTest中所有的特征目前都为string类型，而机器学习则要求其特征为numerical类型，在此需要对特征做转换，包括类型转换和缺失值的处理。
    首先，将intFeature由string转为double，cast()方法将表中指定列string类型转换为double类型，并生成新列并命名为intFeature1Temp，
    之后，需要删除原来的数据列 并将新列重命名为intFeature1，这样，就将string类型的特征转换得到double类型的特征了。*/
    //Cast integer features from String to Double
    dfTest = dfTest.withColumn("intFeature1Temp",dfTest.col("intFeature1").cast("double"));
    dfTest = dfTest.drop("intFeature1").withColumnRenamed("intFeature1Temp","intFeature1");
    如果intFeature特征是年龄或者特征等类型，则需要进行分箱操作，将一个特征按照指定范围进行划分：
    /*特征转换，部分特征需要进行分箱，比如年龄，进行分段成成年未成年等 */
    double[] splitV = {0.0,16.0,Double.MAX_VALUE};
    Bucketizer bucketizer = new Bucketizer().setInputCol("").setOutputCol("").setSplits(splitV);

    再次，需要将categorical 类型的特征转换为numerical类型。主要包括两个步骤，缺失值处理和编码转换。
    缺失值处理方面，可以使用全局的NA来统一标记缺失值：
    /*将categoricalb类型的变量的缺失值使用NA值填充*/
    String[] strCols = {"CatFeature1","CatFeature2"};
    dfTrain = dfTrain.na().fill("NA",strCols);
    dfTest = dfTest.na().fill("NA",strCols);

    缺失值处理完成之后，就可以正式的对categorical类型的特征进行numerical转换了。在spark ml中，可以借助StringIndexer和oneHotEncoder完成
    这一任务：
    // StringIndexer  oneHotEncoder 将 categorical变量转换为 numerical 变量
    // 如某列特征为星期几、天气等等特征，则转换为七个0-1特征
    StringIndexer cat1Index = new StringIndexer().setInputCol("CatFeature1").setOutputCol("indexedCat1").setHandleInvalid("skip");
    OneHotEncoder cat1Encoder = new OneHotEncoder().setInputCol(cat1Index.getOutputCol()).setOutputCol("CatVector1");
    StringIndexer cat2Index = new StringIndexer().setInputCol("CatFeature2").setOutputCol("indexedCat2");
    OneHotEncoder cat2Encoder = new OneHotEncoder().setInputCol(cat2Index.getOutputCol()).setOutputCol("CatVector2");

    至此，特征预处理步骤基本完成了。由于上述特征都是处于单独的列并且列名独立，为方便后续模型进行特征输入，需要将其转换为特征向量，并统一命名，
    可以使用VectorAssembler类完成这一任务：
    /*转换为特征向量*/
    String[] vectorAsCols = {"intFeature1","intFeature2","CatVector1","CatVector2"};
    VectorAssembler vectorAssembler = new VectorAssembler().setInputCols(vectorAsCols).setOutputCol("vectorFeature");
    通常，预处理之后获得的特征有成千上万维，出于去除冗余特征、消除维数灾难、提高模型质量的考虑，需要进行选择。在此，使用卡方检验方法，
    利用特征与类标签之间的相关性，进行特征选取：
    /*特征较多时，使用卡方检验进行特征选择，主要是考察特征与类标签的相关性*/
    ChiSqSelector chiSqSelector = new ChiSqSelector().setFeaturesCol("vectorFeature").setLabelCol("label").setNumTopFeatures(10)
            .setOutputCol("selectedFeature");

    在特征预处理和特征选取完成之后，就可以定义模型及其参数了。简单期间，在此使用LogisticRegression模型，并设定最大迭代次数、正则化项：
    /* 设置最大迭代次数和正则化参数 setElasticNetParam=0.0 为L2正则化 setElasticNetParam=1.0为L1正则化*/
    /*设置特征向量的列名，标签的列名*/
    LogisticRegression logModel = new LogisticRegression().setMaxIter(100).setRegParam(0.1).setElasticNetParam(0.0)
            .setFeaturesCol("selectedFeature").setLabelCol("lable");

    在上述准备步骤完成之后，就可以开始定义pipeline并进行模型的学习了：
    /*将特征转换，特征聚合，模型等组成一个管道，并调用它的fit方法拟合出模型*/
    PipelineStage[] pipelineStage = {cat1Index,cat2Index,cat1Encoder,cat2Encoder,vectorAssembler,logModel};
    Pipeline pipline = new Pipeline().setStages(pipelineStage);
    PipelineModel pModle = pipline.fit(dfTrain);
    上面pipeline的fit方法得到的是一个Transformer，我们可以使它作用于训练集得到模型在训练集上的预测结果：
    //拟合得到模型的transform方法进行预测
    DataFrame output = pModle.transform(dfTest).select("selectedFeature", "label", "prediction", "rawPrediction", "probability");
    DataFrame prediction = output.select("label", "prediction");
        prediction.show();

    //分析计算，得到模型在训练集上的准确率，看看模型的效果怎么样：
    /*测试集合上的准确率*/
    long correct = prediction.filter(prediction.col("label").equalTo(prediction.col("'prediction"))).count();
    long total = prediction.count();
    double accuracy = correct / (double)total;

        System.out.println(accuracy);

    //最后，可以将模型保存下来，下次直接使用就可以了：
    String pModlePath = ""file:\\D:\\dac_sample\\";
            pModle.save(pModlePath);
}
