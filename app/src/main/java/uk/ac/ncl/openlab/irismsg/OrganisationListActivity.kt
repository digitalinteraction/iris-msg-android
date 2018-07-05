package uk.ac.ncl.openlab.irismsg

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_organisation_list.*
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import javax.inject.Inject


class OrganisationListActivity : AppCompatActivity(),
        HasSupportFragmentInjector,
        OrganisationListFragment.OnListFragmentInteractionListener {
    
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    
    private lateinit var pagerAdapter: OrganisationListPagerAdapter
    
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
        // Log.v("Org", organisation.name)
    }
    
    override fun supportFragmentInjector() = dispatchingAndroidInjector
}