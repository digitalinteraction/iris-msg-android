package uk.ac.ncl.openlab.irismsg.repo

import android.arch.lifecycle.MutableLiveData
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
class MessageRepository @Inject constructor(val irisService: IrisMsgService) {
    
    private var pendingMessageCache: MutableList<PendingMessageEntity> = mutableListOf()
    
    /** Load pending messages into a LiveData */
    private fun loadPendingMessagesInto (
        target: MutableLiveData<PendingMsgList>) : MutableLiveData<PendingMsgList> {
        
        // Perform the request
        irisService.listPendingMessages().enqueue(ApiCallback({ res ->
            target.value = res.data
            pendingMessageCache = res.data?.toMutableList() ?: mutableListOf()
        }, { _ ->
            TODO("Handle messages.attempts.index error")
        }))
        
        return target
    }
    
    /** Generate a LiveData and load pending Messages into it */
    fun getPendingMessages () : MutableLiveData<PendingMsgList> {
        val data = MutableLiveData<PendingMsgList>()
        data.value = pendingMessageCache
        return loadPendingMessagesInto(data)
    }
    
    /** Load pending messages into an existing LiveData */
    fun reloadPendingMessages (data: MutableLiveData<PendingMsgList>) {
        loadPendingMessagesInto(data)
    }
    
    /** Load pending messages into a LiveData from the internal cache */
    fun reloadPendingMessagesFromCache (data: MutableLiveData<PendingMsgList>) {
        data.value = pendingMessageCache
    }
}