package uk.ac.ncl.openlab.irismsg.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import uk.ac.ncl.openlab.irismsg.activity.*


@Module
@Suppress("unused")
abstract class EmptyMainActivityModule {
    
    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
    abstract fun contributeMainActivity(): EmptyMainActivity
}


@Module
@Suppress("unused")
abstract class LoginActivityModule {
    
    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
    abstract fun contributeMainActivity(): LoginActivity
}


@Module
@Suppress("unused")
abstract class OnboardActivityModule {
    
    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
    abstract fun contributeMainActivity(): OnboardActivity
}


@Module
@Suppress("unused")
abstract class OrganisationListActivityModule {
    
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): OrganisationListActivity
}


@Module
@Suppress("unused")
abstract class EditOrganisationActivityModule {
    
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): EditOrganisationActivity
}