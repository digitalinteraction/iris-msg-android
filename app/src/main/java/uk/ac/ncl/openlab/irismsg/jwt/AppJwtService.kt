package uk.ac.ncl.openlab.irismsg.jwt

import android.app.Application
import android.content.Context
import com.auth0.android.jwt.JWT

/**
 * A JwtService which pulls from an App's SharedPreferences, designed to be @Injected'd in
 */
class AppJwtService (val app: Application) : JwtService {
    
    override var current: JWT? = null
    private var ctx: Context = app.applicationContext
    
    init {
        getPrefs()?.getString(RAW_TOKEN_KEY, null)?.let { rawToken ->
            current = JWT(rawToken)
        }
    }
    
    
    /** Fetch shared preferences from our context */
    private fun getPrefs () = ctx.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
    
    /** Save/Delete a token using our context */
    override fun save (rawToken: String?) : JWT? {
        current = if (rawToken != null) JWT(rawToken) else null
        
        getPrefs()?.edit()?.apply {
            putString(RAW_TOKEN_KEY, rawToken)
            apply()
        }
        
        return current
    }
    
    companion object {
        private const val RAW_TOKEN_KEY = "raw_json_web_token"
        private const val PREFS_KEY = "jwt"
    }
}