package com.parsanatech.crazycoder

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId="NotificationChannel"
const val channelName="CrazyCoder"

class MyFirebaseMessagingService: FirebaseMessagingService() {

    fun generateNotification(title:String,message:String)
    {
        val intent=Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        var builder:NotificationCompat.Builder=NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.logo1)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder=builder.setContent(getRemoteView(title,message))

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            val notifiacionChannel=NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notifiacionChannel)
        }
        notificationManager.notify(0,builder.build())

    }

    override fun onMessageReceived(message: RemoteMessage) {

        if(message.notification!=null)
        {
            generateNotification(message.notification!!.title!!, message.notification!!.body!!)
        }

    }

    @SuppressLint("RemoteViewLayout")
    private fun getRemoteView(title: String, message: String): RemoteViews {
        val remoteView=RemoteViews("com.parsanatech.crazycoder",R.layout.notification)
        remoteView.setTextViewText(R.id.title,title)
        remoteView.setTextViewText(R.id.message,message)
        remoteView.setImageViewResource(R.id.applogo,R.drawable.logo1)

        return remoteView
    }

}