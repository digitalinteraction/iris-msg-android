package uk.ac.ncl.openlab.irismsg.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import android.support.v4.app.Fragment
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.MenuItem
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector

import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.*
import uk.ac.ncl.openlab.irismsg.model.UserAuthEntity
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import javax.inject.Inject

private enum class LoginState {
    REQUEST, CHECK, WORKING
}

/**
 * A login screen that offers login via email/password.
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
        
        setContentView(R.layout.activity_login)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        phone_number.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLoginRequest()
                return@OnEditorActionListener true
            }
            false
        })
        
        request_button.setOnClickListener { attemptLoginRequest() }
        
        check_button.setOnClickListener { attemptLoginCheck() }
        
        terms_text_view.movementMethod = LinkMovementMethod.getInstance()
        
        // Reset the state
        listOf(login_progress, request_form, check_form).forEach {
            it.visibility = View.GONE
        }
    
        // Enter the requesting state
        enterState(LoginState.REQUEST)
    }
    
    override fun onBackPressed() {
        if (currentState == LoginState.CHECK) {
            enterState(LoginState.REQUEST)
        } else {
            super.onBackPressed()
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

    
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLoginRequest() {
        
        // Reset errors.
        phone_number.error = null
        country_code.error = null
        
        // Grab the submitted values
        val countryCodeStr = country_code.text.toString()
        val phoneNumberStr = phone_number.text.toString()
        
        var cancel = false
        var nextFocus : View? = null
        
        
        // Check for a valid email address.
        if (TextUtils.isEmpty(phoneNumberStr)) {
            phone_number.error = getString(R.string.error_field_required)
            nextFocus = phone_number
            cancel = true
        } else if (!isValidPhoneNumber(phoneNumberStr)) {
            phone_number.error = getString(R.string.error_invalid_phone_number)
            nextFocus = phone_number
            cancel = true
        }
        
        // Check for a country code
        if (TextUtils.isEmpty(countryCodeStr)) {
            country_code.error = getString(R.string.error_field_required)
            nextFocus = country_code
            cancel = true
        } else if (!isValidCountryCode(countryCodeStr)) {
            country_code.error = getString(R.string.error_invalid_country_code)
            nextFocus = country_code
            cancel = true
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
        
        val call = irisService.requestLogin(RequestLoginRequest(phoneNumber, countryCode))
        
        call.enqueue(ApiCallback({ res ->
            if (res == null) {
                enterState(LoginState.REQUEST)
                showApiError(getString(R.string.api_unknown_error))
            } else if (!res.success) {
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
        val call = irisService.checkLogin(CheckLoginRequest(code))
        call.enqueue(ApiCallback({ res ->
            if (res == null) {
                enterState(LoginState.CHECK)
                showApiError(getString(R.string.api_unknown_error))
            } else if (!res.success || res.data == null) {
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
    
        JsonWebToken.save(applicationContext, userAuth.token)
        UserEntity.current = userAuth.user
        
        Log.d("login:token", userAuth.token)
        Log.d("login:user", userAuth.user.toString())
        
        startActivity(Intent(this, OrganisationListActivity::class.java))
        setResult(RESULT_LOGGED_IN)
        finish()
    }
    
    
    private fun isValidPhoneNumber(phoneNumber : String) : Boolean {
        return phoneNumber.length > 5
    }
    
    private fun isValidCountryCode(countryCode : String) : Boolean {
        return countryCode.isNotEmpty()
    }
    
    private fun isValidVerificationCode(codeStr : String) : Boolean {
        val code = codeStr.toIntOrNull() ?: return false
        return code in 0..999999
    }
    
    /**
     * Shows the progress UI and hides the login form.
     */
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
