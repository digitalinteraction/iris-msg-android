package uk.ac.ncl.openlab.irismsg.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import uk.ac.ncl.openlab.irismsg.model.PendingMessageEntity
import uk.ac.ncl.openlab.irismsg.repo.MessageRepository
import javax.inject.Inject

class PendingMessageListViewModel
@Inject constructor (private val messageRepo: MessageRepository) : ViewModel() {
    
    lateinit var messages: MutableLiveData<List<PendingMessageEntity>>
        private set
    
    /** Initialize ourself (we don't control the constructor) */
    fun init () : PendingMessageListViewModel {
        if (!::messages.isInitialized) {
            messages = messageRepo.getPendingMessages()
        }
        return this
    }
    
    /** Force a reload of the messages */
    fun reload () {
        messageRepo.reloadPendingMessages(messages)
    }
}