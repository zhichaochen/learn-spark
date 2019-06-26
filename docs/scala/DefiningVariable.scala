package scala

object DefiningVariable {
  def main(args: Array[String]): Unit = {
    /**
      * 变量的定义可以用关键字var和val修饰
      * var修饰的变量值可以更改
      * val修饰的变量值不可用改变，相当于Java中final修饰的变量
      * 定义变量格式如下 :
      * 方式一 : var | val 变量名称 : 类型 = 值
      * 方式二 : var | val 变量名称 = 值
      *
      */
    val name: String = "尹正杰"
    var age: Int = 26
    val blog = "http://www.cnblogs.com/yinzhengjie/tag/Scala%E8%BF%9B%E9%98%B6%E4%B9%8B%E8%B7%AF/"
    // 输出我们上面定义的变量
    println("姓名 :" + name, "年龄 :" + age, "博客地址 :" + blog)

    /**
      * Unit数据类型相当于Java中void关键字，但是在scala它的表示形式是一对括号，即"()"。
      * 由于我println返回值为空，因此我定义了一个变量res它拿到的返回值必然为空。
      */
    val res: Unit = println("yinzhengjie")
    println(res)

    /**
      * 文字'f'插值器允许创建一个格式化的字符串，类似于C语言中的printf。注意，如果你没有写文字'f'插值器的话，格式化字符串会原样输出哟
      * 在使用'f'插值器时，所有变量引用都应该是printf 样式格式说明符，如％d，％i，％f 等。
      */
    println(f"姓名 :$name%s 年龄 :$age, 博客地址 :$blog ") // 该行输出有换行
    /**
      * 's'允许在处理字符串时直接使用变量。
      * 在println 语句中将String 变量($name)附加到普通字符串中。
      */
    println(s"Name=$name , Age=$age , Url=$blog ")

    /**
      * 字符串插入器还可以处理任意表达式。
      * 使用's'字符串插入器处理具有任意表达式"${10 * 10}"的字符串"10 x 10"的以下代码片段。任何表达式都可以嵌入到${}中。
      */
    println(s"10 x 10 = ${10 * 10}")

    /**
      * 扩展小知识一:
      * 多个变量声明模式
      */
    val (x, y, z) = (100, 200, 300)
    println(s"x = ${x},y = ${y},z = ${z}")

    /**
      * 扩展小知识二
      * 抽取前两个元素依次赋值,目的只是关心a,b两个值，这样我们就可以直接输出a和b的值
      */
    val Array(a, b, _*) = Array("A", "B", "C", "D")
    println(s"a = ${a},b = ${b}")


  }
}
/*
68 以上代码输出结果如下 :
69 (姓名 :尹正杰,年龄 :26,博客地址 :http://www.cnblogs.com/yinzhengjie/tag/Scala%E8%BF%9B%E9%98%B6%E4%B9%8B%E8%B7%AF/)
70 yinzhengjie
71 ()
72 姓名 :尹正杰 年龄 :26, 博客地址 :http://www.cnblogs.com/yinzhengjie/tag/Scala%E8%BF%9B%E9%98%B6%E4%B9%8B%E8%B7%AF/
73 Name=尹正杰 , Age=26 , Url=http://www.cnblogs.com/yinzhengjie/tag/Scala%E8%BF%9B%E9%98%B6%E4%B9%8B%E8%B7%AF/
74 10 x 10 = 100
75 x = 100,y = 200,z = 300
76 a = A,b = B
77  */