package uk.ac.ncl.openlab.irismsg.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import uk.ac.ncl.openlab.irismsg.viewmodel.IrisViewModelFactory
import uk.ac.ncl.openlab.irismsg.viewmodel.OrganisationListViewModel
import uk.ac.ncl.openlab.irismsg.viewmodel.OrganisationViewModel
import uk.ac.ncl.openlab.irismsg.viewmodel.PendingMessageListViewModel


/**
 * A Dagger module to provide View Models
 */
@Module
@Suppress("unused")
abstract class ViewModelModule {
    
    @Binds
    @IntoMap
    @ViewModelKey(OrganisationListViewModel::class)
    abstract fun bindOrganisationListViewModel (organisationListViewModel: OrganisationListViewModel) : ViewModel
    
    @Binds
    @IntoMap
    @ViewModelKey(OrganisationViewModel::class)
    abstract fun bindOrganisationViewModel (organisationViewModel: OrganisationViewModel) : ViewModel
    
    @Binds
    @IntoMap
    @ViewModelKey(PendingMessageListViewModel::class)
    abstract fun bindPendingMessageListViewModel (pendingMessageListViewModel : PendingMessageListViewModel) : ViewModel
    
    @Binds
    abstract fun bindViewModelFactory (factory: IrisViewModelFactory): ViewModelProvider.Factory
}