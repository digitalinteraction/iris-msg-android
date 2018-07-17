package uk.ac.ncl.openlab.irismsg.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasServiceInjector
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.activity.DonateActivity
import uk.ac.ncl.openlab.irismsg.activity.OrganisationListActivity
import javax.inject.Inject


/**
 * A service to respond to push notifications
 */
class FirebaseService : FirebaseMessagingService(), HasServiceInjector {
    
    // Dagger injection point
    @Inject lateinit var injector: DispatchingAndroidInjector<Service>
    override fun serviceInjector() : AndroidInjector<Service> = injector
    
    
    /** Handle receiving fcm */
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("msg", message.toString())
        
        when (message.data[FCM_TYPE_KEY]) {
            TYPE_NEW_DONATIONS -> displayNewDonationsNotification(message)
            else -> Log.e("fcm", "unknown type ${message.data[FCM_TYPE_KEY]}")
        }
    }
    
    /** Display a new donations message */
    private fun displayNewDonationsNotification (message: RemoteMessage) {
        
        val pendingIntent = PendingIntent.getActivities(
            this,
            0,
            arrayOf(
                Intent(this, OrganisationListActivity::class.java),
                Intent(this, DonateActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            ),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_DONATIONS)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(message.notification?.title ?: getString(R.string.title_new_donations_notif))
                .setContentText(message.notification?.body ?: getString(R.string.body_new_donations_notif))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setContentIntent(pendingIntent)
                .build()
        
        NotificationManagerCompat.from(this)
                .notify(NEW_DONATION_NOTIFICATION, notification)
    }
    
    companion object {
        const val FCM_TYPE_KEY = "type"
        
        const val TYPE_NEW_DONATIONS = "new_donations"
        
        const val CHANNEL_DONATIONS = "donation_channel"
        
        const val NEW_DONATION_NOTIFICATION = 1
        
        const val ACTION_DONATE = 10
    }
}