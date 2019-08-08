package uk.ac.ncl.openlab.irismsg.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.ContentResolver
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.EditText
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_organisation_detail.*
import kotlinx.android.synthetic.main.fragment_organisation_detail.view.*
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.CreateMemberRequest
import uk.ac.ncl.openlab.irismsg.api.CreateMessageRequest
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.common.EventBus
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.common.ViewsUtil
import uk.ac.ncl.openlab.irismsg.repo.OrganisationRepository
import uk.ac.ncl.openlab.irismsg.ui.MemberListFragment
import uk.ac.ncl.openlab.irismsg.ui.SendMessageFragment
import uk.ac.ncl.openlab.irismsg.viewmodel.OrganisationMembersViewModel
import uk.ac.ncl.openlab.irismsg.viewmodel.OrganisationViewModel
import java.util.*
import javax.inject.Inject
import android.provider.ContactsContract
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.util.Log


/**
 * An Activity to show an Organisation in detail
 *
 * Parent: OrganisationListActivity
 */
class OrganisationDetailActivity : AppCompatActivity(), HasSupportFragmentInjector, MemberListFragment.Listener, SendMessageFragment.Listener {
    
    @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var irisService: IrisMsgService
    @Inject lateinit var orgRepo: OrganisationRepository
    @Inject lateinit var viewsUtil : ViewsUtil
    @Inject lateinit var events: EventBus
    
    private lateinit var orgViewModel: OrganisationViewModel
    private lateinit var membersViewModel: OrganisationMembersViewModel
    private lateinit var organisationId: String
    private lateinit var pagerAdapter : PagerAdapter
    
