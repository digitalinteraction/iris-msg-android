package uk.ac.ncl.openlab.irismsg.api

import uk.ac.ncl.openlab.irismsg.common.ApiCall
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.common.MessageAttemptUpdate
import uk.ac.ncl.openlab.irismsg.model.*

interface IrisMsgService {
    
    // Auth Endpoints
    fun getSelf () : ApiCall<UserEntity>
    fun requestLogin (phoneNumber: String, countryCode: String) : ApiCall<Nothing>
    fun checkLogin (code: Int) : ApiCall<UserAuthEntity>
    fun updateFcm (fcmToken: String) : ApiCall<Nothing>
    
    // Organisations Endpoints
    fun listOrganisations () : ApiCall<List<OrganisationEntity>>
    fun showOrganisation (id: String) : ApiCall<OrganisationEntity>
    fun createOrganisation (name: String, info: String) : ApiCall<OrganisationEntity>
    fun destroyOrganisation (id: String) : ApiCall<Nothing>
    
    // Members Endpoints
    fun createMember (organisationId: String, role: MemberRole, phoneNumber: String, countryCode: String)
            : ApiCall<MemberEntity>
    fun destroyMember (memberId: String, organisationId: String) : ApiCall<Nothing>
    fun acceptMember (memberId: String) : ApiCall<UserAuthEntity>
    
    // Messages Endpoints
    fun createMessage (body: String, organisationId: String) : ApiCall<MessageEntity>
    fun listPendingMessages () : ApiCall<List<PendingMessageEntity>>
    fun updateMessageAttempts (updates: List<MessageAttemptUpdate>) : ApiCall<Nothing>
}
