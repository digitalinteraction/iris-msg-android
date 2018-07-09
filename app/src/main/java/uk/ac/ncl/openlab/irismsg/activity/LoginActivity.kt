package uk.ac.ncl.openlab.irismsg.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.*
import uk.ac.ncl.openlab.irismsg.model.UserAuthEntity
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import java.util.*
import javax.inject.Inject

private enum class LoginState {
    REQUEST, CHECK, WORKING
}

/**
 * An Activity to login the user using a phone number and verification code
 */
class LoginActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    
    override fun supportFragmentInjector() = dispatchingAndroidInjector
    
    @Inject
    lateinit var irisService: IrisMsgService
    
    private var currentState: LoginState = LoginState.REQUEST
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup the view
        setContentView(R.layout.activity_login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        // Listen for keyboard done events
        phone_number.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLoginRequest()
                return@OnEditorActionListener true
            }
            false
        })
    
        // Listen for keyboard done events
        verification_code.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLoginCheck()
                return@OnEditorActionListener true
            }
            false
        })
        
        // Listen for button clicks
        request_button.setOnClickListener { attemptLoginRequest() }
        check_button.setOnClickListener { attemptLoginCheck() }
        
        // Make terms / privact links clickable
        terms_text_view.movementMethod = LinkMovementMethod.getInstance()
        
        // Reset the state
        listOf(login_progress, request_form, check_form).forEach {
            it.visibility = View.GONE
        }
    
        // Enter the requesting state
        enterState(LoginState.REQUEST)
    }
    
    override fun onBackPressed() {
        // Override 'back' when checking to let the user try again
        when (currentState) {
            LoginState.CHECK -> enterState(LoginState.REQUEST)
            LoginState.WORKING -> return
            else -> super.onBackPressed()
        }
    }
    
    private fun enterState (newState: LoginState) {
        
        // Perform leaving transitions
        toggleElem(when (currentState) {
            LoginState.WORKING -> login_progress
            LoginState.REQUEST -> request_form
            LoginState.CHECK -> check_form
        }, false)
        
        // Set the state
        currentState = newState
    
        // Perform entering transitions
        toggleElem(when (newState) {
            LoginState.WORKING -> login_progress
            LoginState.REQUEST -> request_form
            LoginState.CHECK -> check_form
        }, true)
    }
    
    private fun attemptLoginRequest() {
        
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
        
        enterState(LoginState.WORKING)
        showApiError(null)
        
        irisService.requestLogin(RequestLoginRequest(phoneNumber, countryCode)).enqueue(ApiCallback({ res ->
            if (!res.success) {
                enterState(LoginState.REQUEST)
                showApiError(res.messages.joinToString())
            } else {
                enterState(LoginState.CHECK)
            }
        }, { _ ->
            enterState(LoginState.REQUEST)
            showApiError(getString(R.string.api_unknown_error))
        }))
    }
    
    
    private fun attemptLoginCheck () {
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
        enterState(LoginState.WORKING)
        showApiError(null)
    
        irisService.checkLogin(CheckLoginRequest(code)).enqueue(ApiCallback({ res ->
            if (!res.success || res.data == null) {
                enterState(LoginState.CHECK)
                showApiError(res.messages.joinToString())
            } else {
                finishLogin(res.data)
            }
        }, { _ ->
            enterState(LoginState.CHECK)
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
    
    private fun toggleElem (view: View, show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    
        view.visibility = if (show) View.VISIBLE else View.GONE
        view.animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation : Animator) {
                    view.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
    }
    
    /**
     * Shows an api error or hides the control
     */
    private fun showApiError (error: String?) {
        when (error) {
            null -> {
                api_error_text_view.visibility = View.GONE
                api_error_text_view.text = ""
            }
            else -> {
                api_error_text_view.visibility = View.VISIBLE
                api_error_text_view.text = error
            }
        }
    }
    
    companion object {
        const val REQUEST_LOGIN = 1
        
        const val RESULT_LOGGED_IN = 1
        const val RESULT_CANCELLED = 2
    }
}
