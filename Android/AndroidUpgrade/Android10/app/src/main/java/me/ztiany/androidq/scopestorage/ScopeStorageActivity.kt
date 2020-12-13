package me.ztiany.androidq.scopestorage

import android.app.Activity
import android.content.Intent
import android.media.MediaFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.scope_storage_activity.*
import me.ztiany.androidq.R
import java.io.File
import java.nio.file.Files


private const val TAG = "ScopeStorage"

private const val REQUEST_CODE_FOR_DIR = 1001

/**
 *@author Ztiany
 *      Email: ztiany3@gmail.com
 *      Date : 2020-08-06 11:34
 */
class ScopeStorageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scope_storage_activity)

        AndPermission.with(this)
            .runtime()
            .permission(Permission.Group.STORAGE)
            .onGranted {
                setViews()
            }
            .start()
    }

    private fun setViews() {
        accessSDCard.setOnClickListener {
            accessSdCard()
        }

        requestUriTree.setOnClickListener {
            requestUrlTree()
        }

        accessAppSpecific.setOnClickListener {
            accessAppSpecific()
        }

    }

    private fun accessAppSpecific() {
        val externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: return
        Log.d(TAG, externalFilesDir.absolutePath)
        Files.write(File(externalFilesDir, "abc").toPath(), listOf("abc"))
    }

    private fun requestUrlTree() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, REQUEST_CODE_FOR_DIR)
    }

    private fun handleRequestUriTree(data: Intent) {
        val uriTree: Uri? = data.data
        if (uriTree != null) {
            // create DocumentFile which represents the selected directory
            val root = DocumentFile.fromTreeUri(this, uriTree) ?: return
            // list all sub dirs of root
            val files = root.listFiles()
            // do anything you want with APIs provided by DocumentFile
            files.forEach {
                Log.d(TAG, it.name)
                it.createFile(MediaFormat.MIMETYPE_TEXT_CEA_608, "a")
            }
        }
    }

    private fun accessSdCard() {
        //即使获取了读写权限，直接操作外部存储也会导致应用直接崩溃。
        //java.nio.file.AccessDeniedException: /storage/emulated/0/abc.txt
        val externalStorageState = Environment.getExternalStorageDirectory()
        Log.d(TAG, externalStorageState.absolutePath)
        Files.write(File(externalStorageState, "abc.txt").toPath(), listOf("abc"))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(
            TAG,
            "onActivityResult() called with: requestCode = $requestCode, resultCode = $resultCode, data = $data"
        )
        if (Activity.RESULT_OK != resultCode) {
            return
        }
        if (requestCode == REQUEST_CODE_FOR_DIR) {
            data?.let(::handleRequestUriTree)
        }
    }

}