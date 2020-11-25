package book.ch04_practice.kotlin

fun main() {

    val fibonacci = sequence {
        yield(1)
        var current = 1
        var next = 1
        while (true) {
            yield(next)
            next += current
            current = next - current
        }
    }

    for (i in fibonacci.take(10)) {
        println(i)
    }

}