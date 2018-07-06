package uk.ac.ncl.openlab.irismsg.api

import uk.ac.ncl.openlab.irismsg.common.ApiCall
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.common.MessageAttemptUpdate
import uk.ac.ncl.openlab.irismsg.model.*

class LiveIrisMsgService : IrisMsgService {
    override fun getSelf(): ApiCall<UserEntity> {
        TODO("not implemented")
    }
    
    override fun requestLogin(phoneNumber: String, countryCode: String): ApiCall<Nothing> {
        TODO("not implemented")
    }
    
    override fun checkLogin(code: Int): ApiCall<UserAuthEntity> {
        TODO("not implemented")
    }
    
    override fun updateFcm(fcmToken: String): ApiCall<Nothing> {
        TODO("not implemented")
    }
    
    override fun listOrganisations(): ApiCall<List<OrganisationEntity>> {
        TODO("not implemented")
    }
    
    override fun showOrganisation(id: String): ApiCall<OrganisationEntity> {
        TODO("not implemented")
    }
    
    override fun createOrganisation(name: String, info: String): ApiCall<OrganisationEntity> {
        TODO("not implemented")
    }
    
    override fun destroyOrganisation(id: String): ApiCall<Nothing> {
        TODO("not implemented")
    }
    
    override fun createMember(
        organisationId: String, role : MemberRole, phoneNumber: String, countryCode: String
    ): ApiCall<MemberEntity> {
        TODO("not implemented")
    }
    
    override fun destroyMember(memberId: String, organisationId: String): ApiCall<Nothing> {
        TODO("not implemented")
    }
    
    override fun acceptMember(memberId: String): ApiCall<UserAuthEntity> {
        TODO("not implemented")
    }
    
    override fun createMessage(body: String, organisationId: String): ApiCall<MessageEntity> {
        TODO("not implemented")
    }
    
    override fun listPendingMessages() : ApiCall<List<PendingMessageEntity>> {
        TODO("not implemented")
    }
    
    override fun updateMessageAttempts(updates: List<MessageAttemptUpdate>): ApiCall<Nothing> {
        TODO("not implemented")
    }
    
}