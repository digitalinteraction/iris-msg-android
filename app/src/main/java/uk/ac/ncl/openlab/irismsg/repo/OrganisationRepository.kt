package uk.ac.ncl.openlab.irismsg.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.ac.ncl.openlab.irismsg.api.*
import uk.ac.ncl.openlab.irismsg.common.ApiCall
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A repository responsible for organisation entities
 * TODO - Error handling
 */
@Singleton
class OrganisationRepository @Inject constructor() {
    
    @Inject
    lateinit var irisService: IrisMsgService
    
    private fun <T> handleEnqueue (call: ApiCall<T>) : LiveData<T> {
        val target = MutableLiveData<T>()
        
        call.enqueue(ApiCallback({ res ->
            target.value = res.data
        }, { _ ->
            TODO("Handle this error")
        }))
        return target
    }
    
    fun getOrganisations () : LiveData<List<OrganisationEntity>> {
        return handleEnqueue(irisService.listOrganisations())
    }
}