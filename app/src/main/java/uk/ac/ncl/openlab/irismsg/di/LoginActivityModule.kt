package uk.ac.ncl.openlab.irismsg.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import uk.ac.ncl.openlab.irismsg.activity.LoginActivity

@Module
@Suppress("unused")
abstract class LoginActivityModule {
    
    @ContributesAndroidInjector(modules = [ FragmentBuildersModule::class ])
    abstract fun contributeMainActivity(): LoginActivity
}