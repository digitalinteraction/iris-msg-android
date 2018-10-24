package uk.ac.ncl.openlab.irismsg

import android.app.*
import android.content.BroadcastReceiver
import android.os.Build
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import dagger.android.HasServiceInjector
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.api.UpdateFcmRequest
import uk.ac.ncl.openlab.irismsg.di.AppInjector
import uk.ac.ncl.openlab.irismsg.jwt.JwtService
import uk.ac.ncl.openlab.irismsg.service.FirebaseService
import javax.inject.Inject
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.Crashlytics



/**
 * The Android application, injectable onto objects via Dagger
 */
class IrisMsgApp : Application(), HasActivityInjector, HasBroadcastReceiverInjector, HasServiceInjector {
    
    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>
    @Inject lateinit var receiverInjector: DispatchingAndroidInjector<BroadcastReceiver>
    @Inject lateinit var serviceInjector: DispatchingAndroidInjector<Service>
    
    @Inject lateinit var irisService: IrisMsgService
    @Inject lateinit var jwtService: JwtService
    
    
    override fun onCreate() {
        super.onCreate()
        
        // Init Dagger with outself
        AppInjector.init(this)
        
        // Setup the app
        updateFcm()
        registerNotificationChannels()
        setupCrashlytics()
    }
    
    /** Send the user's fmc token to the server */
    fun updateFcm () {
        jwtService.current ?: return
        
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            Log.d("fcm", it.token)
            irisService.updateFcm(UpdateFcmRequest(it.token)).enqueue(ApiCallback { res ->
                if (res.success) Log.d("fcm", "Token updated")
                else Crashlytics.log(Log.ERROR, "SmsSentReceiver", res.messages.joinToString())
            })
        }
    }
    
    /** Register our notification channel, if supported */
    private fun registerNotificationChannels () {
    
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    
        val channel = NotificationChannel(
            FirebaseService.CHANNEL_DONATIONS,
            getString(R.string.title_new_donations_channel),
            NotificationManager.IMPORTANCE_HIGH
        )
    
        channel.description = getString(R.string.info_new_donations_channel)
    
        getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
    }
    
    /** Setup our error handling */
    private fun setupCrashlytics () {
        val fabric = Fabric.Builder(this)
                .kits(Crashlytics())
                .debuggable(true)  // Enables Crashlytics debugger
                .build()
        Fabric.with(fabric)
    }
    
    override fun activityInjector () = activityInjector
    override fun broadcastReceiverInjector () = receiverInjector
    override fun serviceInjector () = serviceInjector
}