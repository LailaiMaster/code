package me.ztiany.flutter.flutter_basic

import android.content.Intent
import android.net.Uri
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant


class MainActivity : FlutterActivity() {

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        registerMethodChannel(flutterEngine)
        registerViewFactory(flutterEngine)
    }

    private fun registerViewFactory(flutterEngine: FlutterEngine) {
        flutterEngine.platformViewsController.registry.registerViewFactory("SampleView", SampleViewFactory(flutterEngine.dartExecutor))
    }

    private fun registerMethodChannel(flutterEngine: FlutterEngine) {
        MethodChannel(flutterEngine.dartExecutor, "samples.chenhang/navigation")
                .setMethodCallHandler { call, result ->
                    // Note: this method is invoked on the main thread.
                    Log.d("MainActivity", call.method)
                    if (call.method == "openAppStore") {
                        try {
                            val uri = Uri.parse("market://details?id=com.tencent.mm")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } catch (e: Exception) {
                            result.error("UNAVAILABLE", "没有安装应用市场", null)
                        }
                    } else {
                        result.notImplemented()
                    }
                }
    }

}
