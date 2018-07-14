package uk.ac.ncl.openlab.irismsg.jwt

import okhttp3.Interceptor
import okhttp3.Response

/**
 * An okhttp request interceptor to add JWT Auth headers based on the current JsonWebToken
 */
class JwtAuthorisationInterceptor (private val jwtService: JwtService) : Interceptor {
    override fun intercept(chain : Interceptor.Chain) : Response {
        
        // Create a cloned builder for the modified request
        val request = chain.request()
        val jwt = jwtService.current
        val builder = request.newBuilder()
        
        // If set, add the jwt bearer header
        if (jwt != null) {
            builder.addHeader("Authorization", "Bearer $jwt")
        }
        
        // Continue the chain with the (maybe modified) request
        return chain.proceed(builder.build())
    }
}