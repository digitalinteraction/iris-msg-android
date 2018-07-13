package uk.ac.ncl.openlab.irismsg.di

import android.app.Application
import com.squareup.moshi.Moshi
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import uk.ac.ncl.openlab.irismsg.IrisMsgApp
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    ActivityBuildersModule::class,
    ServiceBuildersModule::class,
    ReceiverBuildersModule::class
])
@Suppress("unused")
interface AppComponent {
    
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application (application: Application) : Builder
        
        fun build (): AppComponent
    }
    
    fun inject (irisApp: IrisMsgApp)
    
    fun getMoshi () : Moshi
}