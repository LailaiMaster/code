package me.ztiany.mmkv.simulation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val nativeBridge by lazy { NativeBridge() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        askPermission()

        btnInit.setOnClickListener {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "mmap/test.txt")
            file.createNew()
            nativeBridge.mmapInit(file.absolutePath)
        }

        btnMMapWrite.setOnClickListener {
            nativeBridge.mmapWrite()
        }

        btnMMapRead.setOnClickListener {
            nativeBridge.mmapRead()
        }
    }

    private fun askPermission() {
        AndPermission.with(this)
            .runtime()
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .onDenied {
                supportFinishAfterTransition()
            }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeBridge.destroy()
    }

}
