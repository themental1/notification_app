package gr.ornette.notification_app

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

class NotificationListenerService : NotificationListenerService() {
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
        val webhookUrl = prefs.getString("webhook_url", null) ?: return

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
            val urlObj = URL(url)
            val connection = urlObj.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val payload = JSONObject().apply {
                put("packageName", packageName)
                put("title", title)
                put("text", text)
                put("subText", subText)
                put("tag", tag)
                put("priority", priority)
                put("timestamp", System.currentTimeMillis())
            }

            val outputStream: OutputStream = connection.outputStream
            outputStream.write(payload.toString().toByteArray(Charsets.UTF_8))
            outputStream.close()

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
                android.util.Log.e("NotificationListener", "Webhook failed: $responseCode")
            }
            connection.disconnect()
        } catch (e: Exception) {
            android.util.Log.e("NotificationListener", "Error sending webhook", e)
        }
    }
}
