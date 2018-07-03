package uk.ac.ncl.openlab.irismsg.api

import retrofit2.Call
import uk.ac.ncl.openlab.irismsg.MessageAttemptState
import uk.ac.ncl.openlab.irismsg.model.*

data class MessageAttemptUpdate(
        val attemptId: String,
        val newState: MessageAttemptState
)

data class ApiResponse <T> (
    val success: Boolean,
    val messages: List<String>,
    val payload: T?
)

interface ApiCall<T> : Call<ApiResponse<T>>

interface ApiInterface {
    
    // Auth Endpoints
    fun getSelf () : ApiCall<UserEntity>
    fun requestLogin (phoneNumber: String, countryCode: String) : ApiCall<Void>
    fun checkLogin (code: Int) : ApiCall<UserAuthEntity>
    fun updateFcm (fcmToken: String) : ApiCall<Void>
    
    // Organisations Endpoints
    fun listOrganisations () : ApiCall<List<OrganisationEntity>>
    fun showOrganisation (id: String) : ApiCall<OrganisationEntity>
    fun createOrganisation (name: String, info: String) : ApiCall<OrganisationEntity>
    fun destroyOrganisation (id: String) : ApiCall<Void>
    
    // Members Endpoints
    fun createMember (organisationId: String, phoneNumber: String, countryCode: String)
            : ApiCall<MemberEntity>
    fun destroyMember (memberId: String, organisationId: String) : ApiCall<Void>
    fun acceptMember (memberId: String) : ApiCall<UserAuthEntity>
    
    // Messages Endpoints
    fun createMessage (body: String, organisationId: String) : ApiCall<MessageEntity>
    fun listMessageAttempts () : ApiCall<List<MessageAttemptEntity>>
    fun updateMessageAttempts (updates: List<MessageAttemptUpdate>) : ApiCall<Void>
}
