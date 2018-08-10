package uk.ac.ncl.openlab.irismsg.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import uk.ac.ncl.openlab.irismsg.model.OrganisationMemberEntity
import uk.ac.ncl.openlab.irismsg.repo.OrganisationRepository
import javax.inject.Inject

class OrganisationMembersViewModel
@Inject constructor(private val orgRepo: OrganisationRepository) : ViewModel() {
    
    lateinit var organisationId: String
    
    lateinit var members: MutableLiveData<List<OrganisationMemberEntity>>
    
    fun init (id: String): OrganisationMembersViewModel {
        if (!::members.isInitialized) {
            organisationId = id
            members = orgRepo.getMembers(id)
        }
        return this
    }
    
    fun reload () {
        orgRepo.reloadMembers(members, organisationId)
    }
}