package uk.ac.ncl.openlab.irismsg.viewmodel

import android.arch.lifecycle.*
import uk.ac.ncl.openlab.irismsg.model.MemberEntity
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import uk.ac.ncl.openlab.irismsg.repo.OrganisationRepository
import java.lang.reflect.Member
import javax.inject.Inject

/**
 * A ViewModel for a single Organisation Entity
 */
class OrganisationViewModel
@Inject constructor(private val orgRepo: OrganisationRepository) : ViewModel() {
    
    lateinit var organisationId: String
    
    lateinit var organisation: MutableLiveData<OrganisationEntity>
        private set
    
    lateinit var members: LiveData<MutableList<MemberEntity>>
    
    /** Initialize ourself with the id of the Organisation we represent
        NOTE - we don't control the constructor */
    fun init (id: String) : OrganisationViewModel {
        if (!::organisation.isInitialized) {
            organisationId = id
            organisation = orgRepo.getOrganisation(organisationId)
            members = Transformations.map(organisation) { org -> org.members }
        }
        return this
    }
    
    
}