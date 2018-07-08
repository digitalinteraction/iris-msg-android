package uk.ac.ncl.openlab.irismsg.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.common.MessageAttemptState
import javax.inject.Inject
import okhttp3.ResponseBody
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.squareup.moshi.Moshi
import uk.ac.ncl.openlab.irismsg.di.DaggerAppComponent
import uk.ac.ncl.openlab.irismsg.di.Injectable


data class MessageAttemptUpdate(
    val attemptId: String,
    val newState: MessageAttemptState
)



data class RequestLoginRequest(
    val phoneNumber: String,
    val countryCode: String
)

data class CheckLoginRequest(
    val code: Int
)

data class UpdateFcmRequest(
    val fcmToken: String
)

data class CreateOrganisationRequest(
    val name: String,
    val info: String
)

data class CreateMemberRequest(
    val role: MemberRole,
    val phoneNumber: String,
    val countryCode: String
)

data class CreateMessageRequest(
    val body: String,
    val orgId: String
)

data class UpdateMessageAttemptsRequest(
    val updates: List<MessageAttemptUpdate>
)


class ApiCallback <T> @Inject constructor(
    private val successBlock : (res: T?) -> Unit,
    private val failedBlock : (err: Throwable?) -> Unit
) : retrofit2.Callback<T>, Injectable {
    
    @Inject
    lateinit var moshi : Moshi
    
    override fun onResponse(call : Call<T>?, response : Response<T>?) {
        val successBody = response?.body()
        if (successBody != null) {
            return successBlock(successBody)
        }
        
        call?.request()
        
        
        
        val errorBody = response?.errorBody()
        if (errorBody != null) {
//            val converter = retrofit.responseBodyConverter<T?>(
//                ApiResponse::class.java,
//                arrayOfNulls(0)
//            )
//
//            val res = converter.convert(errorBody)
//
//            if (res != null) return successBlock(res)
//            val errorConverter = retrofit
//                    .responseBodyConverter(Error::class.java, arrayOfNulls(0))
        }
        
        val name = call?.request()?.url()
        throw RuntimeException("Unable to parse api response : $name")
    }
    
    override fun onFailure(call : Call<T>?, t : Throwable?) {
        failedBlock(t)
    }
}