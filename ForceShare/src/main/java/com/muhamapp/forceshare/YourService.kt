package com.muhamapp.forceshare

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask

class YourService : Service() {
    var counter = 0
    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startMyOwnForeground() else startForeground(
            1,
            Notification()
        )
    }

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {

        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID.toString(),
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!
        manager.createNotificationChannel(chan)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID.toString())
        val notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(com.google.android.material.R.drawable.ic_arrow_back_black_24)
            .setOngoing(true)
            .setContentInfo("percent" + "%")
            .setProgress(100, 0, false)
            .build()

        startForeground(NOTIFICATION_CHANNEL_ID, notification)

        CoroutineScope(Dispatchers.IO).launch {
            for (i in 1..100) {
                delay(500)
                updateProgress(i, notificationBuilder, manager)
                if (i==10) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
//                    manager.notify(NOTIFICATION_CHANNEL_ID, notificationBuilder.build())
//                    manager.cancel(NOTIFICATION_CHANNEL_ID)
                    break
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateProgress(i: Int, notificationBuilder: NotificationCompat.Builder, manager: NotificationManager) {
        notificationBuilder.setContentText("$i%")
            .setContentTitle("Sedang Mengirim Report")
            //.setSmallIcon(android.R.drawable.stat_sys_download)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(com.google.android.material.R.drawable.ic_call_answer)
            .setOngoing(true)
            .setContentInfo("$i%")
            .setProgress(100, i, false)
        manager.notify(NOTIFICATION_CHANNEL_ID, notificationBuilder.build())

        Log.d("kocak", "start $i")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stoptimertask()
        // Ini akan dijalankan kembali meskipun stop forego
        val broadcastIntent = Intent()
        broadcastIntent.setAction("restartservice")
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    fun startTimer() {
//        timer = Timer()
//        timerTask = object : TimerTask() {
//            override fun run() {
//                Log.i("Count", "=========  " + counter++)
//            }
//        }
//        timer!!.schedule(timerTask, 1000, 1000) //
    }

    fun stoptimertask() {
//        if (timer != null) {
//            timer!!.cancel()
//            timer = null
//        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object{
        const val NOTIFICATION_CHANNEL_ID = 111
    }
}