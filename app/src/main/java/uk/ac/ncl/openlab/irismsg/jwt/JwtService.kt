package uk.ac.ncl.openlab.irismsg.jwt

import com.auth0.android.jwt.JWT

/**
 * A service for accessing & storing the authentication jwt in SharedPreferences
 */
interface JwtService {
    val current: JWT?
    fun save (rawToken : String?) : JWT?
    
    fun getUserId () : String? = current?.getClaim("usr")?.asString()
    fun getLocale () : String? = current?.getClaim("loc")?.asString()
}