package uk.ac.ncl.openlab.irismsg.api

import retrofit2.http.*
import uk.ac.ncl.openlab.irismsg.common.ApiCall
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.model.*

/**
 * An interface for talking to the IrisMsg service, annotated for Retrofit
 */
interface IrisMsgService {
    
    //
    // Auth Endpoints
    //
    
    @GET("/users/me")
    fun getSelf () : ApiCall<UserEntity>
    
    @POST("/users/login/request")
    fun requestLogin (@Body body: RequestLoginRequest) : ApiCall<Any>
    
    @POST("/users/login/check")
    fun checkLogin (@Body body: CheckLoginRequest) : ApiCall<UserAuthEntity>
    
    @POST("/users/update_fcm")
    fun updateFcm (@Body body: UpdateFcmRequest) : ApiCall<Any>
    
    //
    // Organisations Endpoints
    //
    
    @GET("/organisations")
    fun listOrganisations () : ApiCall<List<OrganisationEntity>>
    
    @GET("/organisations/{org_id}")
    fun showOrganisation (@Path("org_id") id: String) : ApiCall<OrganisationEntity>
    
    @POST("/organisations")
    fun createOrganisation (@Body body: CreateOrganisationRequest) : ApiCall<OrganisationEntity>
    
    @DELETE("/organisations/{org_id}")
    fun destroyOrganisation (@Path("org_id") id: String) : ApiCall<Any>
    
    //
    // Members Endpoints
    //
    
    @POST("/organisations/{org_id}/members")
    fun createMember (@Path("org_id") organisationId: String, @Body body: CreateMemberRequest)
            : ApiCall<MemberEntity>
    
    @DELETE("/organisations/{org_id}/members/{mem_id}")
    fun destroyMember (@Path("mem_id") memberId: String, @Path("org_id") organisationId: String) : ApiCall<Any>
    
    @POST("/organisations/accept/{mem_id}")
    fun acceptMember (@Path("mem_id") memberId: String) : ApiCall<UserAuthEntity>
    
    //
    // Messages Endpoints
    //
    
    @POST("/messages")
    fun createMessage (@Body body: CreateMessageRequest) : ApiCall<MessageEntity>
    
    @GET("/messages/attempts")
    fun listPendingMessages () : ApiCall<List<PendingMessageEntity>>
    
    @POST("/messages/attempts")
    fun updateMessageAttempts (@Body body: UpdateMessageAttemptsRequest) : ApiCall<Any>
    
    
    //
    // Utilities (Wraps the object creation for @Body definitions)
    //
//    fun requestLogin (phoneNumber: String, countryCode: String) : ApiCall<Any> {
//        return requestLogin(RequestLoginRequest(phoneNumber, countryCode))
//    }
//
//    fun checkLogin (code: Int) : ApiCall<UserAuthEntity> {
//        return checkLogin(CheckLoginRequest(code))
//    }
//
//    fun updateFcm (fcmToken: String) : ApiCall<Any> {
//        return updateFcm(UpdateFcmRequest(fcmToken))
//    }
//
//    fun createOrganisation (name: String, info: String) : ApiCall<OrganisationEntity> {
//        return createOrganisation(CreateOrganisationRequest(name, info))
//    }
//
//    fun createMember (
//        organisationId: String, role: MemberRole, phoneNumber: String, countryCode: String
//    ) : ApiCall<MemberEntity> {
//        return createMember(
//            organisationId,
//            CreateMemberRequest(role, phoneNumber, countryCode)
//        )
//    }
//
//    fun createMessage (organisationId: String, content: String) : ApiCall<MessageEntity> {
//        return createMessage(CreateMessageRequest(content, organisationId))
//    }
//
//    fun updateMessageAttempts (updates: List<MessageAttemptUpdate>) : ApiCall<Any> {
//        return updateMessageAttempts(UpdateMessageAttemptsRequest(updates))
//    }
}
