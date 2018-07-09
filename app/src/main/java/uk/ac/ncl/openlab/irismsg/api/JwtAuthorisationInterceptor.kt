package uk.ac.ncl.openlab.irismsg.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * An okhttp request interceptor to add JWT Auth headers based on the current JsonWebToken
 */
class JwtAuthorisationInterceptor (private val ctx: Context) : Interceptor {
    override fun intercept(chain : Interceptor.Chain) : Response {
        
        val request = chain.request()
        val jwt = JsonWebToken.load(ctx)
        val builder = request.newBuilder()
        
        jwt?.apply {
            builder.addHeader("Authorization", "Bearer $this")
        }
        
        return chain.proceed(builder.build())
    }
}