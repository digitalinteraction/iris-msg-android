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
 */
@Singleton
class OrganisationRepository @Inject constructor(val irisService: IrisMsgService) {
    
    private var orgsCache: MutableList<OrganisationEntity> = mutableListOf()
    
    
    /** Load a list of Organisations into a LiveData from the api */
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
    
    /** Load a single Organisations into a LiveData from the api */
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
    fun getOrganisations () : MutableLiveData<OrgList> {
        val data = MutableLiveData<OrgList>()
        data.value = orgsCache
        return loadOrgsInto(data)
    }
    
    /** Re-fetch organisations into a LiveData */
    fun reloadOrganisations (data: MutableLiveData<OrgList>) {
        loadOrgsInto(data)
    }
    
    /** Re-fetch organisations from the cache */
    fun reloadOrganisationsFromCache (data: MutableLiveData<OrgList>) {
        data.value = orgsCache
    }
    
    /** Notify the in-memory cache that an Organisation was created */
    fun organisationCreated (org: OrganisationEntity) {
        orgsCache.add(org)
    }
    
    /** Notify the in-memory cache that an Organisation was deleted */
    fun organisationDestroyed (id: String) {
        orgsCache.removeAll { it.id == id }
    }
    
    /** Get a specific organisation */
    fun getOrganisation (id: String) : MutableLiveData<OrganisationEntity> {
        val data = MutableLiveData<OrganisationEntity>()
        data.value = orgsCache.find { org -> org.id == id }
        if (data.value == null) loadSingleOrgInto(data, id)
        return data
    }
}