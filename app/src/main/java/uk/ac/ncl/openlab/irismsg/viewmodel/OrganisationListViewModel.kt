package uk.ac.ncl.openlab.irismsg.viewmodel

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
    
    // Initialize ourself (we don't control the constructor)
    fun init () : OrganisationListViewModel {
        if (!::organisations.isInitialized) {
            organisations = orgRepo.getOrganisations()
        }
        return this
    }
    
    // Force a reload of the organisations
    fun reload () {
        orgRepo.reloadOrganisations(organisations)
    }
    
    // Reload the organisations from the cache
    fun reloadFromCache () {
        orgRepo.reloadOrganisationsFromCache(organisations)
    }
}