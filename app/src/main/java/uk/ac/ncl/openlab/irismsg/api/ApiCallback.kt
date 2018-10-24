package uk.ac.ncl.openlab.irismsg.api

import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Response
import uk.ac.ncl.openlab.irismsg.di.Injectable

/**
 * A util class to simplify Retrofit.enqueue method signatures into Kotlin lambdas
 * - Also manually processes error bodies (because Retrofit doesn't)
 */
class ApiCallback <T> (
    private val callback : (res: ApiResponse<T>) -> Unit
) : retrofit2.Callback<ApiResponse<T>>, Injectable {
    
    override fun onResponse (call : Call<ApiResponse<T>>?, response : Response<ApiResponse<T>>?) {
        
        // If successful, call the block and stop
        val successBody = response?.body()
        if (successBody != null) {
            return callback(successBody)
        }
        
        // If there was an error, attempt to process it
        val errorBody = response?.errorBody()
        if (errorBody != null) {
    
            // Try to processes the body as json and get the messages out
            return try {
                val res = JsonParser().parse(errorBody.string())
                val messages = res.asJsonObject["meta"]
                        .asJsonObject["messages"]
                        .asJsonArray
                        .map { it.asString }
    
                callback(ApiResponse.fail(messages))
            } catch (err: Exception) {
                failWithError(err)
            }
        }
        
        val name = call?.request()?.url()
        throw RuntimeException("Unable to parse api response : $name")
    }
    
    override fun onFailure (call : Call<ApiResponse<T>>?, t : Throwable?) {
        failWithError(t)
    }
    
    private fun failWithError (t: Throwable?) {
        val messages = listOf(t?.localizedMessage ?: "An unknown error occurred, please try again")
        callback(ApiResponse.fail(messages))
    }
}