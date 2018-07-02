package uk.ac.ncl.openlab.irismsg.api

import uk.ac.ncl.openlab.irismsg.model.*

class LiveApi : ApiInterface {
    override fun getSelf(): ApiResponse<UserEntity> {
        TODO("not implemented")
    }
    
    override fun requestLogin(phoneNumber: String, countryCode: String): ApiResponse<Void> {
        TODO("not implemented")
    }
    
    override fun checkLogin(code: Int): ApiResponse<UserAuthEntity> {
        TODO("not implemented")
    }
    
    override fun updateFcm(fcmToken: String): ApiResponse<Void> {
        TODO("not implemented")
    }
    
    override fun listOrganisations(): ApiResponse<List<OrganisationEntity>> {
        TODO("not implemented")
    }
    
    override fun showOrganisation(id: String): ApiResponse<OrganisationEntity> {
        TODO("not implemented")
    }
    
    override fun createOrganisation(name: String, info: String): ApiResponse<OrganisationEntity> {
        TODO("not implemented")
    }
    
    override fun destroyOrganisation(id: String): ApiResponse<Void> {
        TODO("not implemented")
    }
    
    override fun createMember(organisationId: String, phoneNumber: String, countryCode: String): ApiResponse<MemberEntity> {
        TODO("not implemented")
    }
    
    override fun destroyMember(memberId: String, organisationId: String): ApiResponse<Void> {
        TODO("not implemented")
    }
    
    override fun acceptMember(memberId: String): ApiResponse<UserAuthEntity> {
        TODO("not implemented")
    }
    
    override fun createMessage(body: String, organisationId: String): ApiResponse<MessageEntity> {
        TODO("not implemented")
    }
    
    override fun listMessageAttempts(): ApiResponse<List<MessageAttemptEntity>> {
        TODO("not implemented")
    }
    
    override fun updateMessageAttempts(updates: List<MessageAttemptUpdate>): ApiResponse<Void> {
        TODO("not implemented")
    }
    
}