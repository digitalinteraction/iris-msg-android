package uk.ac.ncl.openlab.irismsg.common

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Application
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A utility for Activities/Fragments to perform common actions, designed to be @Inject'd in
 */
@Singleton
class ViewsUtil @Inject constructor(val app: Application) {
    
    /** Toggle an element with an short fade animation */
    fun toggleElem (view: View?, show: Boolean) {
        view ?: return
        
        // Get the animation time
        val shortAnimTime = app.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        
        // Set the visibility
        view.visibility = if (show) View.VISIBLE else View.GONE
        
        // Animate the alpha
        view.animate()
                .setDuration(shortAnimTime)
                .alpha((if (show) 1 else 0).toFloat())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation : Animator) {
                        view.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }
    
    /** Show an api error or hide its field if null */
    fun showApiError (apiView: TextView, error: String?) {
        when (error) {
            null -> {
                apiView.visibility = View.GONE
                apiView.text = ""
            }
            else -> {
                apiView.visibility = View.VISIBLE
                apiView.text = error
            }
        }
    }
    
    /** Show a set of api errors, joining them with commas */
    fun showApiErrors (apiView: TextView, errors: List<String>) {
        showApiError(apiView, errors.joinToString())
    }
    
    /** Unfocus the currently focused input */
    fun unFocus (currentFocus: View?) {
        currentFocus ?: return
        val currentInput = app.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        currentInput.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
    
}