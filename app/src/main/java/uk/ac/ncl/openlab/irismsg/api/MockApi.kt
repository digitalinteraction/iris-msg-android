package uk.ac.ncl.openlab.irismsg.api

import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.Calls
import uk.ac.ncl.openlab.irismsg.common.ApiCall
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.common.MessageAttemptUpdate
import uk.ac.ncl.openlab.irismsg.model.*

class MockIrisMsgService : IrisMsgService {
    
    private val generator = EntityGenerator()
    
    private fun <T> success (data: T? = null) : ApiCall<T> {
        return Calls.response(ApiResponse.success(data))
    }
    
    override fun getSelf(): ApiCall<UserEntity> {
        return success(
            generator.makeUser(UserGen.CURRENT)
        )
    }
    override fun requestLogin(phoneNumber: String, countryCode: String): ApiCall<Nothing> {
        return Calls.response(ApiResponse(phoneNumber != "fail"))
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
        return success(listOf(
            generator.makeOrganisation(OrganisationGen.COORDINATOR),
            generator.makeOrganisation(OrganisationGen.COORDINATOR),
            generator.makeOrganisation(OrganisationGen.DONOR),
            generator.makeOrganisation(OrganisationGen.DONOR),
            generator.makeOrganisation(OrganisationGen.DONOR)
        ))
    }
    override fun showOrganisation(id: String): ApiCall<OrganisationEntity> {
        return success(
            generator.makeOrganisation(OrganisationGen.COORDINATOR)
        )
    }
    override fun createOrganisation(name: String, info: String): ApiCall<OrganisationEntity> {
        val org = generator.makeOrganisation(OrganisationGen.COORDINATOR)
        org.name = name
        org.info = info
        return success(org)
    }
    override fun destroyOrganisation(id: String): ApiCall<Nothing> {
        return success()
    }
    
    
    override fun createMember(
        organisationId: String, role: MemberRole, phoneNumber: String, countryCode: String
    ) : ApiCall<MemberEntity> {
        return success(generator.makeMember(role, UserGen.VERIFIED))
    }
    override fun destroyMember(memberId: String, organisationId: String): ApiCall<Nothing> {
        return success()
    }
    override fun acceptMember(memberId: String): ApiCall<UserAuthEntity> {
        return success(generator.makeUserAuth())
    }
    
    
    override fun createMessage(body: String, organisationId: String): ApiCall<MessageEntity> {
        return success(generator.makeMessage(organisationId))
    }
    override fun listPendingMessages() : ApiCall<List<PendingMessageEntity>> {
        return success(listOf(
            generator.makePendingMessage("1", "Hello there"),
            generator.makePendingMessage("2", "General Kenobi"),
            generator.makePendingMessage("2", "You are a bold one")
        ))
    }
    override fun updateMessageAttempts(updates: List<MessageAttemptUpdate>): ApiCall<Nothing> {
        return success()
    }
    
}