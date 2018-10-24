package uk.ac.ncl.openlab.irismsg.activity

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_login.*
import uk.ac.ncl.openlab.irismsg.IrisMsgApp
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.CheckLoginRequest
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.api.RequestLoginRequest
import uk.ac.ncl.openlab.irismsg.common.ViewsUtil
import uk.ac.ncl.openlab.irismsg.jwt.JwtService
import uk.ac.ncl.openlab.irismsg.model.UserAuthEntity
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import java.util.*
import javax.inject.Inject

/**
 * An Activity to login the user using a phone number and verification code
 *
 * Parent: OnboardActivity
 */
class LoginActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    @Inject lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject lateinit var app: Application
    @Inject lateinit var irisService: IrisMsgService
    @Inject lateinit var viewsUtil: ViewsUtil
    @Inject lateinit var jwtService: JwtService
    
    private var currentState: State = State.REQUEST
    
    override fun supportFragmentInjector() = fragmentInjector
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup the view
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        
        // Listen for keyboard done events
        phone_number.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLoginRequest()
                viewsUtil.unFocus(currentFocus)
                return@OnEditorActionListener true
            }
            false
        })
    
        // Listen for keyboard done events
        verification_code.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLoginCheck()
                viewsUtil.unFocus(currentFocus)
                return@OnEditorActionListener true
            }
            false
        })
        
        // Listen for button clicks
        request_button.setOnClickListener { attemptLoginRequest() }
        check_button.setOnClickListener { attemptLoginCheck() }
        
        // Make terms / privacy links clickable
        terms_text_view.movementMethod = LinkMovementMethod.getInstance()
        
        // Reset the state
        listOf(login_progress, request_form, check_form).forEach { it.visibility = View.GONE }
    
        // Enter the requesting state
        enterState(State.REQUEST)
    }
    
    override fun onBackPressed() {
        // Override 'back' when checking to let the user try again
        when (currentState) {
            State.CHECK -> enterState(State.REQUEST)
            State.WORKING -> return
            else -> super.onBackPressed()
        }
    }
    
    private fun enterState (newState: State) {
        
        // Perform leaving transitions
        viewsUtil.toggleElem(when (currentState) {
            State.WORKING -> login_progress
            State.REQUEST -> request_form
            State.CHECK -> check_form
        }, false)
        
        // Set the state
        currentState = newState
    
        // Perform entering transitions
        viewsUtil.toggleElem(when (newState) {
            State.WORKING -> login_progress
            State.REQUEST -> request_form
            State.CHECK -> check_form
        }, true)
    }
    
    private fun attemptLoginRequest() {
    
        // Unfocus any fields
        viewsUtil.unFocus(currentFocus)
        
        // Reset errors.
        phone_number.error = null
        
        // Grab the submitted values
        var countryCodeStr = ""
        var phoneNumberStr = phone_number.text.toString()
        
        // The view to refocus if validation fails
        var toRefocus : View? = null
        
        // Parse the phone number
        val phoneNumber = parsePhoneNumber(phoneNumberStr)
        
        // Validate the phone number
        if (phoneNumber == null) {
            phone_number.error = getString(R.string.error_invalid_phone_number)
            toRefocus = phone_number
        } else {
            phoneNumberStr = phoneNumber.nationalNumber.toString()
            countryCodeStr = PhoneNumberUtil.getInstance().getRegionCodeForNumber(phoneNumber)
        }
        
        // Either refocus the invalid field or perform the login request
        if (toRefocus != null) toRefocus.requestFocus()
        else {
            label_sent.text = getString(R.string.login_code_sent, phone_number.text.toString())
            requestLoginCode(countryCodeStr, phoneNumberStr)
        }
    }
    
    private fun requestLoginCode (countryCode: String, phoneNumber: String) {
        
        // Enter the working state and clear previous api errors
        enterState(State.WORKING)
        viewsUtil.showApiError(api_error, null)
        
        // Perform the request and update the state accordingly
        irisService.requestLogin(RequestLoginRequest(phoneNumber, countryCode)).enqueue(ApiCallback { res ->
            if (!res.success) {
                enterState(State.REQUEST)
                viewsUtil.showApiError(api_error, res.messages.joinToString())
            } else {
                enterState(State.CHECK)
            }
        })
    }
    
    private fun attemptLoginCheck () {
    
        // Unfocus any fields
        viewsUtil.unFocus(currentFocus)
        
        // Clear previous errors
        verification_code.error = null
        
        // Get the code they entered
        val codeStr = verification_code.text.toString()
    
        // The field to refocus if invalid
        var toRefocus : View? = null
        
        // Validate the code they entered
        if (TextUtils.isEmpty(codeStr)) {
            verification_code.error = getString(R.string.error_field_required)
            toRefocus = verification_code
        } else if (!isValidVerificationCode(codeStr)) {
            verification_code.error = getString(R.string.error_invalid_verification_code)
            toRefocus = verification_code
        }
    
        // Either refocus the invalid field or perform the login request
        if (toRefocus != null) toRefocus.requestFocus()
        else checkLoginCode(codeStr.toInt())
    }
    
    private fun checkLoginCode (code: Int) {
        
        // Enter the working state
        enterState(State.WORKING)
        viewsUtil.showApiError(api_error, null)
    
        // Perform the request and update the state accordingly
        irisService.checkLogin(CheckLoginRequest(code)).enqueue(ApiCallback { res ->
            if (!res.success || res.data == null) {
                enterState(State.CHECK)
                viewsUtil.showApiError(api_error, res.messages.joinToString())
            } else {
                finishLogin(res.data)
            }
        })
    }
    
    private fun finishLogin (userAuth: UserAuthEntity) {
        
        // Save the token & update the current user
        jwtService.save(userAuth.token)
        UserEntity.current = userAuth.user
        
        // Update the user's fcm
        (app as? IrisMsgApp)?.updateFcm()
        
        // Move to the organisation list activity
        startActivity(Intent(this, OrganisationListActivity::class.java))
        setResult(RESULT_LOGGED_IN)
        finish()
    }
    
    private fun parsePhoneNumber (phoneNumberStr: String) : Phonenumber.PhoneNumber? {
        try {
            // Attempt to parse the phone number or return null
            val util = PhoneNumberUtil.getInstance()
            val locale = Locale.getDefault().country
            val number = util.parse(phoneNumberStr, locale) ?: return null
            return if (util.isValidNumber(number)) number else null
        } catch (e: Exception) {
            Log.d("#parsePhoneNumber", e.toString())
            return null
        }
    }
    
    private fun isValidVerificationCode(codeStr : String) : Boolean {
        val code = codeStr.toIntOrNull() ?: return false
        return code in 0..999999
    }
    
    companion object {
        const val REQUEST_LOGIN = 1
        
        const val RESULT_LOGGED_IN = 1
        const val RESULT_CANCELLED = 2
    }
    
    private enum class State {
        REQUEST, CHECK, WORKING
    }
}
