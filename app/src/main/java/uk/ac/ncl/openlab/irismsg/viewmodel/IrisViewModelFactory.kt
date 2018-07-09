package uk.ac.ncl.openlab.irismsg.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

typealias ViewModelMap = Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>

/**
 * A Factory to create a model:ViewModel map to provide to ViewModelProviders
 */
@Singleton
class IrisViewModelFactory @Inject constructor(private val creators: ViewModelMap)
    : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("unknown model class $modelClass")
        
        try {
            @Suppress("unchecked_cast")
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        
    }
}