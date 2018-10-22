package uk.ac.ncl.openlab.irismsg.repo

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.widget.Toast
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.model.PendingMessageEntity
import javax.inject.Inject
import javax.inject.Singleton

typealias PendingMsgList = List<PendingMessageEntity>


/**
 * A Repository responsible for Message entities
 */
@Singleton
class MessageRepository @Inject constructor(val irisService: IrisMsgService, val application: Application) {

    /** Load pending messages into a LiveData */
    private fun loadPendingMessagesInto (
        target: MutableLiveData<PendingMsgList>) : MutableLiveData<PendingMsgList> {
        
        // Perform the request
        irisService.listPendingMessages().enqueue(ApiCallback({ res ->
            target.value = res.data
        }, { _ ->
            target.value = null
            Toast.makeText(
                application.applicationContext,
                "Cannot fetch donations, please try again",
                Toast.LENGTH_LONG
            ).show()
        }))
        
        return target
    }
    
    /** Generate a LiveData and load pending Messages into it */
    fun getPendingMessages () : MutableLiveData<PendingMsgList> {
        val data = MutableLiveData<PendingMsgList>()
        return loadPendingMessagesInto(data)
    }
    
    /** Load pending messages into an existing LiveData */
    fun reloadPendingMessages (data: MutableLiveData<PendingMsgList>) {
        loadPendingMessagesInto(data)
    }
}