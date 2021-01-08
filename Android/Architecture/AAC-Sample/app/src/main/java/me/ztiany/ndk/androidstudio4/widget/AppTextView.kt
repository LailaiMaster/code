package me.ztiany.ndk.androidstudio4.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import me.ztiany.ndk.androidstudio4.TAG

/**
 *@author Ztiany
 *      Email: ztiany3@gmail.com
 *      Date : 2020-09-02 12:46
 */
class AppTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun getSupportBackgroundTintList(): ColorStateList? {
        val supportBackgroundTintList = super.getSupportBackgroundTintList()
        Log.d(TAG, "getSupportBackgroundTintList() called")
        return supportBackgroundTintList
    }

}