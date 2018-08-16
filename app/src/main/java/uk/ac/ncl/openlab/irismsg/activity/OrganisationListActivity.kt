package uk.ac.ncl.openlab.irismsg.activity

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_organisation_list.*
import uk.ac.ncl.openlab.irismsg.ui.OrganisationListFragment
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.jwt.JwtService
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.model.OrganisationEntity
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import uk.ac.ncl.openlab.irismsg.viewmodel.OrganisationListViewModel
import javax.inject.Inject

/**
 * An Activity to present a Organisations that the user is part of either as a coordinator or donor
 */
class OrganisationListActivity : AppCompatActivity(),
        HasSupportFragmentInjector,
        OrganisationListFragment.Listener {
    
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var viewModel: OrganisationListViewModel
    
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var jwtService: JwtService
    @Inject lateinit var irisService: IrisMsgService
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    
    override fun supportFragmentInjector() = dispatchingAndroidInjector
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup view
        setContentView(R.layout.activity_organisation_list)
        setSupportActionBar(toolbar)
    
        
        // Get a ViewModel to handle organisations
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(OrganisationListViewModel::class.java)
                .init()
        
        
        // Setup tabs
        pagerAdapter = PagerAdapter(supportFragmentManager)
        tabs_pager.adapter = pagerAdapter
        tabs_layout.setupWithViewPager(tabs_pager)
        
        // Setup click handler
        fab.setOnClickListener { _ ->
            startActivity(
                Intent(this, EditOrganisationActivity::class.java)
            )
        }
        
        // Listen for page changes to update the fab
        tabs_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0 : Int) { }
            override fun onPageScrolled(p0 : Int, p1 : Float, p2 : Int) { }
            override fun onPageSelected(newPage : Int) {
                when (newPage) {
                    0 -> fab.show()
                    else -> fab.hide()
                }
            }
        })
    }
    
    override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        
        // Create an intent based on the id
        val intent = when (item.itemId) {
            R.id.action_even_more -> Intent(
                Intent.ACTION_VIEW, Uri.parse("http://irismsg.io")
            )
            R.id.action_open_lab -> Intent(
                Intent.ACTION_VIEW, Uri.parse("http://openlab.ncl.ac.uk")
            )
            R.id.action_donate -> Intent(
                this, DonateActivity::class.java
            )
            else -> null
        }
        
        // If an intent was created, start it
        if (intent != null) startActivity(intent)
    
        // If they clicked logout, perform the logout
        if (item.itemId == R.id.action_logout) {
            
            // Un-store the jwt and current user
            jwtService.save(null)
            UserEntity.current = null
            
            // Go to the onboarding activity
            startActivity(Intent(this, OnboardActivity::class.java))
            finish()
        }
        
        return super.onOptionsItemSelected(item)
    }
    
    override fun onOrganisationSelected(organisation: OrganisationEntity) {
        val currentUser = jwtService.getUserId() ?: return
        
        // If they are a coordinator, open the detail view
        if (organisation.isMember(currentUser, MemberRole.COORDINATOR)) {
            startActivity(
                Intent(this, OrganisationDetailActivity::class.java)
                        .putExtra(OrganisationDetailActivity.ARG_ORGANISATION_ID, organisation.id)
            )
        }
        
        // If they are just a donor, prompt them to undonate
        else if (organisation.isMember(currentUser, MemberRole.DONOR)) {
            attemptUndonate(organisation)
        }
    }
    
    private fun attemptUndonate (organisation : OrganisationEntity) {
        
        // Confirm the action with a dialog
        val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.title_undonate)
                .setMessage(R.string.body_undonate)
                .setPositiveButton(R.string.action_confirm) { _, _ -> performUndonate(organisation) }
                .setNegativeButton(R.string.action_cancel, null)
                .create()
        
        dialog.show()
    }
    
    private fun performUndonate (organisation: OrganisationEntity) {
        
        // Get the current user or stop
        val currentUser = jwtService.getUserId() ?: return
        
        // Get the member record or stop here
        val member = organisation.members.find {
                m -> m.isActive() && m.role === MemberRole.DONOR && m.userId == currentUser
        } ?: return
        
        
        // Delete the member record
        irisService.destroyMember(member.id, organisation.id).enqueue(ApiCallback({ res ->
            if (res.success) {
                Snackbar.make(main_content,
                    R.string.success_undonate,
                    Snackbar.LENGTH_LONG
                ).show()
                viewModel.reload()
            } else {
                Snackbar.make(
                    main_content,
                    res.messages.joinToString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }, { _ ->
            Snackbar.make(
                main_content,
                R.string.error_undonate,
                Snackbar.LENGTH_LONG
            ).show()
        }))
    }
    
    inner class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        
        private val titles = listOf(
            getString(R.string.tab_coordinator),
            getString(R.string.tab_donor)
        )
    
        private val pages = listOf<Fragment>(
            OrganisationListFragment.newInstance(MemberRole.COORDINATOR),
            OrganisationListFragment.newInstance(MemberRole.DONOR)
        )
    
        override fun getCount () = pages.size
        override fun getItem (index: Int) = pages[index]
        override fun getPageTitle (index: Int) : CharSequence? = titles[index]
    }
}