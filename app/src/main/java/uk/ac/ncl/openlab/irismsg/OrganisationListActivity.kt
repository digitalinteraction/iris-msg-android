package uk.ac.ncl.openlab.irismsg

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_organisation_list.*
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity

class OrganisationListActivity : AppCompatActivity(),
        OrganisationListFragment.OnListFragmentInteractionListener {
    
    private var organisations = listOf<OrganisationEntity>()
    private lateinit var pagerAdapter: OrganisationListPagerAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create lazies
        pagerAdapter = OrganisationListPagerAdapter(
            supportFragmentManager,
            applicationContext
        )
        
        // Setup view
        setContentView(R.layout.activity_organisation_list)
        setSupportActionBar(organisation_list_toolbar)
        
        // Setup tabs
        tabs_pager.adapter = pagerAdapter
        tabs_layout.setupWithViewPager(tabs_pager)
    }
    
    override fun onListFragmentInteraction(org: OrganisationEntity) {
        Log.v("Org", org.name)
    }
}

class OrganisationListViewModel () : ViewModel() {
    private lateinit var organisations: LiveData<List<OrganisationEntity>>
    lateinit var userId: String
    
    fun init (userId: String) {
        this.userId = userId
    }
    
    fun getOrganisations () = organisations
}