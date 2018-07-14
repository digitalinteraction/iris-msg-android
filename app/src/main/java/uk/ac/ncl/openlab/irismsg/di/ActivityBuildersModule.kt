package uk.ac.ncl.openlab.irismsg.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import uk.ac.ncl.openlab.irismsg.activity.*

/**
 * A Dagger module to provide Activities
 */
@Module
@Suppress("unused")
abstract class ActivityBuildersModule {
    
    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
    abstract fun contributeEmptyMainActivity () : EmptyMainActivity
    
    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
    abstract fun contributeLoginActivity () : LoginActivity
    
    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
    abstract fun contributeOnboardActivity () : OnboardActivity
    
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeOrganisationListActivity () : OrganisationListActivity
    
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeEditOrganisationActivity () : EditOrganisationActivity
    
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeOrganisationDetailActivity () : OrganisationDetailActivity
    
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeDonateActivity () : DonateActivity
}
