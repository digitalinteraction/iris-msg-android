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
    
    private fun <T> loadDataFromCall (call: ApiCall<T>, target: MutableLiveData<T>) : MutableLiveData<T> {
        call.enqueue(ApiCallback({ res ->
            target.value = res.data
        }, { _ ->
            TODO("Handle this error")
        }))
        return target
    }
    
    private fun <T> handleEnqueue (call: ApiCall<T>) : MutableLiveData<T> {
        return loadDataFromCall(call, MutableLiveData<T>())
    }
    
    fun getOrganisations () : MutableLiveData<List<OrganisationEntity>> {
        return handleEnqueue(irisService.listOrganisations())
    }
    
    fun reloadOrganisations (data: MutableLiveData<List<OrganisationEntity>>) {
        loadDataFromCall(irisService.listOrganisations(), data)
    }
}