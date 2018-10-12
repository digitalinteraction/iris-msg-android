package uk.ac.ncl.openlab.irismsg.receiver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.crashlytics.android.Crashlytics
import dagger.android.DaggerBroadcastReceiver
import uk.ac.ncl.openlab.irismsg.activity.DonateActivity
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.api.MessageAttemptUpdate
import uk.ac.ncl.openlab.irismsg.api.UpdateMessageAttemptsRequest
import uk.ac.ncl.openlab.irismsg.common.EventBus
import uk.ac.ncl.openlab.irismsg.common.MessageAttemptState
import javax.inject.Inject


/**
 * A BroadcastReceiver to respond to sms sent messages
 */
class SmsSentReceiver : DaggerBroadcastReceiver() {
    
    @Inject lateinit var irisService : IrisMsgService
    @Inject lateinit var events : EventBus
    
    override fun onReceive(context : Context, intent : Intent) {
        super.onReceive(context, intent)
        
        // Get the attempt id
        val attemptId = intent.getStringExtra(EXTRAS_ATTEMPT_ID) ?: return
        
        
        // Work out the state using our result code
        val newState = when (resultCode) {
            Activity.RESULT_OK -> MessageAttemptState.SUCCESS
            else -> MessageAttemptState.FAILED
        }
        
        
        // Perform the api request
        val update = MessageAttemptUpdate(attemptId, newState)
        val body = UpdateMessageAttemptsRequest(listOf(update))
        irisService.updateMessageAttempts(body).enqueue(ApiCallback({ res ->
            if (res.success) {
                events.emit(DonateActivity.EVENT_SMS_SENT)
            } else {
                Crashlytics.log(Log.ERROR, "SmsSentReceiver", res.messages.joinToString())
            }
        }, { e ->
            Crashlytics.logException(e)
        }))
        
    }
    
    companion object {
        const val REQUEST_API_UPDATE = 1
        const val EXTRAS_ATTEMPT_ID = "attempt_id"
    }
}