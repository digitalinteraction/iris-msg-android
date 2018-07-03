package uk.ac.ncl.openlab.irismsg.api

data class ApiResponse <T> (
    val success: Boolean,
    val messages: List<String> = listOf(),
    val data: T? = null
) {
    companion object {
        fun <T> success (data: T? = null) : ApiResponse<T> {
            return ApiResponse(true, listOf(), data)
        }
        fun <T> fail (messages : List<String> = listOf()) : ApiResponse<T> {
            return ApiResponse(false, messages)
        }
    }
}