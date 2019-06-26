package com.wangweimin.learnspark.utils;

import org.apache.spark.sql.SparkSession;

/**
 * SparkSession工具类。
 * @author Phoenix Lee (liqingyuan1986@aliyun.com)
 * @since 2016/9/19
 */
public class SparkSessionUtil {

    public static SparkSession getSpark() {
        String warehouseLocation = "file:" + System.getProperty("user.dir") + "spark-warehouse";
        return SparkSession
                .builder()
                .appName("Java Spark Hive Example")
                .config("spark.sql.warehouse.dir", warehouseLocation)
                .enableHiveSupport()
                .getOrCreate();
    }
}
