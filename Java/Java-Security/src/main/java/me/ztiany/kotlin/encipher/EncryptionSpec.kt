package me.ztiany.kotlin.encipher

import java.security.KeyFactory


/**
 *
 * @author Ztiany
 *          Email ztiany3@gmail.com
 *          Date 2020/2/26 16:43
 */
fun main() {
    val instance = KeyFactory.getInstance("RSA")
    println(instance.algorithm)
}