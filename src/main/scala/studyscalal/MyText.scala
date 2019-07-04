package studyscalal

object MyText {
  def main(args: Array[String]): Unit = {
    val list: List[String] = List("a", "b" ,"a")
    //为列表【预添加】元素
    println("A" +: list) //List(A, a, b, a)
    println(list +: "A" )//Vector(List(a, b, a), A)

    println(list :+ "1")//List(a, b, a, 1)

    println("1" :+ list)//Vector(1, List(a, b, a))
    println(list.init) //List(a, b)

    println("c" :: list)
    println(list :: "c")


  }

}
