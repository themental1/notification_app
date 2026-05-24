package gr.ornette.notification_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Optionally start the notification listener service or any background work
            android.util.Log.d("BootReceiver", "Device boot completed")
        }
    }
}
