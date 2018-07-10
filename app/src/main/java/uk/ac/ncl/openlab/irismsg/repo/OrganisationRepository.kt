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

typealias OrgList = List<OrganisationEntity>

/**
 * A repository responsible for organisation entities
 * TODO - Error handling
 */
@Singleton
class OrganisationRepository @Inject constructor() {
    
    @Inject
    lateinit var irisService: IrisMsgService
    
    var orgsCache: MutableList<OrganisationEntity> = mutableListOf()
    
    private fun loadOrgsInto (target: MutableLiveData<OrgList>) : MutableLiveData<OrgList> {
        
        irisService.listOrganisations().enqueue(ApiCallback({ res ->
            
            target.value = res.data
            orgsCache = res.data?.toMutableList() ?: mutableListOf()
            
        }, {
            TODO("Handle this error")
        }))
        
        return target
    }
    
    fun getOrganisations () : MutableLiveData<List<OrganisationEntity>> {
        val data = MutableLiveData<OrgList>()
        data.value = orgsCache
        return loadOrgsInto(data)
    }
    
    fun reloadOrganisations (data: MutableLiveData<List<OrganisationEntity>>) {
        loadOrgsInto(data)
    }
    
    fun organisationCreated (org: OrganisationEntity) {
        orgsCache.add(org)
    }
}