package uk.ac.ncl.openlab.irismsg.irismsg

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import uk.ac.ncl.openlab.irismsg.irismsg.dummy.DummyContent

class OrganisationListActivity : AppCompatActivity(), OrganisationFragment.OnListFragmentInteractionListener {
    
    private lateinit var pagerAdapter: OrganisationsPagerAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        pagerAdapter = OrganisationsPagerAdapter(supportFragmentManager, applicationContext)
        
        setContentView(R.layout.activity_organisation_list)
        
        setSupportActionBar(findViewById(R.id.organisation_list_toolbar))
        
        val tabsPager = findViewById<ViewPager>(R.id.tabs_pager)
        tabsPager.adapter = pagerAdapter
    
        val tabsControl = findViewById<TabLayout>(R.id.tabs_control)
        tabsControl.setupWithViewPager(tabsPager)
    }
    
    
    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        Log.v("test", item.toString())
    }
    
}
