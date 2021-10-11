package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat


// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context,
                                         fileName: String, status: Boolean) {
    // Create the content intent for the notification, which launches this activity
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    //add status action
    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    detailIntent.putExtra(DetailActivity.FILENAME_EXTRA, fileName)
    detailIntent.putExtra(DetailActivity.STATUS_EXTRA, status)

    // Create the TaskStackBuilder
    val detailPendingIntent: PendingIntent? = TaskStackBuilder.create(applicationContext).run {
        // Add the intent, which inflates the back stack
        addNextIntentWithParentStack(detailIntent)
        // Get the PendingIntent containing the entire back stack
        getPendingIntent(REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    // get an instance of NotificationCompat.Builder
    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )

        // set title, text and icon to builder
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)

        // set content intent
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)

        // add detail action
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.status),
            detailPendingIntent
        )

        // set priority
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    // call notify
    notify(NOTIFICATION_ID, builder.build())
}
