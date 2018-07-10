package uk.ac.ncl.openlab.irismsg.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import uk.ac.ncl.openlab.irismsg.repo.OrganisationRepository
import javax.inject.Inject

class OrganisationViewModel
@Inject constructor(private val orgRepo: OrganisationRepository) : ViewModel() {
    
    lateinit var organisationId: String
    
    lateinit var organisation: MutableLiveData<OrganisationEntity>
        private set
    
    fun init (id: String) : OrganisationViewModel {
        if (!::organisation.isInitialized) {
            organisationId = id
            organisation = orgRepo.getOrganisation(organisationId)
        }
        return this
    }
}