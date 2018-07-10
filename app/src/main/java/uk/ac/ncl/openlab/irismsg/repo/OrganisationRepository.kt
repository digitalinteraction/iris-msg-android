package uk.ac.ncl.openlab.irismsg.repo

import android.arch.lifecycle.MutableLiveData
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
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
        
        // Fetch organisations, put it into the live data & cache them
        irisService.listOrganisations().enqueue(ApiCallback({ res ->
            target.value = res.data
            orgsCache = res.data?.toMutableList() ?: mutableListOf()
        }, { _ ->
            TODO("Handle this error")
        }))
        
        return target
    }
    
    private fun loadSingleOrgInto(target: MutableLiveData<OrganisationEntity>, id: String) {
        
        // Fetch the organisation, put it into the live data and cache it
        irisService.showOrganisation(id).enqueue(ApiCallback({ res ->
            target.value = res.data
            if (res.data != null) {
                orgsCache.removeAll { it.id == id }
                orgsCache.add(res.data)
            }
        }, { _ ->
            TODO("Handle this error")
        }))
    }
    
    /** Fetch all organisations the user has access to and place into a LiveData */
    fun getOrganisations () : MutableLiveData<List<OrganisationEntity>> {
        val data = MutableLiveData<OrgList>()
        data.value = orgsCache
        return loadOrgsInto(data)
    }
    
    /** Re-fetch organisations into a LiveData */
    fun reloadOrganisations (data: MutableLiveData<List<OrganisationEntity>>) {
        loadOrgsInto(data)
    }
    
    /** Re-fetch organisations from the cache */
    fun reloadOrganisationsFromCache (data: MutableLiveData<List<OrganisationEntity>>) {
        data.value = orgsCache
    }
    
    /** Notify the in-memory cache that an Organisation was created */
    fun organisationCreated (org: OrganisationEntity) {
        orgsCache.add(org)
    }
    
    /** Get a specific organisation */
    fun getOrganisation (id: String) : MutableLiveData<OrganisationEntity> {
        val data = MutableLiveData<OrganisationEntity>()
        data.value = orgsCache.find { org -> org.id == id }
        if (data.value == null) loadSingleOrgInto(data, id)
        return data
    }
}