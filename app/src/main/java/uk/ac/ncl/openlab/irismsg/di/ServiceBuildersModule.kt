package uk.ac.ncl.openlab.irismsg.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import uk.ac.ncl.openlab.irismsg.service.FirebaseService

@Module
@Suppress("unused")
abstract class ServiceBuildersModule {
    
    @ContributesAndroidInjector
    abstract fun contributeFirebaseService () : FirebaseService
}