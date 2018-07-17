package uk.ac.ncl.openlab.irismsg.activity

import android.app.Application
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
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
import uk.ac.ncl.openlab.irismsg.common.ViewsUtil
import uk.ac.ncl.openlab.irismsg.jwt.JwtService
import uk.ac.ncl.openlab.irismsg.model.MemberInviteEntity
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import javax.inject.Inject

class AcceptRoleActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    @Inject lateinit var fragmentInjector : DispatchingAndroidInjector<Fragment>
    @Inject lateinit var jwtService: JwtService
    @Inject lateinit var irisService: IrisMsgService
    @Inject lateinit var viewsUtil: ViewsUtil
    @Inject lateinit var app: Application
    
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
        if (uri.pathSegments.size != 2 || uri.pathSegments[0] != "invite") {
            TODO("Handle invalid uri error")
        }
        
        
        // Make the terms clickable
        terms.movementMethod = LinkMovementMethod.getInstance()
        
        
        // Store the token
        inviteToken = uri.pathSegments[1]
    
        
        // Hide ui elements for now
        listOf(api_progress, accept_form, api_error).map { it.visibility = View.GONE }
        
        
        // Animate in the progress spinner
        enterState(State.WORKING)
        
        
        // Setup accept button click listener
        accept_button.setOnClickListener { acceptInvite(inviteToken) }
        
        
        // Fetch the role to accept
        irisService.showInvite(inviteToken).enqueue(ApiCallback({ res ->
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
            
        }, { _ ->
            TODO("Handle members.showInvite error")
        }))
    }
    
    private fun acceptInvite (token: String) {
        enterState(State.WORKING)
        
        irisService.acceptInvite(token).enqueue(ApiCallback({ res ->
    
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
        }, { _ ->
            TODO("Handle members.acceptInvite error")
        }))
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
}
