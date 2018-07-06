package uk.ac.ncl.openlab.irismsg.api

import android.content.Context
import com.auth0.android.jwt.JWT

private const val fakedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c3IiOiJjdXJyZW50LXVzZXItaWQiLCJpYXQiOjE1MzA3MTM1NzZ9.swsQwUsEVghOiABq2Dokm3iM3aIaDQ9X4jd_B5RRH8g"

class JsonWebToken (token: String) : JWT(token) {
    
    companion object {
        private const val RAW_TOKEN_KEY = "raw_json_web_token"
        private const val PREFS_KEY = "jwt"
        
        private fun getPrefs (ctx: Context) = ctx.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        
        fun load (ctx: Context) : JsonWebToken? {
//            val raw = getPrefs(ctx)?.getString(RAW_TOKEN_KEY, null) ?: return null
            return JsonWebToken(fakedToken)
        }
        
        fun save (ctx: Context, token: String) {
            val prefs = getPrefs(ctx) ?: return
            with (prefs.edit()) {
                putString(RAW_TOKEN_KEY, token)
                commit()
            }
        }
    }
    
    fun getUserId () : String? = this.getClaim("usr").asString()
}