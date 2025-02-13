package com.baljeet.expirytracker.util

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.baljeet.expirytracker.MainActivity
import com.baljeet.expirytracker.R
import com.baljeet.expirytracker.data.AppDatabase
import com.baljeet.expirytracker.data.repository.TrackerRepository
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random


class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        SharedPref.init(context)
        val trackerId = intent.getIntExtra(titleExtra, 0)
        if(trackerId == 8888){
            showDailyNotification(context)
        }else{
            showNormalNotification(context, trackerId)
        }
    }

    private fun showDailyNotification(context : Context){
        val repository = TrackerRepository(AppDatabase.getDatabase(context).trackerDao())
        val trackers = repository.getActiveTrackers()
        if(trackers.isEmpty()){
            showAddProductReminder(context)
        }else{
             showReportReminder(context)
        }
    }

    private fun showReportReminder(context: Context){
        if(SharedPref.isAlertEnabled && SharedPref.isDailyAlertEnabled){
            val collapsed =
                RemoteViews(context.packageName, R.layout.daily_reminder_report)
            val pendingIntent = NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.main_nav)
                .setDestination(R.id.dashFragment)
                .createPendingIntent()

            val builder = NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_app_icon_svg)
                .setContentTitle(context.getString(R.string.app_name))
                .setCustomContentView(collapsed)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.notify(8888, builder.build())
        }
    }

    private fun showAddProductReminder(context : Context){
        if(SharedPref.isAlertEnabled && SharedPref.isDailyAlertEnabled){
            val collapsed =
                RemoteViews(context.packageName, R.layout.daily_reminder_add_product)

            when((1..3).random()){
                1 ->{
                    collapsed.setImageViewResource(
                        R.id.product_image, context.resources.getIdentifier(
                            "ic_girl_48",
                            "drawable",
                            context.packageName
                        )
                    )
                }
                2->{
                    collapsed.setImageViewResource(
                        R.id.product_image, context.resources.getIdentifier(
                            "ic_peruvian",
                            "drawable",
                            context.packageName
                        )
                    )
                }
                3->{
                    collapsed.setImageViewResource(
                        R.id.product_image, context.resources.getIdentifier(
                            "ic_woman",
                            "drawable",
                            context.packageName
                        )
                    )
                }else->{
                    collapsed.setImageViewResource(
                        R.id.product_image, context.resources.getIdentifier(
                            "ic_peruvian",
                            "drawable",
                            context.packageName
                        )
                    )
                }
            }
            val pendingIntent = NavDeepLinkBuilder(context)
                .setComponentName(MainActivity::class.java)
                .setGraph(R.navigation.main_nav)
                .setDestination(R.id.addTrackerV2)
                .createPendingIntent()

            val builder = NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_app_icon_svg)
                .setContentTitle(context.getString(R.string.app_name))
                .setCustomContentView(collapsed)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.notify(8888, builder.build())
        }
    }


    private fun showNormalNotification(context:Context, trackerId : Int){
        if(SharedPref.isAlertEnabled) {
            try {
                val repository = TrackerRepository(AppDatabase.getDatabase(context).trackerDao())

                val tracker = repository.getTrackerByID(trackerId)

                val collapsed =
                    RemoteViews(context.packageName, R.layout.notification_collapsed_layout)
                val expanded =
                    RemoteViews(context.packageName, R.layout.notification_expanded_layout)

                expanded.setTextViewText(
                    R.id.product_name,
                    tracker.productAndCategoryAndImage.product.name
                )
                when (tracker.productAndCategoryAndImage.image.mimeType) {
                    "asset" -> {
                        expanded.setImageViewResource(
                            R.id.product_image, context.resources.getIdentifier(
                                tracker.productAndCategoryAndImage.image.imageUrl,
                                "drawable",
                                context.packageName
                            )
                        )
                        collapsed.setImageViewResource(
                            R.id.product_image, context.resources.getIdentifier(
                                tracker.productAndCategoryAndImage.image.imageUrl,
                                "drawable",
                                context.packageName
                            )
                        )
                    }
                    else -> {
                        expanded.setImageViewBitmap(
                            R.id.product_image,
                            ImageConvertor.stringToBitmap(tracker.productAndCategoryAndImage.image.bitmap)
                        )
                        collapsed.setImageViewBitmap(
                            R.id.product_image,
                            ImageConvertor.stringToBitmap(tracker.productAndCategoryAndImage.image.bitmap)
                        )
                    }
                }
                val expiryDate = tracker.tracker.expiryDate
                val mfgDate = tracker.tracker.mfgDate
                expiryDate?.let {
                    expanded.setTextViewText(
                        R.id.expiring_date,
                        context.getString(
                            R.string.date_var,
                            it.month.name.substring(0, 3).uppercase(),
                            it.dayOfMonth,
                            it.year
                        )
                    )
                }
                val dateToday = LocalDateTime.now()

                val totalHours = Duration.between(mfgDate, expiryDate).toMinutes()
                val spentHours = Duration.between(mfgDate, dateToday).toMinutes()

                val progressValue =
                    (spentHours.toFloat() / totalHours.toFloat()) * 100
                expanded.setProgressBar(R.id.item_progressbar, 100, progressValue.toInt(), false)
                when {
                    progressValue >= 100 -> {
                        expanded.setTextViewText(
                            R.id.tracking_status,
                            context.getText(R.string.expired)
                        )
                        collapsed.setTextViewText(
                            R.id.message_text,
                            "${tracker.productAndCategoryAndImage.product.name} has expired."
                        )
                    }
                    progressValue >= 80 -> {
                        expanded.setTextViewText(
                            R.id.tracking_status,
                            context.getText(R.string.expiring)
                        )
                        collapsed.setTextViewText(
                            R.id.message_text,
                            "${tracker.productAndCategoryAndImage.product.name} is expiring soon"
                        )
                    }
                    progressValue < 80 && progressValue >= 50 -> {
                        expanded.setTextViewText(
                            R.id.tracking_status,
                            context.getText(R.string.still_ok)
                        )
                        collapsed.setTextViewText(
                            R.id.message_text,
                            "Tracking update about ${tracker.productAndCategoryAndImage.product.name}"
                        )
                    }
                    progressValue < 50 -> {
                        expanded.setTextViewText(
                            R.id.tracking_status,
                            context.getText(R.string.fresh)
                        )
                        collapsed.setTextViewText(
                            R.id.message_text,
                            "Tracking update about ${tracker.productAndCategoryAndImage.product.name}"
                        )
                    }
                }
                val args = Bundle()
                args.putInt("selectedTrackerId", tracker.tracker.trackerId)
                val pendingIntent = NavDeepLinkBuilder(context)
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.main_nav)
                    .setDestination(R.id.trackerDetails)
                    .setArguments(args)
                    .createPendingIntent()

                val builder = NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.drawable.ic_app_icon_svg)
                    .setCustomContentView(collapsed)
                    .setCustomBigContentView(expanded)
                    .setContentIntent(pendingIntent)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                
                val manager = context.getSystemService(NotificationManager::class.java)
                manager.notify(trackerId, builder.build())
            } catch (e: Exception) {
                Log.d("Log for notification error - ", "${e.printStackTrace()}")
            }
        }else{
            Log.d("Log for - ","Notification received but ignored because of preferences")
        }
    }
}
