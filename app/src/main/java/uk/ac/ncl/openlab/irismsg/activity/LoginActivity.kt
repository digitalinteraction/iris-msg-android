package uk.ac.ncl.openlab.irismsg.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import android.support.v4.app.Fragment
import android.text.method.LinkMovementMethod
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector

import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.ac.ncl.openlab.irismsg.R
import uk.ac.ncl.openlab.irismsg.api.ApiResponse
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
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
        setupActionBar()
        
        phone_number.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        
        sign_in_button.setOnClickListener { attemptLogin() }
        
        terms_text_view.movementMethod = LinkMovementMethod.getInstance()
        
        // Reset the state
        listOf(login_progress, request_form, check_form).forEach {
            it.visibility = View.GONE
        }
    
        // Enter the requesting state
        enterState(LoginState.REQUEST)
    }
    
    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private fun setupActionBar() {
        
        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun enterState (newState: LoginState) {
        
        // Perform leaving transitions
        when (currentState) {
            LoginState.WORKING -> showProgress(false)
            LoginState.REQUEST -> request_form.visibility = View.GONE
            LoginState.CHECK -> check_form.visibility = View.GONE
        }
        
        // Set the state
        currentState = newState
    
        // Perform entering transitions
        when (newState) {
            LoginState.WORKING -> showProgress(true)
            LoginState.REQUEST -> request_form.visibility = View.VISIBLE
            LoginState.CHECK -> check_form.visibility = View.VISIBLE
        }
    }

    
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        
        // Reset errors.
        phone_number.error = null
        country_code.error = null
        api_error_text_view.text = ""
        api_error_text_view.visibility = View.GONE
        
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
        irisService.requestLogin(phoneNumber, countryCode).enqueue(object : Callback<ApiResponse<Nothing>> {
            override fun onResponse(call : Call<ApiResponse<Nothing>>?, response : Response<ApiResponse<Nothing>>?) {
                
                val body = response?.body()
                if (body == null) {
                    enterState(LoginState.REQUEST)
                    showApiError(getString(R.string.api_unknown_error))
                } else if (!body.success) {
                    enterState(LoginState.REQUEST)
                    showApiError(body.messages.joinToString())
                } else {
                    enterState(LoginState.CHECK)
                }
            }
            override fun onFailure(call : Call<ApiResponse<Nothing>>?, t : Throwable?) {
                enterState(LoginState.REQUEST)
            }
        })
    }
    
    private fun isValidPhoneNumber(phoneNumber : String) : Boolean {
        return phoneNumber.length > 5
    }
    
    private fun isValidCountryCode(countryCode : String) : Boolean {
        return countryCode.isNotEmpty()
    }
    
    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show : Boolean) {
        
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        
        request_form.visibility = if (show) View.GONE else View.VISIBLE
        request_form.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 0 else 1).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation : Animator) {
                        request_form.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })
        
        login_progress.visibility = if (show) View.VISIBLE else View.GONE
        login_progress.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation : Animator) {
                        login_progress.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }
    
    /**
     * Shows an api error or hides the control
     */
    private fun showApiError(error: String?) {
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
    
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
//    inner class UserLoginTask internal constructor(private val mEmail : String, private val mPassword : String) : AsyncTask<Void, Void, Boolean>() {
//
//        override fun doInBackground(vararg params : Void) : Boolean? {
//            // TODO: attempt authentication against a network service.
//
//            try {
//                // Simulate network access.
//                Thread.sleep(2000)
//            } catch (e : InterruptedException) {
//                return false
//            }
//
//            return DUMMY_CREDENTIALS
//                    .map { it.split(":") }
//                    .firstOrNull { it[0] == mEmail }
//                    ?.let {
//                        // Account exists, return true if the password matches.
//                        it[1] == mPassword
//                    }
//                    ?: true
//        }
//
//        override fun onPostExecute(success : Boolean?) {
//            mAuthTask = null
//            showProgress(false)
//
//            if (success!!) {
//                finish()
//            } else {
//                password.error = getString(R.string.error_incorrect_password)
//                password.requestFocus()
//            }
//        }
//
//        override fun onCancelled() {
//            mAuthTask = null
//            showProgress(false)
//        }
//    }
    
//    companion object {
//
//        /**
//         * Id to identity READ_CONTACTS permission request.
//         */
//        private val REQUEST_READ_CONTACTS = 0
//
//        /**
//         * A dummy authentication store containing known user names and passwords.
//         * TODO: remove after connecting to a real authentication system.
//         */
//        private val DUMMY_CREDENTIALS = arrayOf(
//            "foo@example.com:hello",
//            "bar@example.com:world"
//        )
//    }
}
