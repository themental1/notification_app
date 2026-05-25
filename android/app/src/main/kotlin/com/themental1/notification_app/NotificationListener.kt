package com.themental1.notification_app

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import io.flutter.embedding.engine.FlutterEngine
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class NotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val notification = sbn.notification
        val extras = notification.extras

        val packageName = sbn.packageName
        val title = extras.getString("android.title") ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val subText = extras.getCharSequence("android.subText")?.toString()
        val tag = sbn.tag
        val priority = notification.priority

        val prefs = getSharedPreferences("notification_config", Context.MODE_PRIVATE)
        val webhookUrl = prefs.getString("webhook_url", "http://10.0.10.125:8082/log")
        
        android.util.Log.d("NotificationListener", "onNotificationPosted called for: $packageName")
        android.util.Log.d("NotificationListener", "webhook_url from SharedPreferences: '$webhookUrl'")
        
        if (webhookUrl.isNullOrEmpty()) {
            android.util.Log.w("NotificationListener", "webhook_url is null or empty, ignoring notification")
            return
        }

        android.util.Log.d("NotificationListener", "Starting webhook thread for URL: $webhookUrl")
        Thread {
            sendToWebhook(
                webhookUrl,
                packageName,
                title,
                text,
                subText,
                tag,
                priority
            )
        }.start()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle notification removal if needed
    }

    private fun sendToWebhook(
        url: String,
        packageName: String,
        title: String,
        text: String,
        subText: String?,
        tag: String?,
        priority: Int
    ) {
        try {
            android.util.Log.d("NotificationListener", "sendToWebhook called with URL: $url")
            
            val urlObj = URL(url)
            val connection = urlObj.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val payload = JSONObject().apply {
                put("packageName", packageName)
                put("title", title)
                put("text", text)
                put("subText", subText)
                put("tag", tag)
                put("priority", priority)
                put("timestamp", System.currentTimeMillis())
            }

            android.util.Log.d("NotificationListener", "Sending payload: ${payload.toString()}")
            
            val outputStream: OutputStream = connection.outputStream
            outputStream.write(payload.toString().toByteArray(Charsets.UTF_8))
            outputStream.close()

            val responseCode = connection.responseCode
            android.util.Log.d("NotificationListener", "Webhook response code: $responseCode")
            
            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
                android.util.Log.e("NotificationListener", "Webhook failed: $responseCode")
            } else {
                android.util.Log.d("NotificationListener", "Webhook sent successfully")
            }
            connection.disconnect()
        } catch (e: Exception) {
            android.util.Log.e("NotificationListener", "Error sending webhook: ${e.message}", e)
        }
    }
}
