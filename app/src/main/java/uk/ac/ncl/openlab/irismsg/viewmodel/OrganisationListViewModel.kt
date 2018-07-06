package uk.ac.ncl.openlab.irismsg.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import uk.ac.ncl.openlab.irismsg.repo.OrganisationRepository
import javax.inject.Inject

class OrganisationListViewModel
@Inject constructor(val organisationRepository : OrganisationRepository) : ViewModel() {
    
    lateinit var organisations: LiveData<List<OrganisationEntity>>
        private set
    
    fun init () {
        if (::organisations.isInitialized) return
        organisations = organisationRepository.getOrganisations()
    }
}