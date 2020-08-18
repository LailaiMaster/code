package analyse

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *
 * @author Ztiany
 *          Email ztiany3@gmail.com
 *          Date 2019/8/23 0:27
 */
suspend fun main() {
    GlobalScope.launch {
        log("me.ztiany.tools.main")
    }.join()
}