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
    
    fun init () : PendingMessageListViewModel {
        if (!::messages.isInitialized) {
            messages = messageRepo.getPendingMessages()
        }
        return this
    }
    
    fun reload () {
        messageRepo.reloadPendingMessages(messages)
    }
    
    fun reloadFromCache () {
        messageRepo.reloadPendingMessagesFromCache(messages)
    }
}