    override fun supportFragmentInjector() = fragmentInjector
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup the view
        setContentView(R.layout.activity_organisation_detail)
        setSupportActionBar(toolbar)
        
        
        // Get the passed org id or fail
        organisationId = intent.extras.getString(ARG_ORGANISATION_ID)
                ?: throw RuntimeException("OrganisationDetailActivity not passed an Organisation id")
        
        
        // Create a ViewModel to handle the Organisations
        orgViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(OrganisationViewModel::class.java)
                .init(organisationId)
    
    
        // Create a ViewModel to handle the OrganisationMembers
        membersViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(OrganisationMembersViewModel::class.java)
                .init(organisationId)
        
        
        // Listen to changes for our ViewModel (ie it loading)
        orgViewModel.organisation.observe(this, Observer { org ->
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
                viewsUtil.unFocus(currentFocus)
                when (tab.position) {
                    0 -> fab.hide()
                    else -> fab.show()
                }
            }
    
            override fun onTabUnselected(tab : TabLayout.Tab) {}
    
            override fun onTabReselected(tab : TabLayout.Tab) {}
        })
    
    
        // Listen to fab clicks
        fab.setOnClickListener {
            when (tabs_pager.currentItem) {
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
        orgViewModel.organisation.value ?: return
        
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
        irisService.destroyOrganisation(organisationId).enqueue(ApiCallback { res ->
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
        })
    }
    
    private fun performDeleteMember (memberId: String) {
        
        // Perform the request
        irisService.destroyMember(memberId, organisationId).enqueue(ApiCallback { res ->
            if (res.success) {
                
                // Update the organisation ViewModel
                val member = orgViewModel.organisation.value?.members?.find { it.id == memberId }
                        ?: return@ApiCallback
                
                orgViewModel.organisation.value = orgViewModel.organisation.value?.apply {
                    members.removeAll { member -> member.id == memberId }
                }
    
                membersViewModel.members.value = membersViewModel.members.value?.filter {
                    it.id != memberId
                }
                
                // Tell the user
                Snackbar.make(
                    main_content,
                    getString(R.string.member_deleted, "Member"),
                    Snackbar.LENGTH_LONG
                )
                
            } else {
                
                // Present any errors
                Snackbar.make(
                    main_content,
                    res.messages.joinToString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }
    
    override fun onSendMessage (message: String) {
        if (message == "") return

        // Unfocus whatever is focussed
        viewsUtil.unFocus(currentFocus)
        fab.isEnabled = false

        // Tell the fragment to disable the submit button
        events.emit(SendMessageFragment.EVENT_TOGGLE_SUBMIT, false)

        // Make the API request to send the message
        val body = CreateMessageRequest(message, organisationId)
        irisService.createMessage(body).enqueue(ApiCallback { res ->
            fab.isEnabled = true

            // Reset the state on success
            if (res.success) {
                events.emit(SendMessageFragment.EVENT_RESET)
                viewsUtil.unFocus(currentFocus)
            }

            // Tell the fragment to un disable the submit field
            events.emit(SendMessageFragment.EVENT_TOGGLE_SUBMIT, true)

            // Use a snackbar to tell the user what happened
            Snackbar.make(
                main_content,
                when (res.success) {
                    true -> getString(R.string.message_sent)
                    else -> res.messages.joinToString()
                },
                Snackbar.LENGTH_LONG
            ).show()
        })
    }
    
    private fun onAddMember (role: MemberRole) {
        orgViewModel.organisation.value ?: return
        
        val title = getString(R.string.title_add_member, role.humanized.toLowerCase())

        val options = arrayOf(
                getString(R.string.action_add_from_contacts),
                getString(R.string.action_add_manually)
        )

        // Create a dialog to pick how they want to add a member
        val dialog = AlertDialog.Builder(this)
                .setTitle(title)
                .setItems(options) { dialog, which ->
                    when(which) {
                        0 -> addMemberFromContacts(role)
                        1 -> addMemberManually(role)
                    }
                }
                .setNegativeButton(R.string.action_cancel, null)
                .create()

        // Show the dialog
        dialog.show()
    }

    private fun addMemberManually(role: MemberRole) {

        val title = getString(R.string.title_add_member, role.humanized.toLowerCase())

        val message = when (role) {
            MemberRole.SUBSCRIBER -> getString(R.string.body_add_subscriber)
            else -> getString(R.string.body_add_donor)
        }

        // Create a dialog to add the member via a number/label form
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

    private fun addMemberFromContacts(role: MemberRole) {

        // Pick the type of request depending on the role passed
        val request = when (role) {
            MemberRole.DONOR -> REQUEST_DONOR_FROM_CONTACTS
            MemberRole.SUBSCRIBER -> REQUEST_SUB_FROM_CONTACTS
            else -> return
        }

        // Create an intent to pick a phone number
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }

        // Start the intent if we are allowed
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, request)
        }

    }
    
    private fun attemptAddMember (role: MemberRole, dialog: AlertDialog) {
        
        // Grab the text field & entered value
        val numberField = dialog.findViewById<EditText>(R.id.phone_number)!!
        val phoneNumberStr = numberField .text.toString()
    
        // Parse the phone number, using the phone's current local as a base
        val util = PhoneNumberUtil.getInstance()
        val number = util.parse(phoneNumberStr, Locale.getDefault().country)
        
        // Show an error and stop if the number is invalid
        if (!util.isValidNumber(number)) {
            numberField .error = getString(R.string.error_invalid_phone_number)
            return
        }

        // Get the value of the label field
        val labelField = dialog.findViewById<EditText>(R.id.member_label)!!
        val memberLabel = labelField.text.toString()
        
        // Perform the request
        performAddMember(
            role,
            util.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL),
            util.getRegionCodeForNumber(number),
            memberLabel
        )
        
        // Dismiss the dialog
        dialog.dismiss()
    }
    
    private fun performAddMember (
        role: MemberRole, phoneNumber: String, countryCode: String, label: String = ""
    ) {
        
        // Make the request
        val body = CreateMemberRequest(role, phoneNumber, countryCode, label)
        irisService.createMember(organisationId, body).enqueue(ApiCallback { res ->
            if (res.success && res.data != null) {
                
                // Let the user know it was successful
                Snackbar.make(
                    findViewById<View>(R.id.main_content),
                    getString(R.string.member_created, "Member"),
                    Snackbar.LENGTH_LONG
                ).show()
                
                // Add the member to the organisation (w/ data binding)
                orgViewModel.organisation.value = orgViewModel.organisation.value?.apply {
                    members.add(res.data)
                }
                
                membersViewModel.reload()
            } else {
                
                // Show the user the error
                Snackbar.make(
                    findViewById<View>(R.id.main_content),
                    res.messages.joinToString(),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }
    
    private fun handleContactPicked(contactUri: Uri, role: MemberRole) {
        // Pick which contact fields we want returned
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )

        // Create a query of the contacts database, using our special contactUri
        // The uri has special permission to access the contact without Permissions.READ_CONTACTS
        val cursor = contentResolver.query(contactUri, projection, null, null, null)
        try {
            cursor.moveToFirst()

            // Read the normalized phone numer (if there is one)
            var rawNumber = cursor.getString(
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER)
            )

            // Read the fallback number if there wasn't a normalized one
            if (rawNumber == null) {
                rawNumber = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
            }

            // Parse the phone number
            val util = PhoneNumberUtil.getInstance()
            val number = util.parse(rawNumber, Locale.getDefault().country)
    
            // Show an error and stop if the number is invalid
            if (!util.isValidNumber(number)) {
                return Snackbar.make(
                    findViewById<View>(R.id.main_content),
                    R.string.error_invalid_phone_number,
                    Snackbar.LENGTH_LONG
                ).show()
            }

            // Read the contact's name
            val name = cursor.getString(
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            )
    
            // Perform the API request to add the member
            performAddMember(
                role,
                util.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL),
                util.getRegionCodeForNumber(number),
                name
            )
        } catch (error: Error) {
        
        } finally {
            cursor?.close()
        }
    }
    
    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        if (resultCode != RESULT_OK || data == null) return

        // Handle the contact and pass the correct role based on the request code
        when (requestCode) {
            REQUEST_DONOR_FROM_CONTACTS  -> handleContactPicked(data.data, MemberRole.DONOR)
            REQUEST_SUB_FROM_CONTACTS -> handleContactPicked(data.data, MemberRole.SUBSCRIBER)
        }
    }
    
    companion object {
        const val ARG_ORGANISATION_ID = "organisation_id"
        const val REQUEST_DONOR_FROM_CONTACTS = 1
        const val REQUEST_SUB_FROM_CONTACTS = 2
    }
    
    inner class PagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {
    
        override fun getCount() = 3
        
        override fun getItem(position : Int) = when (position) {
            0 -> SendMessageFragment.newInstance()
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
            private const val ARG_SECTION_NUMBER = "section_number"
            
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
