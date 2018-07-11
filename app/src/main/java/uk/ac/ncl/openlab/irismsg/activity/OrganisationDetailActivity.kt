package uk.ac.ncl.openlab.irismsg.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.support.design.widget.TabLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector

import uk.ac.ncl.openlab.irismsg.R
import kotlinx.android.synthetic.main.activity_organisation_detail.*
import kotlinx.android.synthetic.main.fragment_organisation_detail.view.*
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.CreateMemberRequest
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.repo.OrganisationRepository
import uk.ac.ncl.openlab.irismsg.ui.MemberListFragment
import uk.ac.ncl.openlab.irismsg.viewmodel.OrganisationViewModel
import java.util.*
import javax.inject.Inject

/**
 * An Activity to show an Organisation in detail
 * TODO - Setup the tabs
 * TODO - Setup the fab
 * TODO - Add a dialog to the destroy click
 */
class OrganisationDetailActivity : AppCompatActivity(), HasSupportFragmentInjector, MemberListFragment.Listener {
    
    // Dagger injection point
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    override fun supportFragmentInjector() = dispatchingAndroidInjector
    
    private lateinit var viewModel: OrganisationViewModel
    private lateinit var organisationId: String
    private lateinit var pagerAdapter : PagerAdapter
    
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var irisService: IrisMsgService
    @Inject lateinit var orgRepo: OrganisationRepository
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup the view
        setContentView(R.layout.activity_organisation_detail)
        setSupportActionBar(toolbar)
        
        
        // Get the passed org id or fail
        organisationId = intent.extras.getString(ARG_ORGANISATION_ID)
                ?: throw RuntimeException("OrganisationDetailActivity not passed an Organisation id")
        
        
        // Create a ViewModel to handle the api entity
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(OrganisationViewModel::class.java)
                .init(organisationId)
        
        
        // Listen to changes for our ViewModel (ie it loading)
        viewModel.organisation.observe(this, Observer { org ->
            if (org != null) {
                supportActionBar?.title = org.name
                organisation_info.text = org.info
            }
        })
        
        
        // Setup tabs
        pagerAdapter = PagerAdapter(supportFragmentManager)
        tabs_pager.adapter = pagerAdapter
        tabs_layout.setupWithViewPager(tabs_pager)
        
        
        // Listen for tab changes and set the fab icon
        tabs_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
    
            override fun onTabSelected(tab : TabLayout.Tab) {
                fab.setImageDrawable(ContextCompat.getDrawable(applicationContext, when (tab.position) {
                    0 -> R.drawable.ic_send_black_24dp
                    else -> R.drawable.ic_add_black_24dp
                }))
            }
    
            override fun onTabUnselected(tab : TabLayout.Tab) {}
    
