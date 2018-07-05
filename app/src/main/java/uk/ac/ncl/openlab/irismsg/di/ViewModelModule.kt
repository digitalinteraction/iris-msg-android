package uk.ac.ncl.openlab.irismsg.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import uk.ac.ncl.openlab.irismsg.api.OrganisationListViewModel
import uk.ac.ncl.openlab.irismsg.viewmodel.IrisViewModelFactory

@Module
@Suppress("unused")
abstract class ViewModelModule {
    
    @Binds
    @IntoMap
    @ViewModelKey(OrganisationListViewModel::class)
    abstract fun bindOrganisationListViewModel(organisationListViewModel : OrganisationListViewModel) : ViewModel
    
    @Binds
    abstract fun bindViewModelFactory(factory: IrisViewModelFactory): ViewModelProvider.Factory
    
//    @Binds
//    @IntoMap
//    @ViewModelKey(UserViewModel::class)
//    abstract fun bindUserViewModel(userViewModel: UserViewModel): ViewModel

//    @Binds
//    @IntoMap
//    @ViewModelKey(SearchViewModel::class)
//    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

//    @Binds
//    @IntoMap
//    @ViewModelKey(RepoViewModel::class)
//    abstract fun bindRepoViewModel(repoViewModel: RepoViewModel): ViewModel

}