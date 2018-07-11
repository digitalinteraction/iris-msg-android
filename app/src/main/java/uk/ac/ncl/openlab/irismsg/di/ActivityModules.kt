package uk.ac.ncl.openlab.irismsg.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import uk.ac.ncl.openlab.irismsg.activity.*


@Module
@Suppress("unused")
abstract class ActivityBuildersModule {
    
    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
    abstract fun contributeEmptyMainActivity(): EmptyMainActivity
    
    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
    abstract fun contributeLoginActivity(): LoginActivity
    
    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
    abstract fun contributeOnboardActivity(): OnboardActivity
    
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeOrganisationListActivity(): OrganisationListActivity
    
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeEditOrganisationActivity(): EditOrganisationActivity
    
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeOrganisationDetailActivity(): OrganisationDetailActivity
}


//@Module
//@Suppress("unused")
//abstract class LoginActivityModule {
//
//    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
//    abstract fun contributeMainActivity(): LoginActivity
//}


//@Module
//@Suppress("unused")
//abstract class OnboardActivityModule {
//
//    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
//    abstract fun contributeMainActivity(): OnboardActivity
//}


//@Module
//@Suppress("unused")
//abstract class OrganisationListActivityModule {
//
//    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
//    abstract fun contributeMainActivity(): OrganisationListActivity
//}


//@Module
//@Suppress("unused")
//abstract class EditOrganisationActivityModule {
//
//    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
//    abstract fun contributeMainActivity(): EditOrganisationActivity
//}


//@Module
//@Suppress("unused")
//abstract class OrganisationDetailActivityModule {
//
//    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
//    abstract fun contributeMainActivity(): OrganisationDetailActivity
//}