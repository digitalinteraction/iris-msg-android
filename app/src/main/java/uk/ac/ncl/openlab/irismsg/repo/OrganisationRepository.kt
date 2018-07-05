package uk.ac.ncl.openlab.irismsg.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.ac.ncl.openlab.irismsg.api.ApiCall
import uk.ac.ncl.openlab.irismsg.api.ApiInterface
import uk.ac.ncl.openlab.irismsg.api.ApiResponse
import uk.ac.ncl.openlab.irismsg.api.MockApi
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrganisationRepository @Inject constructor() {
    
    private val service: ApiInterface = MockApi()
    
    // TODO: Error handling
    fun <T> handleEnqueue (call: ApiCall<T>) : LiveData<T> {
        val target = MutableLiveData<T>()
        
        call.enqueue(object : Callback<ApiResponse<T>> {
            override fun onResponse(call : Call<ApiResponse<T>>?, response : Response<ApiResponse<T>>?) {
                response?.body()?.apply {
                    if (data != null) target.value = data
                }
            }
            override fun onFailure(call : Call<ApiResponse<T>>?, t : Throwable?) {}
        })
        return target
    }
    
    fun getOrganisations (userId: String) : LiveData<List<OrganisationEntity>> {
        return handleEnqueue(service.listOrganisations())
    }
}