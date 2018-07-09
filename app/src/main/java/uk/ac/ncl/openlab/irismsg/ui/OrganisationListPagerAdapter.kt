package uk.ac.ncl.openlab.irismsg.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import uk.ac.ncl.openlab.irismsg.common.MemberRole

private data class TitledFrag <T : Fragment> (val title: String, val frag: T)

/**
 * A Pager Adapter to display organisation lists for different roles
 */
class OrganisationListPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

    private val pages = listOf(
        TitledFrag("Organisations", OrganisationListFragment.newInstance(MemberRole.COORDINATOR)),
        TitledFrag("Donations", OrganisationListFragment.newInstance(MemberRole.DONOR))
    )
    
    override fun getCount () = pages.size
    override fun getItem(index: Int) = pages[index].frag
    override fun getPageTitle(index: Int) = pages[index].title
}