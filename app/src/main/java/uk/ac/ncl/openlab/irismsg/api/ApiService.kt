package uk.ac.ncl.openlab.irismsg.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.ac.ncl.openlab.irismsg.ApiException
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity

enum class ApiMode {
    LIVE,
    MOCK
}

//object ApiService {
//
//    private val client = makeClient()
//    private val currentApiMode = ApiMode.MOCK
//
//    // TODO: Properly parameterise ...
//    fun apiMode(): ApiMode = currentApiMode
//
//
//    private fun makeClient(): ApiInterface {
//        return if (apiMode() === ApiMode.LIVE) LiveApi() else MockApi()
//    }
//
//    private fun <T> handleCall (call: ApiCall<T>) : Observable<T?> {
//        return Observable.create { subscriber ->
//            call.execute().body()?.apply {
//                if (data != null) { subscriber.onNext(data) }
//
//                if (success) {
//                    subscriber.onComplete()
//                } else {
//                    subscriber.onError(ApiException(messages))
//                }
//            }
//        }
//    }
//
//    fun fetchOrganisations () = handleCall(client.listOrganisations())
//}

private typealias OrgList = List<OrganisationEntity>

class OrganisationListRepository (val service: ApiInterface) {

//    fun <T> handleEnqueue (call: ApiCall<T>, target: MutableLiveData<T>) : LiveData<T> {
//        call.enqueue(enqueueCallback(target))
//        return target
//    }
    
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
    
    fun getOrganisations () : LiveData<OrgList> {
        return handleEnqueue(service.listOrganisations())
    }
}