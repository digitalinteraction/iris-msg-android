package uk.ac.ncl.openlab.irismsg

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class OrganisationsPagerAdapter : FragmentPagerAdapter {
    
    private val mContext: Context
    
    constructor(fm: FragmentManager, context: Context) : super(fm) {
        this.mContext = context
    }
    
    override fun getItem(index: Int): Fragment {
        val type = if (index === 0) MemberRole.COORDINATOR else MemberRole.DONOR
        val frag = OrganisationFragment()
        frag.arguments = Bundle().apply {
            putSerializable("role", type.ordinal)
        }
        return frag
    }
    
    override fun getCount () : Int {
        return 2
    }
    
    override fun getPageTitle(position: Int): CharSequence? {
        val id = if (position === 0) R.string.tab_coordinator else R.string.tab_donor
        return mContext.resources.getString(id)
    }
}