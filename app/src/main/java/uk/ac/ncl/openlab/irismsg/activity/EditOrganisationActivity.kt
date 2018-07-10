package uk.ac.ncl.openlab.irismsg.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_edit_organisation.*
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.CreateOrganisationRequest
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.common.ViewsUtil
import uk.ac.ncl.openlab.irismsg.repo.OrganisationRepository
import javax.inject.Inject

class EditOrganisationActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    
    override fun supportFragmentInjector() = dispatchingAndroidInjector
    
    
    @Inject lateinit var irisService: IrisMsgService
    @Inject lateinit var viewsUtil: ViewsUtil
    @Inject lateinit var orgRepo: OrganisationRepository
    
    private var currentState: State = State.INPUT
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContentView(R.layout.activity_edit_organisation)
        setSupportActionBar(toolbar)
        
        save_button.setOnClickListener { _ ->
            viewsUtil.unFocus(currentFocus)
            attemptSave()
        }
        
        listOf(api_progress, edit_form).forEach {
            it.visibility = View.GONE
        }
        
        enterState(State.INPUT)
    }
    
    private fun attemptSave () {
        
        var cancelFocus: View? = null
        
        val nameStr = name.text.toString()
        val infoStr = info.text.toString()
    
        if (TextUtils.isEmpty(info.text)) {
            info.error = getString(R.string.error_field_required)
            cancelFocus = info
        } else if (!isValidInfo(infoStr)) {
            info.error = getString(R.string.error_invalid_info)
            cancelFocus = info
        }
        
        if (TextUtils.isEmpty(name.text)) {
            name.error = getString(R.string.error_field_required)
            cancelFocus = name
        } else if (!isValidName(nameStr)) {
            name.error = getString(R.string.error_invalid_name)
            cancelFocus = name
        }
        
        if (cancelFocus != null) {
            cancelFocus.requestFocus()
        } else {
            saveOrganisation(nameStr, infoStr)
        }
    }
    
    private fun isValidName (nameStr: String) : Boolean {
        return nameStr.length >= 4
    }
    
    private fun isValidInfo (infoStr: String) : Boolean {
        return infoStr.length >= 4
    }
    
    private fun saveOrganisation (name: String, info: String) {
        enterState(State.WORKING)
        viewsUtil.showApiError(api_error, null)
        
        val body = CreateOrganisationRequest(name, info)
        
        irisService.createOrganisation(body).enqueue(ApiCallback({ res ->
            if (!res.success || res.data == null) {
                enterState(State.INPUT)
                viewsUtil.showApiErrors(api_error, res.messages)
            } else {
                // Add Org to OrganisationRepo
//                viewModel.push(res.data)
                orgRepo.organisationCreated(res.data)
                
                // Move to Org Detail?
                setResult(RESULT_CREATED)
                finish()
            }
        }, { _ ->
            enterState(State.INPUT)
            viewsUtil.showApiError(api_error, getString(R.string.api_unknown_error))
        }))
    }
    
    private fun enterState (newState: State) {
        viewsUtil.toggleElem(when (currentState) {
            State.INPUT -> edit_form
            State.WORKING -> api_progress
            else -> null
        }, false)
        
        currentState = newState
    
        viewsUtil.toggleElem(when (newState) {
            State.INPUT -> edit_form
            State.WORKING -> api_progress
            else -> null
        }, true)
    }
    
    companion object {
        const val MODE_ADD = 1
        const val MODE_EDIT = 2
        
        const val RESULT_CREATED = 10
        
        const val EXTRA_ORG = 20
    }
    
    enum class State {
        INPUT, WORKING, SUCCESS
    }
}