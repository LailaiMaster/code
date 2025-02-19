package com.bennyhuo.github.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bennyhuo.common.log.logger
import com.bennyhuo.github.R
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.nestedScrollView
import kotlin.concurrent.thread

/**
 * dsl 和 xml 性能对比
 *
 * Created by benny on 7/9/17.
 */
class PerformanceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thread {
            // 提前加载 dsl 要用到的类，防止类加载影响测试
            PerformanceFragmentUI().createView(AnkoContext.create(container!!.context, this))
            //提前加载 xml 要用到的类，防止类加载影响测试（这里要使用与实际测试时不一样的布局，因为 Android 对 xml 是有缓存的）
            inflater.inflate(R.layout.activity_login, container, false)
            System.nanoTime()

            fun cost(tag: String, block: () -> Unit) {
                System.gc()
                System.gc()
                val start = System.nanoTime()
                //只能重复一次，因为 Android 对 xml 是有缓存的。
                repeat(1) {
                    block()
                }
                val cost = System.nanoTime() - start
                logger.error("$tag: $cost")
            }

            cost("dsl") {
                PerformanceFragmentUI().createView(AnkoContext.create(container!!.context, this))
            }

            cost("xml") {
                inflater.inflate(R.layout.fragment_about, container, false)
            }
        }
        return PerformanceFragmentUI().createView(AnkoContext.create(container!!.context, this))
    }
}

class PerformanceFragmentUI : AnkoComponent<PerformanceFragment> {
    override fun createView(ui: AnkoContext<PerformanceFragment>) = ui.apply {
        nestedScrollView {
            verticalLayout {

                imageView {
                    imageResource = R.mipmap.ic_launcher
                }.lparams(width = wrapContent, height = wrapContent) {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                themedTextView("GitHub", R.style.detail_title) {
                    textColor = R.color.colorPrimary
                }.lparams(width = wrapContent, height = wrapContent) {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                themedTextView("By Bennyhuo", R.style.detail_description) {
                    textColor = R.color.colorPrimary
                }.lparams(width = wrapContent, height = wrapContent) {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
                themedTextView(R.string.open_source_licenses, R.style.detail_description) {
                    textColor = R.color.colorPrimary
                }.lparams(width = wrapContent, height = wrapContent) {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
            }.lparams(width = wrapContent, height = wrapContent) {
                gravity = Gravity.CENTER
            }
        }

    }.view
}