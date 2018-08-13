package uk.ac.ncl.openlab.irismsg.repo

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.widget.Toast
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import uk.ac.ncl.openlab.irismsg.model.OrganisationMemberEntity
import javax.inject.Inject
import javax.inject.Singleton

typealias OrgList = List<OrganisationEntity>
typealias OrgMemberList = List<OrganisationMemberEntity>

/**
 * A repository responsible for organisation entities
 */
@Singleton
class OrganisationRepository @Inject constructor(val irisService: IrisMsgService, val application: Application) {
    
    private var orgsCache: MutableList<OrganisationEntity> = mutableListOf()
    
    
    /** Load a list of Organisations into a LiveData using the api */
    private fun loadOrgsInto (target: MutableLiveData<OrgList>) : MutableLiveData<OrgList> {
        
        // Fetch organisations, put it into the live data & cache them
        irisService.listOrganisations().enqueue(ApiCallback({ res ->
            target.value = res.data
            orgsCache = res.data?.toMutableList() ?: mutableListOf()
        }, { _ ->
            target.value = null
            Toast.makeText(
                application.applicationContext,
                "Failed to fetch organisations, please try again",
                Toast.LENGTH_LONG
            ).show()
        }))
        
        return target
    }
    
    /** Load a single Organisations into a LiveData using the api */
    private fun loadSingleOrgInto (target: MutableLiveData<OrganisationEntity>, id: String) {
        
        // Fetch the organisation, put it into the live data and cache it
        irisService.showOrganisation(id).enqueue(ApiCallback({ res ->
            target.value = res.data
            if (res.data != null) {
                orgsCache.removeAll { it.id == id }
                orgsCache.add(res.data)
            }
        }, { _ ->
            target.value = null
            Toast.makeText(
                application.applicationContext,
                "Failed to fetch organisation, please try again",
                Toast.LENGTH_LONG
            ).show()
        }))
    }
    
    /** Load an Organisation's members into a LiveData using the api */
    private fun loadMembersInto (target: MutableLiveData<OrgMemberList>, id: String) {
        
        irisService.listOrganisationMembers(id).enqueue(ApiCallback({ res ->
            target.value = res.data
        }, { _ ->
            target.value = null
            Toast.makeText(
                application.applicationContext,
                "Failed to fetch members, please try again",
                Toast.LENGTH_LONG
            ).show()
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
    
    /** Re-fetch members */
    fun reloadMembers (data: MutableLiveData<OrgMemberList>, id: String) {
        loadMembersInto(data, id)
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
    
    /** Get the members of an Organisation */
    fun getMembers (id: String) : MutableLiveData<OrgMemberList > {
        val data = MutableLiveData<OrgMemberList>()
        loadMembersInto(data, id)
        return data
    }
}