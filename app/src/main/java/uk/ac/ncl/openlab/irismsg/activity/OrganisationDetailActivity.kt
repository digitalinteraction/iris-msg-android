package uk.ac.ncl.openlab.irismsg.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector

import uk.ac.ncl.openlab.irismsg.R
import kotlinx.android.synthetic.main.activity_organisation_detail.*
import kotlinx.android.synthetic.main.fragment_organisation_detail.view.*
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.viewmodel.OrganisationViewModel
import javax.inject.Inject

/**
 * An Activity to show an Organisation in detail
 * TODO - Setup the tabs
 * TODO - Setup the fab
 * TODO - Add a dialog to the destroy click
 */
class OrganisationDetailActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    // Dagger injection point
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    override fun supportFragmentInjector() = dispatchingAndroidInjector
    
    
    private lateinit var viewModel: OrganisationViewModel
    private lateinit var organisationId: String
    
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var irisService: IrisMsgService
    
    private var mSectionsPagerAdapter : SectionsPagerAdapter? = null
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup the view
        setContentView(R.layout.activity_organisation_detail)
        setSupportActionBar(toolbar)
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        
        // Get the passed org id or fail
        organisationId = intent.extras.getString(ORGANISATION_ID_KEY)
                ?: throw RuntimeException("OrganisationDetailActivity not passed an Organisation id")
        
        
        // Create a ViewModel to handle the api entity
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(OrganisationViewModel::class.java)
                .init(organisationId)
        
        
        // Listen to changes for our ViewModel (ie it loading)
        viewModel.organisation.observe(this, Observer { org ->
            if (org != null) {
                supportActionBar?.title = org.name
                info.text = org.info
            } else {
                finish()
            }
        })
        
        // TODO - Setup the tabs
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    
    
        // TODO - Setup the fab
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
    
    
    override fun onCreateOptionsMenu(menu : Menu) : Boolean {
        menuInflater.inflate(R.menu.menu_organisation_detail, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        
        if (item.itemId== R.id.action_delete_organisation) {
            
            // Destroy the organisation
            irisService.destroyOrganisation(organisationId).enqueue(ApiCallback({ res ->
                if (res.success) finish()
                else Snackbar.make(toolbar, res.messages.joinToString(), Snackbar.LENGTH_LONG).show()
            }, { _ ->
                TODO("Handle orgs.destroy api failure")
            }))
            
            return true
        }
        
        return super.onOptionsItemSelected(item)
    }
    
    companion object {
        const val ORGANISATION_ID_KEY = "organisation_id"
    }
    
    
    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {
        
        override fun getItem(position : Int)
                = PlaceholderFragment.newInstance(position + 1)
    
        override fun getCount() = 3
    }
    
    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {
        
        override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?,
            savedInstanceState : Bundle?
        ) : View? {
            val rootView = inflater.inflate(R.layout.fragment_organisation_detail, container, false)
            rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
            return rootView
        }
        
        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"
            
            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber : Int) : PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
