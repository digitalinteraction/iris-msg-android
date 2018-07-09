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
    private val successBlock : (res: ApiResponse<T>) -> Unit,
    private val failedBlock : (err: Throwable) -> Unit
) : retrofit2.Callback<ApiResponse<T>>, Injectable {
    
    override fun onResponse(call : Call<ApiResponse<T>>?, response : Response<ApiResponse<T>>?) {
        
        // If successful, call the block and stop
        val successBody = response?.body()
        if (successBody != null) {
            return successBlock(successBody)
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
    
                successBlock(ApiResponse.fail(messages))
            } catch (err: Exception) {
                failedBlock(err)
            }
        }
        
        val name = call?.request()?.url()
        throw RuntimeException("Unable to parse api response : $name")
    }
    
    override fun onFailure(call : Call<ApiResponse<T>>?, t : Throwable?) {
        failedBlock(t ?: RuntimeException("An unknown error occurred"))
    }
}