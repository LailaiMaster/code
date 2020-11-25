package me.ztiany.completablefuture

import java.util.concurrent.CompletableFuture

/**
 *
 * @author Ztiany
 *          Email ztiany3@gmail.com
 *          Date 2020/8/28 17:19
 */
private class Bitmap

private fun bitmapCompletableFuture(url: String): CompletableFuture<Bitmap> = CompletableFuture.supplyAsync {
    Bitmap()
}

private fun <T> List<CompletableFuture<T>>.allOf(): CompletableFuture<List<T>> {
    return CompletableFuture.allOf(*this.toTypedArray())
            .thenApply {
                this.map {
                    it.get()
                }
            }
}

fun main() {
    listOf("url1", "url2", "url3")
            .map { bitmapCompletableFuture(url = it) }
            .allOf()
            .thenApply {

            }
}