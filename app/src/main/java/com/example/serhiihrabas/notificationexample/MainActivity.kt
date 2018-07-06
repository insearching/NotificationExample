package com.example.serhiihrabas.notificationexample

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button


class MainActivity : AppCompatActivity() {

    private val DELETE_ACTION = "com.example.serhiihrabas.notificationexample"
    private val CHANNEL_ID: String = "my_channel_id"
    private val GROUP_KEY = "com.example.serhiihrabas.notificationexample.GROUP_NAME"

    private var notificationId = 0
    private var groupNotificationDismissed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.notification_button_single).setOnClickListener {
            showSingleNotification("My very loooooooooooooooong message", "My short message")
        }

        findViewById<Button>(R.id.notification_button).setOnClickListener {
            showNotification("My very loooooooooooooooong message", "My short message")
        }

        registerReceiver(DeleteReceiver(), IntentFilter(DELETE_ACTION))
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun showSingleNotification(message: String, shortMessage: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(this, CHANNEL_ID)
        }
        val intent = Intent(this, MainActivity::class.java)

        val notification = Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(shortMessage)
                .setStyle(Notification.BigTextStyle().bigText(message))
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setAutoCancel(true)
                .setGroup(GROUP_KEY)
                .setGroupSummary(true)
                .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(generateNotificationId(), notification)
    }

    private fun showNotification(message: String, shortMessage: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(this, CHANNEL_ID)
        }
        val intent = Intent(this, MainActivity::class.java)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(shortMessage)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setAutoCancel(true)
                .setGroup(GROUP_KEY)
                .build()

        if (groupNotificationDismissed) {
            showGroupNotification()
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(generateNotificationId(), notification)
    }

    private fun showGroupNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(this, CHANNEL_ID)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.app_name))
                .setDeleteIntent(PendingIntent.getBroadcast(this, 0, Intent(DELETE_ACTION), 0))
                .setAutoCancel(true)
                .setGroup(GROUP_KEY)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroupSummary(true)
                .build()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(generateNotificationId(), notification)
        groupNotificationDismissed = false
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, id: String) {
        val channel = NotificationChannel(id, context.getString(R.string.chanel_name),
                NotificationManager.IMPORTANCE_HIGH)
        channel.description = context.getString(R.string.chanel_description)
        channel.enableLights(true)
        channel.setShowBadge(true)
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun generateNotificationId() = ++notificationId

    inner class DeleteReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            groupNotificationDismissed = true
        }
    }
}
