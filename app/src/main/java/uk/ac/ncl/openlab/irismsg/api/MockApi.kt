package uk.ac.ncl.openlab.irismsg.api

import retrofit2.mock.Calls
import uk.ac.ncl.openlab.irismsg.model.*

class MockApi : ApiInterface {
    
    private val generator = EntityGenerator()
    
    override fun getSelf(): ApiCall<UserEntity> {
        return Calls.response(
            ApiResponse.success(generator.makeUser(UserGen.CURRENT))
        )
    }
    override fun requestLogin(phoneNumber: String, countryCode: String): ApiCall<Nothing> {
        return Calls.response(ApiResponse(
            phoneNumber != "fail"
        ))
    }
    override fun checkLogin(code: Int): ApiCall<UserAuthEntity> {
        return Calls.response(
            if (code >= 0) ApiResponse.success(generator.makeUserAuth())
            else ApiResponse.fail()
        )
    }
    override fun updateFcm(fcmToken: String): ApiCall<Nothing> {
        return Calls.response(ApiResponse.success())
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
    
    
    override fun createMember(organisationId: String, phoneNumber: String, countryCode: String)
            : ApiCall<MemberEntity> {
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
    override fun listMessageAttempts(): ApiCall<List<MessageAttemptEntity>> {
        TODO("not implemented")
    }
    override fun updateMessageAttempts(updates: List<MessageAttemptUpdate>): ApiCall<Nothing> {
        TODO("not implemented")
    }
    
}