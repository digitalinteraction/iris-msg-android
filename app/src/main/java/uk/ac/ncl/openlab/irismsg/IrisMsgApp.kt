package uk.ac.ncl.openlab.irismsg

import android.app.Activity
import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import uk.ac.ncl.openlab.irismsg.di.AppInjector
import javax.inject.Inject


class IrisMsgApp : Application(), HasActivityInjector {
    
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    
    override fun onCreate() {
        super.onCreate()
        
         AppInjector.init(this)
    }
    
    override fun activityInjector() = dispatchingAndroidInjector
}