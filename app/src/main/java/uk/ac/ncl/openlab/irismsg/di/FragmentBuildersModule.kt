package uk.ac.ncl.openlab.irismsg.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import uk.ac.ncl.openlab.irismsg.ui.MemberListFragment
import uk.ac.ncl.openlab.irismsg.ui.OrganisationListFragment
import uk.ac.ncl.openlab.irismsg.ui.SendMessageFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    
    @ContributesAndroidInjector
    abstract fun contributeOrganisationListFragment(): OrganisationListFragment
    
    @ContributesAndroidInjector
    abstract fun contributeMemberListFragment(): MemberListFragment
    
    @ContributesAndroidInjector
    abstract fun contributeSendMessageFragment(): SendMessageFragment
}
