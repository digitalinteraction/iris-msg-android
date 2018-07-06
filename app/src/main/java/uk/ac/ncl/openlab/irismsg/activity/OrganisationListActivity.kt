package uk.ac.ncl.openlab.irismsg.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_organisation_list.*
import uk.ac.ncl.openlab.irismsg.ui.OrganisationListFragment
import uk.ac.ncl.openlab.irismsg.ui.OrganisationListPagerAdapter
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import javax.inject.Inject

class OrganisationListActivity : AppCompatActivity(),
        HasSupportFragmentInjector,
        OrganisationListFragment.OnListFragmentInteractionListener {
    
    private lateinit var pagerAdapter: OrganisationListPagerAdapter
    
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    
    override fun supportFragmentInjector() = dispatchingAndroidInjector
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create lazies
        pagerAdapter = OrganisationListPagerAdapter(supportFragmentManager)
        
        // Setup view
        setContentView(R.layout.activity_organisation_list)
        setSupportActionBar(organisation_list_toolbar)
        
        // Setup tabs
        tabs_pager.adapter = pagerAdapter
        tabs_layout.setupWithViewPager(tabs_pager)
    }
    
    override fun onListFragmentInteraction(organisation: OrganisationEntity) {
         Log.v("Org", organisation.name)
    }
}