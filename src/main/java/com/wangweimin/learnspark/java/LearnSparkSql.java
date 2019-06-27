package com.wangweimin.learnspark.java;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author weimin.wang
 * @Date 2019/6/27 15:45
 **/
public class LearnSparkSql {
    public static void main(String[] args) {
        String warehouseLocation = System.getProperty("user.dir") + "spark-warehouse";//用户的当前工作目录
        System.out.println(">>>>>>>>>>>>>>>"+warehouseLocation);
        SparkConf conf = new SparkConf().setAppName("spark sql test")
                .set("spark.sql.warehouse.dir", warehouseLocation)
                .setMaster("local[*]");

        //使用SparkSession，取代了原本的SQLContext与HiveContext。
        SparkSession sparkSession = SparkSession
                .builder()
                .config(conf)
                .getOrCreate();

        Dataset<Row> dataset = sparkSession.read().json("C:\\bigdata\\student.json");
        //查看表
        dataset.show();
        //查看表结构
        dataset.printSchema();
        //查看某一列 类似于MySQL： select name from people
        dataset.select("name").show();
        //查看多列并作计算 类似于MySQL: select name ,age+1 from people
        dataset.select("name", ("age")+1).show();
        //设置过滤条件 类似于MySQL:select * from people where age>21
        dataset.filter(("age")).show();
        //做聚合操作 类似于MySQL:select age,count(*) from people group by age
        //dataset.groupBy("age").count().show();
        //上述多个条件进行组合 select ta.age,count(*) from (select name,age+1 as "age" from people) as ta where ta.age>21 group by ta.age
        // dataset.select("name", Column("age").plus(1).alias("age")).filter(col("age").gt(21)).groupBy("age").count().show();

        //方法一：
        Encoder<Person> personEncoder = Encoders.bean(Person.class);
        Dataset<Person> javaBeanDS = sparkSession.createDataset(createPersons(),personEncoder);
        javaBeanDS.show();
        //方法二：利用Java反射的特性，来从其他数据集中创建DataSet对象：
        //spark支持使用java 反射机制推断表结构
        //1 首先创建一个存储person对象的RDD

        JavaRDD<Person> personRdd = sparkSession.read().textFile().javaRDD()
                .map(new Function<String, Person>() {
                    @Override
                    public Person call(String line) throws Exception {
                        String[] parts = line.split(",");
                        Person person = new Person();
                        person.setId(Integer.valueOf(parts[0]));
                        person.setName(parts[1]);
                        person.setAge(Integer.parseInt(parts[1].trim()));
                        return person;
                    }
                });
        Dataset<Row> peopleDF = sparkSession.createDataFrame(personRdd, Person.class);
        peopleDF.createOrReplaceTempView("people");

        //3 定义map 这里对每个元素做序列化操作
        Encoder<String> stringEncoder = Encoders.STRING();
        Dataset<String> peopleSerDF = peopleDF.map(new MapFunction<Row, String>() {
            public String call(Row row) throws Exception {
                return "Name: " + row.getString(1) + " and age is " + String.valueOf(row.getInt(0));
            }
        }, stringEncoder);
        peopleSerDF.show();
        //==============================================3 从RDD创建Dataset StructType对象的使用
        JavaRDD<String> peopleRDD2 = sparkSession.sparkContext()
                .textFile("..\\sparkTestData\\people.txt", 1)
                .toJavaRDD();

        // 创建一个描述表结构的schema
        String schemaString = "name age";
        List<StructField> fields = new ArrayList<StructField>();
        for (String fieldName : schemaString.split(" ")) {
            StructField field = DataTypes.createStructField(fieldName, DataTypes.StringType, true);
            fields.add(field);
        }
        StructType schema = DataTypes.createStructType(fields);

        // Convert records of the RDD (people) to Rows
        JavaRDD<Row> rowRDD = peopleRDD2.map(new Function<String, Row>() {
            //@Override
            public Row call(String record) throws Exception {
                String[] attributes = record.split(",");
                return RowFactory.create(attributes[0], attributes[1].trim());
            }
        });

        // Apply the schema to the RDD
        Dataset<Row> peopleDataFrame = sparkSession.createDataFrame(rowRDD, schema);

        // Creates a temporary view using the DataFrame
        peopleDataFrame.createOrReplaceTempView("people");
        peopleDataFrame.show();
    }
    public static List<Person> createPersons(){
        List<Person> personpList = new ArrayList<Person>();
        Person person1 = new Person();
        person1.setId(5);
        person1.setName("Andy");
        person1.setAge(32);

        Person person2 = new Person();
        person1.setId(6);
        person2.setName("Justin");
        person2.setAge(19);
        personpList.add(person1);
        personpList.add(person2);

        return personpList;
    }
    public static class Person{
        private int id;
        private String name;
        private int age;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