            override fun onTabReselected(tab : TabLayout.Tab) {}
        })
    
    
        // Setup fab
        fab.setOnClickListener { view ->
            when (tabs_pager.currentItem) {
                0 -> onSendMessage()
                1 -> onAddMember(MemberRole.DONOR)
                2 -> onAddMember(MemberRole.SUBSCRIBER)
            }
        }
    }
    
    
    override fun onCreateOptionsMenu(menu : Menu) : Boolean {
        menuInflater.inflate(R.menu.menu_organisation_detail, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        
        if (item.itemId == R.id.action_delete_organisation) {
    
            // Confirm the deletion
            AlertDialog.Builder(this)
                    .setTitle(R.string.title_confirm_delete_org)
                    .setMessage(R.string.body_confirm_delete_org)
                    .setPositiveButton(R.string.action_delete) { _, _ -> performDeleteOrganisation() }
                    .setNegativeButton(R.string.action_cancel, null)
                    .create()
                    .show()
            
            return true
        }
        
        return super.onOptionsItemSelected(item)
    }
    
    override fun onDeleteMember(memberId: String, role: MemberRole) {
        viewModel.organisation.value ?: return
        
        // Format the title of the alert
        val title = getString(
            R.string.title_confirm_delete_member,
            role.humanized.toLowerCase()
        )
        
        // Confirm the deletion
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(R.string.body_confirm_delete_member)
                .setPositiveButton(R.string.action_confirm) { _, _ -> performDeleteMember(memberId) }
                .setNegativeButton(R.string.action_cancel) { alert, _ -> alert.dismiss() }
                .create()
                .show()
    }
    
    private fun performDeleteOrganisation () {
    
        // Destroy the organisation
        irisService.destroyOrganisation(organisationId).enqueue(ApiCallback({ res ->
            if (res.success) {
                
                // Update the cache and finish
                orgRepo.organisationDestroyed(organisationId)
                finish()
            }
            else {
                
                // Present any errors
                Snackbar.make(
                    main_content,
                    res.messages.joinToString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }, { _ ->
            TODO("Handle orgs.destroy api failure")
        }))
    }
    
    private fun performDeleteMember (memberId: String) {
        
        // Perform the request
        irisService.destroyMember(memberId, organisationId).enqueue(ApiCallback({ res ->
            if (res.success) {
                
                // Update the organisation ViewModel
                viewModel.organisation.value = viewModel.organisation.value?.apply {
                    members.removeAll { member -> member.id == memberId }
                }
            } else {
                
                // Present any errors
                Snackbar.make(
                    main_content,
                    res.messages.joinToString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }, { _ ->
            TODO("Handle orgs.members.destroy api failure")
        }))
    }
    
    private fun onSendMessage () {
        // TODO ...
    }
    
    private fun onAddMember (role: MemberRole) {
        val organisation = viewModel.organisation.value ?: return
        
        val title = getString(R.string.title_add_member, role.humanized.toLowerCase())
        
        val message = when (role) {
            MemberRole.SUBSCRIBER -> getString(R.string.body_add_subscriber)
            else -> getString(R.string.body_add_donor)
        }
        
        // Create a dialog to add the member
        val dialog = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setView(R.layout.dialog_add_member)
                .setPositiveButton(R.string.action_confirm, null)
                .setNegativeButton(R.string.action_cancel, null)
                .create()
        
        // Show the dialog
        dialog.show()
    
        // Override the click handler to perform validation
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            attemptAddMember(role, dialog)
        }
    }
    
    private fun attemptAddMember (role: MemberRole, dialog: AlertDialog) {
        
        // Grab the text field & entered value
        val field = dialog.findViewById<EditText>(R.id.phone_number)!!
        val phoneNumberStr = field.text.toString()
    
        // Parse the phone number, using the phone's current local as a base
        val util = PhoneNumberUtil.getInstance()
        val number = util.parse(phoneNumberStr, Locale.getDefault().country)
        
        // Show an error and stop if the number is invalid
        if (!util.isValidNumber(number)) {
            field.error = getString(R.string.error_invalid_phone_number)
            return
        }
        
        // Perform the request
        performAddMember(
            role,
            number.nationalNumber.toString(),
            util.getRegionCodeForNumber(number)
        )
        
        // Dismiss the dialog
        dialog.dismiss()
    }
    
    private fun performAddMember (role: MemberRole, phoneNumber: String, countryCode: String) {
        
        // Make the request
        val body = CreateMemberRequest(role, phoneNumber, countryCode)
        irisService.createMember(organisationId, body).enqueue(ApiCallback({ res ->
            if (res.success && res.data != null) {
                
                // Let the user know it was successful
                Snackbar.make(
                    findViewById<View>(R.id.main_content),
                    getString(R.string.member_created, phoneNumber),
                    Snackbar.LENGTH_LONG
                ).show()
                
                // Add the member to the organisation (w/ data binding)
                viewModel.organisation.value = viewModel.organisation.value?.apply {
                    members.add(res.data)
                }
            } else {
                
                // Show the user the error
                Snackbar.make(
                    findViewById<View>(R.id.main_content),
                    res.messages.joinToString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }, { _ ->
            TODO("Handle orgs.members.create api failure")
        }))
    }
    
    companion object {
        const val ARG_ORGANISATION_ID = "organisation_id"
    }
    
    inner class PagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {
    
        override fun getCount() = 3
        
        override fun getItem(position : Int) = when (position) {
            1 -> MemberListFragment.newInstance(MemberRole.DONOR, organisationId)
            2 -> MemberListFragment.newInstance(MemberRole.SUBSCRIBER, organisationId)
            else -> PlaceholderFragment.newInstance(position + 1)
        }
    
        override fun getPageTitle(position : Int) : CharSequence? = when (position) {
            0 -> getString(R.string.tab_messaging)
            1 -> getString(R.string.tab_org_donors)
            2 -> getString(R.string.tab_org_subscribers)
            else -> null
        }
    }
    
    class PlaceholderFragment : Fragment() {
        
        override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?,
            savedInstanceState : Bundle?
        ) : View? {
            val rootView = inflater.inflate(R.layout.fragment_organisation_detail, container, false)
            rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
            return rootView
        }
        
        companion object {
            /** The fragment argument representing the section number for this fragment. */
            private const val ARG_SECTION_NUMBER = "section_number"
            
            /** Returns a new instance of this fragment for the given section number. */
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
