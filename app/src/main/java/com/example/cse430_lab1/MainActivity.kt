package com.example.cse430_lab1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.app.NotificationManager
import android.content.Context
import android.app.NotificationChannel
import androidx.core.app.NotificationCompat
import android.content.Intent
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import com.example.cse430_lab1.Constants.Companion.ACTION_DISMISS_NOTIFICATION
import com.example.cse430_lab1.Constants.Companion.ACTION_UPDATE_NOTIFICATION
import com.example.cse430_lab1.Constants.Companion.NOTIFICATION_ID
import com.example.cse430_lab1.Constants.Companion.PRIMARY_CHANNEL_ID


class MainActivity : AppCompatActivity() {


    private val mReceiver: NotificationReceiver = NotificationReceiver()
    private var mNotifyManager: NotificationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mReceiver.mainActivity = this
        notify.setOnClickListener { sendNotification() }
        update.setOnClickListener { updateNotification() }
        cancel.setOnClickListener { cancelNotification() }
        createNotificationChannel()
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )
        val intentFilter =  IntentFilter().also {
            it.addAction(ACTION_UPDATE_NOTIFICATION)
            it.addAction(ACTION_DISMISS_NOTIFICATION)
        }
        registerReceiver(mReceiver, intentFilter)
    }

    private fun cancelNotification() {
        mNotifyManager!!.cancel(NOTIFICATION_ID)
        setNotificationButtonState(
            isNotifyEnabled = true,
            isUpdateEnabled = false,
            isCancelEnabled = false
        )
    }

    fun updateNotification() {
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.setStyle(NotificationCompat.InboxStyle()
                .setSummaryText("Hey you")
                .addLine("Such a time to be alive")
                .addLine("such wow")
                .addLine("so amaze")
                .setBigContentTitle("Notification Updated!"))
        mNotifyManager!!.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = false,
            isCancelEnabled = true
        )
    }

    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT)
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder.addAction(R.drawable.ic_update, "Update Notification", updatePendingIntent)
        mNotifyManager!!.notify(NOTIFICATION_ID, notifyBuilder.build())
        setNotificationButtonState(
            isNotifyEnabled = false,
            isUpdateEnabled = true,
            isCancelEnabled = true
        )
    }

    private fun createNotificationChannel() {
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Mascot Notification", NotificationManager
                    .IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notification from Mascot"
            mNotifyManager!!.createNotificationChannel(notificationChannel)
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("You've been notified!")
            .setContentText("This is your notification text.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(notificationPendingIntent)
            .setAutoCancel(true)
            .setDeleteIntent(getDeleteIntent())
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
    }

    fun setNotificationButtonState(isNotifyEnabled: Boolean,
                                   isUpdateEnabled: Boolean,
                                   isCancelEnabled: Boolean) {
        notify.isEnabled = isNotifyEnabled
        update.isEnabled = isUpdateEnabled
        cancel.isEnabled = isCancelEnabled
    }

    private fun getDeleteIntent(): PendingIntent {
        val intent = Intent().also {
            it.action = ACTION_DISMISS_NOTIFICATION
        }
        return PendingIntent.getBroadcast (this, 0 , intent , PendingIntent. FLAG_ONE_SHOT )
    }

    override fun onDestroy() {
        mReceiver.mainActivity = null
        unregisterReceiver(mReceiver)
        super.onDestroy()
    }

    class NotificationReceiver : BroadcastReceiver() {
        var mainActivity: MainActivity? = null
        override fun onReceive(context: Context, intent: Intent) {
            if(intent.action == ACTION_DISMISS_NOTIFICATION) {
                mainActivity?.setNotificationButtonState(
                    isNotifyEnabled = true,
                    isUpdateEnabled = false,
                    isCancelEnabled = false
                )
                return
            }
            mainActivity?.updateNotification()
        }
    }
}


