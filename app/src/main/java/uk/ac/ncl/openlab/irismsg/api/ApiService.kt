package uk.ac.ncl.openlab.irismsg.api

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import uk.ac.ncl.openlab.irismsg.repo.OrganisationRepository
import javax.inject.*

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

class OrganisationListViewModel
@Inject constructor(val organisationRepository : OrganisationRepository) : ViewModel() {
    
    lateinit var organisations: LiveData<List<OrganisationEntity>>
        private set
    
    fun init () {
        if (::organisations.isInitialized) return
        organisations = organisationRepository.getOrganisations()
    }
}