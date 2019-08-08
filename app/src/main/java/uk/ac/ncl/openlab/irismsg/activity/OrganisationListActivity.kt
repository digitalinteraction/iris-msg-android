package uk.ac.ncl.openlab.irismsg.activity

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.g00fy2.versioncompare.Version
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_organisation_list.*
import uk.ac.ncl.openlab.irismsg.BuildConfig
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
 *
 * Parent: EmptyMainActivity
 */
class OrganisationListActivity : AppCompatActivity(),
        HasSupportFragmentInjector,
        OrganisationListFragment.Listener {
    
    @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var jwtService: JwtService
    @Inject lateinit var irisService: IrisMsgService
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    
    private lateinit var pagerAdapter: PagerAdapter
    private lateinit var viewModel: OrganisationListViewModel
    
    override fun supportFragmentInjector() = fragmentInjector
    
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
        fab.setOnClickListener {
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

        checkForUpdate()
    }

    override fun onPause() {
        super.onPause()

        // Store the current tab, so we can come back to the same one
        val prefs = getSharedPreferences("OrganisationListActivity", Context.MODE_PRIVATE).edit()
        prefs.putInt(PERSISTENT_TAB_KEY, tabs_pager.currentItem)
        prefs.apply()

    }

    override fun onResume() {
        super.onResume()

        // Get the current tab from the preferences, or default to 0
        val prefs = getSharedPreferences("OrganisationListActivity", Context.MODE_PRIVATE)
        tabs_pager.currentItem = prefs.getInt(PERSISTENT_TAB_KEY, 0)
    }
    
    override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item : MenuItem) : Boolean {

        // If they pressed info, dump information into a dialog
        if (item.itemId == R.id.action_app_info) {
            val message = "" +
                    "Iris Msg: " + BuildConfig.VERSION_NAME + "\n\n" +
                    "Server: " + BuildConfig.SERVER_URL + "\n" +
                    "Build: " + BuildConfig.BUILD_TYPE + "\n" +
                    "Package: " + BuildConfig.APPLICATION_ID

            val alert = AlertDialog.Builder(this)
                    .setTitle(R.string.title_app_info)
                    .setPositiveButton(R.string.action_confirm) { _, _ -> }
                    .setMessage(message)
                    .create()
                    .show()
            
            return true
        }
        
        // Try to create an intent based on the id of the chosen menu item
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

        // If they pressed 'check for update', do the check
        if (item.itemId == R.id.action_check_for_update) {
            checkForUpdate { isNewVersion ->
                if (isNewVersion) return@checkForUpdate

                // If there wasn't a new version, let them know
                AlertDialog.Builder(this)
                        .setTitle(R.string.title_no_update)
                        .setMessage(R.string.body_no_update)
                        .setPositiveButton(R.string.action_confirm) { _, _ -> }
                        .show()
            }
        }
        
        return super.onOptionsItemSelected(item)
    }
    
    override fun onOrganisationSelected(organisation: OrganisationEntity, asRole: MemberRole) {

        // Decide what to do based on the role tab they came from
        // - as a coordinator, go to the organisation edit activity
        // - as a donor, go to the donation activity (slightly hacky UX)
        when (asRole) {
            MemberRole.COORDINATOR -> {
                startActivity(
                        Intent(this, OrganisationDetailActivity::class.java)
                                .putExtra(OrganisationDetailActivity.ARG_ORGANISATION_ID, organisation.id)
                )
            }
            MemberRole.DONOR -> {
                startActivity(Intent(this, DonateActivity::class.java))
            }
            else -> { }
        }
    }

    override fun onOrganisationHeld(organisation: OrganisationEntity, asRole: MemberRole): Boolean {

        // Decide what to do based on the role tab they came from
        // - as a donor, attempt to undonate
        return when (asRole) {
            MemberRole.DONOR -> {
                attemptUndonate(organisation)
                true
            }
            else -> false
        }
    }
    
    private fun attemptUndonate (organisation : OrganisationEntity) {
        
        // Confirm the action with a dialog
        AlertDialog.Builder(this)
                .setTitle(R.string.title_undonate)
                .setMessage(R.string.body_undonate)
                .setPositiveButton(R.string.action_confirm) { _, _ -> performUndonate(organisation) }
                .setNegativeButton(R.string.action_cancel, null)
                .create()
                .show()
    }
    
    private fun performUndonate (organisation: OrganisationEntity) {
        
        // Get the current user or stop
        val currentUser = jwtService.getUserId() ?: return
        
        // Get the member record or stop here
        val member = organisation.members.find {
                m -> m.isActive() && m.role === MemberRole.DONOR && m.userId == currentUser
        } ?: return
        
        
        // Delete the member record
        irisService.destroyMember(member.id, organisation.id).enqueue(ApiCallback { res ->
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
        })
    }

    private fun checkForUpdate(handler: (isNewVersion: Boolean) -> Unit = { _ -> }) {

        // Check we're running the latest version of the app
        // If not, prompt the user to get the latest version
        irisService.getAppVersion().enqueue(ApiCallback { res ->
            try {
                if (res.data == null) return@ApiCallback handler(false)

                // Parse the local version and the one returned from the API
                val localVersion = Version(BuildConfig.VERSION_NAME)
                val serverVersion = Version(res.data.version)

                // Do nothing if our version is greater or equal to the server version
                if (localVersion.isAtLeast(serverVersion)) return@ApiCallback handler(false)

                // Create an intent to open the url where the update is
                val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(res.data.url)
                )

                // Let the handler know
                handler(true)

                // Let the user choose what to do
                AlertDialog.Builder(this)
                        .setTitle(R.string.title_new_app_update)
                        .setMessage(R.string.body_new_app_update)
                        .setPositiveButton(R.string.action_update_app) { _, _ ->
                            startActivity(intent)
                            System.exit(0)
                        }
                        .setNegativeButton(R.string.action_skip_update) { _, _ -> }
                        .show()
            } catch (e: Exception) {
                // Report any errors processing semvers
                Crashlytics.log(Log.ERROR, "#getAppVersion", e.localizedMessage)
            }
        })
    }

    companion object {
        const val PERSISTENT_TAB_KEY = "current_tab"
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