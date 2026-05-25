package com.themental1.notification_app

import android.content.Context
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.themental1.notification_app/config"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "setWebhookUrl" -> {
                        val url = call.argument<String>("url")
                        if (url != null) {
                            val prefs = getSharedPreferences("notification_config", Context.MODE_PRIVATE)
                            prefs.edit().putString("webhook_url", url).apply()
                            result.success(null)
                        } else {
                            result.error("INVALID_ARGUMENT", "URL cannot be null", null)
                        }
                    }
                    "getWebhookUrl" -> {
                        val prefs = getSharedPreferences("notification_config", Context.MODE_PRIVATE)
                        val url = prefs.getString("webhook_url", "")
                        result.success(url)
                    }
                    else -> result.notImplemented()
                }
            }
    }
}
