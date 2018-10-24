package uk.ac.ncl.openlab.irismsg.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import uk.ac.ncl.openlab.irismsg.model.OrganisationMemberEntity
import uk.ac.ncl.openlab.irismsg.repo.OrganisationRepository
import javax.inject.Inject

/**
 * A ViewModel for a list of Members of an Organisation
 */
class OrganisationMembersViewModel
@Inject constructor(private val orgRepo: OrganisationRepository) : ViewModel() {
    
    lateinit var organisationId: String
    
    lateinit var members: MutableLiveData<List<OrganisationMemberEntity>>
    
    /** Initialize ourself (we don't control the constructor) */
    fun init (id: String): OrganisationMembersViewModel {
        if (!::members.isInitialized) {
            organisationId = id
            members = orgRepo.getMembers(id)
        }
        return this
    }
    
    /** Force a reload of the members */
    fun reload () {
        orgRepo.reloadMembers(members, organisationId)
    }
}