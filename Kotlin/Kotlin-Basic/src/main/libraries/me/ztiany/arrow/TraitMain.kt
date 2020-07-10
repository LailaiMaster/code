package me.ztiany.arrow

/**在 Kotlin 中“实现”trait/类型类：https://hltj.me/kotlin/2020/01/11/kotlin-trait-typeclass.html*/
fun main() {

}

/*
trait 应同时具备以下三项能力
    定义“接口”并可提供默认实现
    用作泛型约束
    给既有类型增加功能

在 Kotlin 中并没有同时具备这三项能力的对应，只有分别提供三项能力的特性。 其中 Kotlin 的接口同时具备前两项能力。
*/

/*1 定义带默认实现的接口*/
interface WithDescription {
    val description: String
        get() = "The description of $this"
}

class Foo : WithDescription {
    // Foo 类为 description 属性提供了自己的实现
    override val description = "This is a Foo object"
}

// 对象 Bar 的 description 属性采用默认实现
object Bar : WithDescription

/*2 用作泛型约束*/
fun <T : WithDescription> T.printDescription() = println(description)

/*
3 在 Kotlin 中不能给既有类型（类或接口）实现新的接口，比如不能为 Boolean 或者 Iterable 实现 WithDescription。
即接口不具备第三项能力，因此它不是 trait/类型类。
 */

/*
4 在 Kotlin 中“实现”trait/类型类：把以上两个特性以某种方式结合起来，实现”trait/类型类
*/

/*定义带泛型的接口*/
interface WithDescriptionEnhance<T> {
    val T.description: String
        get() = "The description of $this"
}

//4.1 利用了分发接收者可以子类化、扩展接收者静态解析的特性，可以为任何既有类型添加实现：
object CharWithDescription : WithDescriptionEnhance<Char> {
    override val Char.description: String
        get() = "${this.category} $this"
}

// 采用默认实现
object StringWithDescription : WithDescriptionEnhance<String>

//4.2 用作泛型约束也不成问题
fun <T, Ctx : WithDescriptionEnhance<T>> Ctx.printDescription(t: T) = println(t.description)

//如果仍然希望目标类型（如例中的Char、String）作为printDescription 的接收者，只要将其接收者与参数互换即可
fun <T, Ctx : WithDescriptionEnhance<T>> T.printDescription2(ctx: Ctx) = ctx.run { println(description) }

//4.3 上述两种方式中提供泛型约束的上下文要么占用了函数的扩展接收者、要么占用了函数参数。实际上还有一种方式——占用分发接收者
interface WithDescriptionAndItsPrinter<T> : WithDescriptionEnhance<T> {
    fun T.printDescription() = println(description)
}

object StringWithDescriptionAndItsPrinter : WithDescriptionAndItsPrinter<String>
object CharWithDescriptionAndItsPrinter : WithDescriptionAndItsPrinter<Char>, WithDescriptionEnhance<Char> by CharWithDescription

private fun testWithDescriptionEnhance() {
    println(StringWithDescription.run { "hello".description })

    with(CharWithDescription) {
        println('a'.description)
    }

    StringWithDescription.run {
        printDescription("Kotlin")
    }
    CharWithDescription.run {
        printDescription('①')
    }

    "hltj.me".printDescription2(StringWithDescription)
    '`'.printDescription2(CharWithDescription)

    StringWithDescriptionAndItsPrinter.run {
            "hltj.me".printDescription()
    }

    CharWithDescriptionAndItsPrinter.run {
        '★'.printDescription()
    }

}