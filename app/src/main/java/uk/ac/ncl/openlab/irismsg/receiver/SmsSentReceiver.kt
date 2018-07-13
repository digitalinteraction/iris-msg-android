package uk.ac.ncl.openlab.irismsg.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SmsSentReceiver: BroadcastReceiver() {
    
    override fun onReceive(context : Context, intent: Intent) {
        Log.d("SmsSentService", intent.toString())
    }
}