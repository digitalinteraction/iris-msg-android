package uk.ac.ncl.openlab.irismsg.api

import com.squareup.moshi.Json

/**
 * The response envelope the api should return responses as, with some utils for creation / usage
 */
data class ApiResponse <T> (
    @Json(name = "meta") val meta: ApiMeta,
    @Json(name = "data") val data: T? = null
) {
    companion object {
        fun <T> success (data: T? = null) : ApiResponse<T> {
            return ApiResponse(
                ApiMeta(true, listOf()),
                data
            )
        }
        fun <T> fail (messages : List<String> = listOf()) : ApiResponse<T> {
            return ApiResponse(
                ApiMeta(false, messages)
            )
        }
        fun <T> create (
            success: Boolean = true, messages: List<String> = listOf(), data: T? = null
        ) : ApiResponse<T> {
            return ApiResponse(ApiMeta(success, messages), data)
        }
    }
    
    val success: Boolean
        get() = meta.success
    
    val messages: List<String>
        get() = meta.messages
}

data class ApiMeta(
    @Json(name = "success") val success: Boolean,
    @Json(name = "messages") val messages: List<String>
)