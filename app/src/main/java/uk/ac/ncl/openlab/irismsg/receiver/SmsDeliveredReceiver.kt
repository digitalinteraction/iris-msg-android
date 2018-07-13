package uk.ac.ncl.openlab.irismsg.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SmsDeliveredReceiver : BroadcastReceiver() {
    
    override fun onReceive(context : Context, intent: Intent) {
        Log.d("SmsDeliveredService", intent.toString())
    }
}