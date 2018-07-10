package uk.ac.ncl.openlab.irismsg.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_login.*
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.*
import uk.ac.ncl.openlab.irismsg.common.ViewsUtil
import uk.ac.ncl.openlab.irismsg.model.UserAuthEntity
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import java.util.*
import javax.inject.Inject

/**
 * An Activity to login the user using a phone number and verification code
 */
class LoginActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    
    override fun supportFragmentInjector() = dispatchingAndroidInjector
    
    
    
    @Inject lateinit var irisService: IrisMsgService
    @Inject lateinit var viewsUtil: ViewsUtil
    
    private var currentState: State = State.REQUEST
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup the view
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
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
        listOf(login_progress, request_form, check_form).forEach {
            it.visibility = View.GONE
        }
    
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
    
        viewsUtil.unFocus(currentFocus)
        
        // Reset errors.
        phone_number.error = null
        
        // Grab the submitted values
        var countryCodeStr = ""
        var phoneNumberStr = phone_number.text.toString()
        
        var cancel = false
        var nextFocus : View? = null
        
        val phoneNumber = parsePhoneNumber(phoneNumberStr)
        
        if (phoneNumber == null) {
            phone_number.error = getString(R.string.error_invalid_phone_number)
            nextFocus = phone_number
            cancel = true
        } else {
            phoneNumberStr = phoneNumber.nationalNumber.toString()
            countryCodeStr = PhoneNumberUtil.getInstance().getRegionCodeForNumber(phoneNumber)
        }
        
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            nextFocus?.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            requestLoginCode(countryCodeStr, phoneNumberStr)
        }
    }
    
    private fun requestLoginCode (countryCode: String, phoneNumber: String) {
        
        enterState(State.WORKING)
        showApiError(null)
        
        irisService.requestLogin(RequestLoginRequest(phoneNumber, countryCode)).enqueue(ApiCallback({ res ->
            if (!res.success) {
                enterState(State.REQUEST)
                showApiError(res.messages.joinToString())
            } else {
                enterState(State.CHECK)
            }
        }, { _ ->
            enterState(State.REQUEST)
            showApiError(getString(R.string.api_unknown_error))
        }))
    }
    
    
    private fun attemptLoginCheck () {
    
        viewsUtil.unFocus(currentFocus)
        
        verification_code.error = null
        
        val codeStr = verification_code.text.toString()
    
        var cancel = false
        var nextFocus : View? = null
        
        if (TextUtils.isEmpty(codeStr)) {
            verification_code.error = getString(R.string.error_field_required)
            nextFocus = verification_code
            cancel = true
        } else if (!isValidVerificationCode(codeStr)) {
            verification_code.error = getString(R.string.error_invalid_verification_code)
            nextFocus = verification_code
            cancel = true
        }
        
        if (cancel) {
            nextFocus?.requestFocus()
        } else {
            checkLoginCode(codeStr.toInt())
        }
    }
    
    private fun checkLoginCode (code: Int) {
        enterState(State.WORKING)
        showApiError(null)
    
        irisService.checkLogin(CheckLoginRequest(code)).enqueue(ApiCallback({ res ->
            if (!res.success || res.data == null) {
                enterState(State.CHECK)
                showApiError(res.messages.joinToString())
            } else {
                finishLogin(res.data)
            }
        }, { _ ->
            enterState(State.CHECK)
            showApiError(getString(R.string.api_unknown_error))
        }))
    }
    
    private fun finishLogin (userAuth: UserAuthEntity) {
        
        // Save the token & update the current user
        JsonWebToken.save(applicationContext, userAuth.token)
        UserEntity.current = userAuth.user
        
        // Move to the organisation list activity
        startActivity(Intent(this, OrganisationListActivity::class.java))
        setResult(RESULT_LOGGED_IN)
        finish()
    }
    
    private fun parsePhoneNumber (phoneNumberStr: String) : Phonenumber.PhoneNumber? {
        val util = PhoneNumberUtil.getInstance()
        val number = util.parse(phoneNumberStr, Locale.getDefault().country) ?: return null
        return if (util.isValidNumber(number)) number else null
    }
    
    private fun isValidVerificationCode(codeStr : String) : Boolean {
        val code = codeStr.toIntOrNull() ?: return false
        return code in 0..999999
    }
    
    /**
     * Shows an api error or hides the control
     */
    private fun showApiError (error: String?) {
        when (error) {
            null -> {
                api_error.visibility = View.GONE
                api_error.text = ""
            }
            else -> {
                api_error.visibility = View.VISIBLE
                api_error.text = error
            }
        }
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
