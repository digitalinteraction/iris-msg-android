package uk.ac.ncl.openlab.irismsg.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import uk.ac.ncl.openlab.irismsg.repo.OrganisationRepository
import javax.inject.Inject

/**
 * A View model for a list of Organisation Entities
 */
class OrganisationListViewModel
@Inject constructor(private val orgRepo : OrganisationRepository) : ViewModel() {
    
    lateinit var organisations: MutableLiveData<List<OrganisationEntity>>
        private set
    
    fun init () : OrganisationListViewModel {
        if (!::organisations.isInitialized) {
            organisations = orgRepo.getOrganisations()
        }
        return this
    }
    
    fun reload () {
        orgRepo.reloadOrganisations(organisations)
    }
}