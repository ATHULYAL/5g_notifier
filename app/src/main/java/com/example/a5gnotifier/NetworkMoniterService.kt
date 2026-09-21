package com.example.a5gnotifier

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder

class NetworkMonitorService : Service() {

    companion object {
        private const val CHANNEL_ID = "network_monitor_channel"
        private const val NOTIFICATION_ID = 1
    }

    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()

        notificationManager =
            getSystemService(NotificationManager::class.java)

        createNotificationChannel()

        // Initial notification
        updateNotification("Detecting...")

        // Start monitoring network
        networkMonitor = NetworkMonitor(this) { network ->
            updateNotification(network)
        }

        networkMonitor.start()
    }

    private fun updateNotification(network: String) {

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("5G Notifier")
            .setContentText("📶 Current Network: $network")
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        networkMonitor.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Network Monitor",
                NotificationManager.IMPORTANCE_LOW
            )

            notificationManager.createNotificationChannel(channel)
        }
    }
}