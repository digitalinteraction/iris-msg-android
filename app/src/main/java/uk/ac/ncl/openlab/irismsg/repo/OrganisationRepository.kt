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

@Singleton
class OrganisationRepository @Inject constructor() {
    
    @Inject
    lateinit var irisService: IrisMsgService
    
    // TODO: Error handling
    
    private fun <T> handleEnqueue (call: ApiCall<T>) : LiveData<T> {
        val target = MutableLiveData<T>()
        
        call.enqueue(object : Callback<ApiResponse<T>> {
            override fun onResponse(call : Call<ApiResponse<T>>?, response : Response<ApiResponse<T>>?) {
                response?.body()?.apply {
                    if (data != null) target.value = data
                }
            }
            override fun onFailure(call : Call<ApiResponse<T>>?, t : Throwable?) {
                // ...
            }
        })
        return target
    }
    
    fun getOrganisations () : LiveData<List<OrganisationEntity>> {
        return handleEnqueue(irisService.listOrganisations())
    }
}