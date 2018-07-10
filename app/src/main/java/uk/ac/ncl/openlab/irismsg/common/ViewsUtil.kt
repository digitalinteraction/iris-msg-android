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

@Singleton
class ViewsUtil @Inject constructor(val app: Application) {
    
    fun toggleElem (view: View?, show: Boolean) {
        view ?: return
        
        val shortAnimTime = app.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        
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
    
    fun showApiErrors (apiView: TextView, errors: List<String>) {
        showApiError(apiView, errors.joinToString())
    }
    
    fun unFocus (currentFocus: View?) {
        currentFocus ?: return
        val currentInput = app.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
        currentInput.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
    
}