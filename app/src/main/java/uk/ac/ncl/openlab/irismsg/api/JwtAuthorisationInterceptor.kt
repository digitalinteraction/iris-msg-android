package uk.ac.ncl.openlab.irismsg.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * An okhttp request interceptor to add JWT Auth headers based on the current JsonWebToken
 */
class JwtAuthorisationInterceptor (private val ctx: Context) : Interceptor {
    override fun intercept(chain : Interceptor.Chain) : Response {
        
        // Create a cloned builder for the modified request
        val request = chain.request()
        val jwt = JsonWebToken.load(ctx)
        val builder = request.newBuilder()
        
        // If set, add the jwt bearer header
        jwt?.let { jwt ->
            builder.addHeader("Authorization", "Bearer $jwt")
        }
        
        // Continue the chain with the (maybe modified) request
        return chain.proceed(builder.build())
    }
}