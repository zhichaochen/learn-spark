package com.wangweimin.learnspark.java;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

/**
 * @Author weimin.wang
 * @Date 2019/6/25 10:37
 **/
public class Test2 {
    public void main(){
        SparkContext sc = new SparkContext();
        PairFunction keyData = new PairFunction() {
            @Override
            public Tuple2 call(String o) throws Exception {
                return new Tuple2(o.split(" ")[0],o);
            }
        }
    }
}
