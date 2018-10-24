package uk.ac.ncl.openlab.irismsg.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_empty_main.*
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.jwt.JwtService
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import javax.inject.Inject

/**
 * An empty Activity to check if there is a current user and push the OrgList / Onboarding accordingly
 *
 * Links:
 *   android.intent.action.MAIN
 *
 * Serves as the entrypoint to the app and performs checks before proceeding
 *  - Pings to ensure the backend is available
 *  - Fetches the current user using the stored jwt, if there is one
 *  - Goes to onboarding if there is no logged in user
 *  - Goes to the organisation list activity if there is a user
 */
class EmptyMainActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var irisService : IrisMsgService
    @Inject lateinit var jwtService: JwtService
    
    override fun supportFragmentInjector() = fragmentInjector
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        // Setup view
        setContentView(R.layout.activity_empty_main)
        setSupportActionBar(toolbar)
        
        // Start checking the network
        checkNetwork()
        
        // Listen for try again clicks
        try_again.setOnClickListener { checkNetwork() }
    }
    
    private fun checkNetwork () {
        retry_form.visibility = View.GONE
        irisService.ping().enqueue(ApiCallback { res ->
            if (res.success) {
                checkAuth()
            } else {
                retry_form.visibility = View.VISIBLE
                Log.e("api.health", res.messages.joinToString())
            }
        })
    }
    
    private fun checkAuth () {
        // See if there is a current user
        val jwt = jwtService.current
    
        // If there isn't, push them to onboarding
        if (jwt == null) {
            pushOnboard()
        } else {
        
            // If there is a token, fetch the current user
            irisService.getSelf().enqueue(ApiCallback { res ->
                UserEntity.current = res.data
                when (UserEntity.current) {
                    null -> pushOnboard()
                    else -> pushOrgList()
                }
            })
        }
    }
    
    private fun pushOnboard () {
        startActivity(Intent(this, OnboardActivity::class.java))
        finish()
    }
    
    private fun pushOrgList () {
        startActivity(Intent(this, OrganisationListActivity::class.java))
        finish()
    }
}