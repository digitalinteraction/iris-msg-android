package uk.ac.ncl.openlab.irismsg.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_onboard.*
import kotlinx.android.synthetic.main.fragment_onboard.view.*
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.common.PermissionsManager
import javax.inject.Inject

/**
 * An Activity to onboard the user into the app
 */
class OnboardActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    // Dagger injection point
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    override fun supportFragmentInjector() = dispatchingAndroidInjector
    
    @Inject lateinit var perms: PermissionsManager
    private var mSectionsPagerAdapter : SectionsPagerAdapter? = null
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup the view
        setContentView(R.layout.activity_onboard)
        setSupportActionBar(toolbar)
        
        // Create an adapter to serve the pages
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        tabs_pager.adapter = mSectionsPagerAdapter
        
        // Listen for login clicks
        login_button.setOnClickListener { _ ->
    
            perms.request(this, perms.defaultPermissions, PERMS_REQUEST_CODE) {
                startActivityForResult(
                    Intent(this, LoginActivity::class.java),
                    LoginActivity.REQUEST_LOGIN
                )
            }
        }
        
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults : IntArray) {
        
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode != PERMS_REQUEST_CODE) return
        
        val hasPermission = perms.checkResult(
            perms.defaultPermissions,
            permissions,
            grantResults
        )
        
        if (hasPermission) {
            startActivityForResult(
                Intent(this, LoginActivity::class.java),
                LoginActivity.REQUEST_LOGIN
            )
        } else {
            Snackbar.make(
                main_content,
                R.string.body_permissions_required,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
    
    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        // Close ourself if the login activity was successful
        if (requestCode == LoginActivity.REQUEST_LOGIN) {
            when (resultCode) {
                LoginActivity.RESULT_LOGGED_IN -> finish()
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu : Menu) : Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_onboard, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        
        // Processes menu clicks
        val intent = when (item.itemId) {
            R.id.action_even_more -> Intent(Intent.ACTION_VIEW, Uri.parse("http://irismsg.io"))
            R.id.action_open_lab  -> Intent(Intent.ACTION_VIEW, Uri.parse("http://openlab.ncl.ac.uk"))
            else -> null
        }
        
        // If there is an activity, start it
        if (intent != null) startActivity(intent)
        
        return super.onOptionsItemSelected(item)
    }
    
    
    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {
        
        override fun getItem(position : Int) : Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }
        
        override fun getCount() = 4
    }
    
    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {
        
        override fun onCreateView(inflater : LayoutInflater, container : ViewGroup?,
            savedInstanceState : Bundle?
        ) : View? {
            val rootView = inflater.inflate(R.layout.fragment_onboard, container, false)
            rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
            return rootView
        }
        
        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private const val ARG_SECTION_NUMBER = "section_number"
            
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
    
    companion object {
        private const val PERMS_REQUEST_CODE = 1
    }
}
