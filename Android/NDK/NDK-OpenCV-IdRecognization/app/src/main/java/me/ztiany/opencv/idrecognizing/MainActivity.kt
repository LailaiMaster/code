package me.ztiany.opencv.idrecognizing

import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val log = fun(message: String) {
        Log.d("MainActivity", message)
    }

    private var tessBaseAPI: TessBaseAPI? = null

    private val systemMediaSelector by lazy {
        SystemMediaSelector(SystemMediaSelectorResultListener { path: String? ->
            log("path = $path")
            path?.let(::processImage)
        }, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askPermission()

        btnSelectImage.setOnClickListener {
            systemMediaSelector.takeFile("image/*")
        }

        btnInitTess.setOnClickListener {
            initTessTwo()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun askPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
        } else {
            initTessTwo()
        }
    }

    private fun initTessTwo() {
        TessTwoUtils.initTess(this, object : TessTwoUtils.CallBack {
            override fun onSuccess(result: TessBaseAPI?) {
                tessBaseAPI = result
                tvTessStatus.text = "Tess初始化成功"
            }

            override fun onProgressing() {
                Toast.makeText(this@MainActivity, "Tess is initializing", Toast.LENGTH_LONG).show()
                tvTessStatus.text = "Tess初始化中"
            }

            override fun onError() {
                Toast.makeText(this@MainActivity, "Tess init failed", Toast.LENGTH_LONG).show()
                tvTessStatus.text = "Tess初始化失败"
            }
        })
    }

    private fun processImage(path: String) {
        val bitmap = BitmapFactory.decodeFile(path)
        val number = ImageProcess.getIdNumber(BitmapUtils.toBitmap(path), Bitmap.Config.ARGB_8888)
        ivOrigin.setImageBitmap(bitmap)
        ivResult.setImageBitmap(number)

        val baseAPI = tessBaseAPI ?: return
        baseAPI.setImage(number)
        tvResult.text = baseAPI.utF8Text
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        systemMediaSelector.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        tessBaseAPI?.end()
    }

    companion object {
        private const val PERMISSION_CODE = 100
    }

}
