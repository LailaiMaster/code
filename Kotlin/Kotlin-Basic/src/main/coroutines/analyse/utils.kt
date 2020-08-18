package analyse

import java.text.SimpleDateFormat
import java.util.*


val now = {
    SimpleDateFormat("HH:mm:ss:SSS").format(Date(System.currentTimeMillis()))
}

fun log(msg: Any?) = println("${now()} [${Thread.currentThread().name}] $msg")
