package uk.ac.ncl.openlab.irismsg

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import uk.ac.ncl.openlab.irismsg.api.OrganisationListViewModel
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass



//typealias ViewModelMap = MutableMap<Class<out ViewModel>, Provider<ViewModel>>
//
//
//
//@Singleton
//@Suppress("unchecked_cast")
//class ViewModelFactory @Inject constructor(private val viewModels: ViewModelMap)
//    : ViewModelProvider.Factory {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T
//            = viewModels[modelClass]?.get() as T
//}
//
//
//
//@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
//@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
//@MapKey
//internal annotation class ViewModelKey(val value: KClass<out ViewModel>)
//
//
//
//@Module
//@Suppress("unused")
//abstract class ViewModelModule {
//
//    @Binds
//    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(OrganisationListViewModel::class)
//    internal abstract fun postListViewModel(viewModel: OrganisationListViewModel): ViewModel
//
//    //Add more ViewModels here
//}