package uk.ac.ncl.openlab.irismsg.api

import retrofit2.Call
import uk.ac.ncl.openlab.irismsg.MessageAttemptState
import uk.ac.ncl.openlab.irismsg.model.*

data class MessageAttemptUpdate(
        val attemptId: String,
        val newState: MessageAttemptState
)

interface ApiResponse<T> : Call<T> {
    val success: Boolean
    val messages: List<String>
    val payload: T?
}

interface ApiInterface {
    
    // Auth Endpoints
    fun getSelf () : ApiResponse<UserEntity>
    fun requestLogin (phoneNumber: String, countryCode: String) : ApiResponse<Void>
    fun checkLogin (code: Int) : ApiResponse<UserAuthEntity>
    fun updateFcm (fcmToken: String) : ApiResponse<Void>
    
    // Organisations Endpoints
    fun listOrganisations () : ApiResponse<List<OrganisationEntity>>
    fun showOrganisation (id: String) : ApiResponse<OrganisationEntity>
    fun createOrganisation (name: String, info: String) : ApiResponse<OrganisationEntity>
    fun destroyOrganisation (id: String) : ApiResponse<Void>
    
    // Members Endpoints
    fun createMember (organisationId: String, phoneNumber: String, countryCode: String)
            : ApiResponse<MemberEntity>
    fun destroyMember (memberId: String, organisationId: String) : ApiResponse<Void>
    fun acceptMember (memberId: String) : ApiResponse<UserAuthEntity>
    
    // Messages Endpoints
    fun createMessage (body: String, organisationId: String) : ApiResponse<MessageEntity>
    fun listMessageAttempts () : ApiResponse<List<MessageAttemptEntity>>
    fun updateMessageAttempts (updates: List<MessageAttemptUpdate>) : ApiResponse<Void>
}
