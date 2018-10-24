package uk.ac.ncl.openlab.irismsg.activity

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_accept_role.*
import uk.ac.ncl.openlab.irismsg.IrisMsgApp
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.common.PermissionsManager
import uk.ac.ncl.openlab.irismsg.common.ViewsUtil
import uk.ac.ncl.openlab.irismsg.jwt.JwtService
import uk.ac.ncl.openlab.irismsg.model.MemberInviteEntity
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import javax.inject.Inject

/**
 * An activity to deep link to and accept a role invitation
 * Also manages app permissions, requesting them from the user
 *
 * Links:
 *   irismsg://invite
 *   https://api.dev.irismsg.io/open/invite/.*
 *   https://api.irismsg.io/open/invite/.*
 */
class AcceptRoleActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    @Inject lateinit var fragmentInjector : DispatchingAndroidInjector<Fragment>
    @Inject lateinit var jwtService: JwtService
    @Inject lateinit var irisService: IrisMsgService
    @Inject lateinit var viewsUtil: ViewsUtil
    @Inject lateinit var app: Application
    @Inject lateinit var perms: PermissionsManager
    
    private lateinit var inviteToken: String
    private var invite: MemberInviteEntity? = null
    private var currentState: State = State.NONE
    
    override fun supportFragmentInjector() = fragmentInjector
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accept_role)
        setSupportActionBar(toolbar)
        
        // Parse member id from deep link
        val uri = intent.data
        
        
        // Fail for invalid uris
        if (uri.pathSegments.size == 0) {
            TODO("Handle invalid uri error")
        }
        
        
        // Make the terms clickable
        terms.movementMethod = LinkMovementMethod.getInstance()
        
        
        // Store the token
        inviteToken = uri.pathSegments.last()
    
        
        // Hide ui elements for now
        listOf(api_progress, accept_form, api_error).map { it.visibility = View.GONE }
        
        
        // Animate in the progress spinner
        enterState(State.WORKING)
        
        
        // Setup accept button click listener
        accept_button.setOnClickListener { _ ->
            perms.request(this, perms.defaultPermissions, PERMS_REQUEST_CODE) {
                acceptInvite(inviteToken)
            }
        }
        
        
        // Fetch the role to accept
        irisService.showInvite(inviteToken).enqueue(ApiCallback { res ->
            invite = res.data
            
            // Hide the spinner
            viewsUtil.toggleElem(api_progress, false)
            
            if (res.success && res.data != null) {
                val org = res.data.organisation
                
                // Show the accept form & set fields based on the response
                enterState(State.VIEWING)
                role_info.text = getString(R.string.body_donor_role_info, org.name)
                
                org_name.text = org.name
                org_info.text = org.info
                
            } else {
                
                // Show the api error(s)
                enterState(State.ERROR)
                viewsUtil.showApiErrors(api_error, res.messages)
            }
            Log.d("showInvite", res.data.toString())
            
        })
    }
    
    override fun onRequestPermissionsResult(
        requestCode : Int, permissions : Array<out String>, grantResults : IntArray) {
        
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    
        if (requestCode != PERMS_REQUEST_CODE) return
        
        val hasPermission = perms.checkResult(
            perms.defaultPermissions,
            permissions,
            grantResults
        )
        
        if (hasPermission) {
            acceptInvite(inviteToken)
        } else {
            Snackbar.make(
                main_content,
                getString(R.string.body_permissions_required),
                Snackbar.LENGTH_LONG
            )
        }
    }
    
    private fun acceptInvite (token: String) {
        enterState(State.WORKING)
        
        irisService.acceptInvite(token).enqueue(ApiCallback { res ->
            
            if (res.success && res.data != null) {
                jwtService.save(res.data.token)
                UserEntity.current = res.data.user
                
                (app as? IrisMsgApp)?.updateFcm()
                
                startActivity(Intent(this, OrganisationListActivity::class.java))
                finish()
            } else {
                enterState(State.ERROR)
                viewsUtil.showApiErrors(api_error, res.messages)
            }
        })
    }
    
    private fun enterState (state: State) {
        
        // Toggle out the old element
        viewsUtil.toggleElem(when (currentState) {
            State.VIEWING -> accept_form
            State.WORKING -> api_progress
            else -> null
        }, false)
        
        // Set the state
        currentState = state
    
        // Toggle in the new element
        viewsUtil.toggleElem(when (state) {
            State.VIEWING -> accept_form
            State.WORKING -> api_progress
            else -> null
        }, true)
    }
    
    enum class State {
        VIEWING, WORKING, ERROR, NONE
    }
    
    companion object {
        const val PERMS_REQUEST_CODE = 1
    }
}
