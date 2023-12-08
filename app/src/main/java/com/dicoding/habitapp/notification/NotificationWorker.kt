package com.dicoding.habitapp.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dicoding.habitapp.R
import com.dicoding.habitapp.ui.countdown.CountDownActivity
import com.dicoding.habitapp.ui.detail.DetailHabitActivity
import com.dicoding.habitapp.ui.list.HabitListActivity
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIFICATION_CHANNEL_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val habitId = inputData.getInt(HABIT_ID, 0)
    private val habitTitle = inputData.getString(HABIT_TITLE)

    override fun doWork(): Result {
        val prefManager =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify =
            prefManager.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)

        //TODO 12 : If notification preference on, show notification with pending intent

        if (shouldNotify) {
            showAlarmNotification(context = applicationContext)
        }
        return Result.success()
    }

    private fun showAlarmNotification(context: Context) {
        val title = habitTitle
        val message = context.getString(R.string.notify_content)
        val notificationIntent = Intent(context, HabitListActivity::class.java)

        val taskStackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
        taskStackBuilder.addParentStack(CountDownActivity::class.java)
        taskStackBuilder.addNextIntent(notificationIntent)

        val pendingIntent: PendingIntent = getPendingIntent(habitId)
        val managerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setSound(notificationSound)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "habit-notify",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000)
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            managerCompat.createNotificationChannel(channel)
        }

        notificationBuilder.setAutoCancel(true)
        val notification = notificationBuilder.build()
        notification.flags = Notification.FLAG_AUTO_CANCEL or Notification.FLAG_ONGOING_EVENT
        managerCompat.notify(101, notification)
        Log.d("NotificationWorker", "doWork() executed")
    }


    private fun getPendingIntent(id: Int): PendingIntent {
        val pendingIntent = Intent(applicationContext, DetailHabitActivity::class.java)
        pendingIntent.putExtra(HABIT_ID, id)
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(pendingIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
    }

}
