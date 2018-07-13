package uk.ac.ncl.openlab.irismsg.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasBroadcastReceiverInjector
import javax.inject.Inject

class SmsDeliveredReceiver : BroadcastReceiver(), HasBroadcastReceiverInjector {
    
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<BroadcastReceiver>
    
    override fun broadcastReceiverInjector() : AndroidInjector<BroadcastReceiver> = dispatchingAndroidInjector
    
    override fun onReceive(context : Context, intent: Intent) {
        Log.d("SmsDeliveredService", intent.toString())
    }
    
    companion object {
        const val REQUEST_API_UPDATE = 1
        const val EXTRAS_ATTEMPT_ID = "id"
    }
}