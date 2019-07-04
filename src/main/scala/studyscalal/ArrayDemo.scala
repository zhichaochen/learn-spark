package studyscalal

import scala.collection.mutable.ArrayBuffer

object ArrayDemo {
  def main(args: Array[String]): Unit = {
    //val arr = Array(1,2,3,4,5,6)
    //filter一个过滤器,根据传入的条件将数据过滤出来
    //val arr1: Array[Int] = arr.filter(x => x%2 == 0)

    //map把数组中每一个元素都取出来的到一个全新数组
    //val arr2: Array[Int] = arr.map(x => x)

    //foreach数组中的元素取出来并打印 (无返回值)
    //arr.foreach(x => println(x))
    //========================================
    //定义数组
    var arr:Array[Int] = new Array[Int](3)
    //简化
    var arr1 = new Array[Int](3)
    val arr2 = Array(1,2,3)
    //操作当前数组,下标
    arr2(0) = 100
    //翻转数组
    println(arr.reverse.toBuffer) //需转换成StringBuffer后输出,不然输出的会是地址
    //删除数组元素 这时候操作的是可变数组

  }

  def main1(args: Array[String]): Unit = {
    val  arr = new ArrayBuffer[Int](4)
    //删除数组使用remove(), 这个方法有两种用法
    arr.remove(2)  //删除arr数组下标为2的元素
    arr.remove(2,3)   //从下标2开始,删除3个元素

    //数组的排序，有三种方法
    //1、sorted   sorted默认为升序排序, 如果想要降序需要进行反转
    //val sortedArray : Array[Int] = arr.sorted     //升序
    //val sortedArray : Array[Int] = arr.sorted.reverse   //降序

    //2、sortWith需要传一个参数, 参数是一个函数, 这个函数需要有两个参数进行比较, 返回的是一个布尔类型的值
    //1、val f1 = (x : Int , y: Int) => x < y
    //val sortwithArray : ArrayBuffer[Int] = arr.sortWith(f1)
    //2. 简化一下
    //val sortwithArray = arr.sortWith((x,y) => x < y)
    //3. 最简化
    //val sortwithArray = arr.sortWith(_ < _)

    //3 sortBy
    /**
      * sortBy也是需要出入参数的
      * f:(Int)=>B代表传入一个参数，这个参数类型是Int类型，这个函数的返回值是B类型的
      * 后面的implicit ord:Ordering[B]这个是排序的规则
      */
    //val sortBy = arr.sortBy(x => x)   //升序
    //val sortBy = arr.sortBy(x => -x)   //降序
  }
}
