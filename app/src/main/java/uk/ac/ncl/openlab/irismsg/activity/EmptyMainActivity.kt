package uk.ac.ncl.openlab.irismsg.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.jwt.JwtService
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import javax.inject.Inject

/**
 * An empty Activity to check if there is a current user and push the OrgList / Onboarding accordingly
 */
class EmptyMainActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    // Dagger injection point
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    override fun supportFragmentInjector() = dispatchingAndroidInjector
    
    
    @Inject lateinit var irisService : IrisMsgService
    @Inject lateinit var jwtService: JwtService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // See if there is a current user
        val jwt = jwtService.current

        // If there isn't, push them to onboarding
        if (jwt == null) {
            pushOnboard()
        } else {
            
            // If there is a token, fetch the current user
            irisService.getSelf().enqueue(ApiCallback({ res ->
                UserEntity.current = res.data
                when (UserEntity.current) {
                    null -> pushOnboard()
                    else -> pushOrgList()
                }
            }, { t ->
                Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_SHORT).show()
                pushOnboard()
            }))
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