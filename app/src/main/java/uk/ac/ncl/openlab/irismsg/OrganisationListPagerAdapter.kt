package uk.ac.ncl.openlab.irismsg

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

private data class TitledFrag <T : Fragment> (val title: String, val frag: T)

class OrganisationListPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    private val pages = listOf(
        TitledFrag("Organisations", OrganisationListFragment.newInstance(MemberRole.COORDINATOR)),
        TitledFrag("Donations", OrganisationListFragment.newInstance(MemberRole.DONOR))
    )
    
    override fun getCount () = pages.size
    override fun getItem(index: Int) = pages[index].frag
    override fun getPageTitle(index: Int) = pages[index].title
}