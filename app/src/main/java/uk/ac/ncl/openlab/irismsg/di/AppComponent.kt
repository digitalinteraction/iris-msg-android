package uk.ac.ncl.openlab.irismsg.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import uk.ac.ncl.openlab.irismsg.IrisMsgApp
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    OrganisationListActivityModule::class
])
interface AppComponent {
    
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        
        fun build(): AppComponent
    }
    
    fun inject(irisApp: IrisMsgApp)
}