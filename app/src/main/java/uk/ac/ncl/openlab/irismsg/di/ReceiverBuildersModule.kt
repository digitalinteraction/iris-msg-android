package uk.ac.ncl.openlab.irismsg.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import uk.ac.ncl.openlab.irismsg.receiver.SmsDeliveredReceiver
import uk.ac.ncl.openlab.irismsg.receiver.SmsSentReceiver

@Module
@Suppress("unused")
abstract class ReceiverBuildersModule {
    
    @ContributesAndroidInjector
    abstract fun contributeSmsSentReceiver () : SmsSentReceiver
    
    @ContributesAndroidInjector
    abstract fun contributeSmsDeliveredReceiver () : SmsDeliveredReceiver
}