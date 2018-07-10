package uk.ac.ncl.openlab.irismsg.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_organisation_list.*
import uk.ac.ncl.openlab.irismsg.ui.OrganisationListFragment
import uk.ac.ncl.openlab.irismsg.ui.OrganisationListPagerAdapter
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.JsonWebToken
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import javax.inject.Inject

/**
 * An Activity to present a Organisations that the user is part of either as a coordinator or donor
 * TODO - Implement organisation clicks
 */
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
        
        // Setup click handler
        add_organisation_fab.setOnClickListener { _ ->
            startActivity(
                Intent(this, EditOrganisationActivity::class.java)
            )
        }
        
        tabs_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0 : Int) { }
            override fun onPageScrolled(p0 : Int, p1 : Float, p2 : Int) { }
            override fun onPageSelected(p0 : Int) {
                when (p0) {
                    0 -> add_organisation_fab.show()
                    else -> add_organisation_fab.hide()
                }
            }
        })
    }
    
    override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item : MenuItem?) : Boolean {
    
        val intent = when (item?.itemId) {
            R.id.action_even_more -> Intent(Intent.ACTION_VIEW, Uri.parse("http://irismsg.io"))
            R.id.action_open_lab  -> Intent(Intent.ACTION_VIEW, Uri.parse("http://openlab.ncl.ac.uk"))
            else -> null
        }
    
        if (intent != null) startActivity(intent)
    
        // Perform logout
        if (item?.itemId == R.id.action_logout) {
            
            // Un-store the jwt and current user
            JsonWebToken.save(applicationContext, null)
            UserEntity.current = null
            
            // Go to the onboarding activity
            startActivity(Intent(this, OnboardActivity::class.java))
            finish()
        }
        
        return super.onOptionsItemSelected(item)
    }
    
    override fun onListFragmentInteraction(organisation: OrganisationEntity) {
         startActivity(
             Intent(this, OrganisationDetailActivity::class.java)
                     .putExtra(OrganisationDetailActivity.ORGANISATION_ID_KEY, organisation.id)
         )
    }
}