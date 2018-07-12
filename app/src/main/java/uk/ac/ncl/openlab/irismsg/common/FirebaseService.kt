package uk.ac.ncl.openlab.irismsg.common

import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import uk.ac.ncl.openlab.irismsg.R

class FirebaseService : FirebaseMessagingService() {
    
    override fun onNewToken(newToken: String) {
        Log.d("fcm", newToken)
    }
    
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("msg", message.toString())
        
        when (message.data[FCM_TYPE_KEY]) {
            TYPE_NEW_DONATIONS -> displayNewDonationsNotification(message)
            else -> Log.e("fcm", "unknown type ${message.data[FCM_TYPE_KEY]}")
        }
        
    }
    
    private fun displayNewDonationsNotification (message: RemoteMessage) {
        
        val notification = NotificationCompat.Builder(this, CHANNEL_DONATIONS)
                .setSmallIcon(R.drawable.notification_icon_background)
                .setContentTitle(getString(R.string.title_new_donations_notif))
                .setContentText(getString(R.string.body_new_donations_notif))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .build()
        
        NotificationManagerCompat.from(this)
                .notify(NEW_DONATION_NOTIFICATION, notification)
    }
    
    companion object {
        const val FCM_TYPE_KEY = "type"
        
        const val TYPE_NEW_DONATIONS = "new_donations"
        
        const val CHANNEL_DONATIONS = "donation_channel"
        
        const val NEW_DONATION_NOTIFICATION = 1
    }
}