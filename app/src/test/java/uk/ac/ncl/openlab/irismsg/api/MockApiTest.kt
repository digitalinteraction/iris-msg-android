package uk.ac.ncl.openlab.irismsg.api

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import uk.ac.ncl.openlab.irismsg.common.ApiCall
import uk.ac.ncl.openlab.irismsg.common.MemberRole

class MockApiTest {
    
    private lateinit var api: IrisMsgService
    
    private val createOrgBody = CreateOrganisationRequest("Test name", "Test info")
    private val createMsgBody = CreateMessageRequest("Hello, World!", "1")
    private val updateAttemptsBody = UpdateMessageAttemptsRequest(listOf())
    
    @Before fun setUp () {
        api = MockIrisMsgService()
    }
    
    fun <T> assertSuccess (call: ApiCall<T>) {
        val body = call.execute().body()
        assertTrue(body?.success ?: false)
    }
    
    fun <T> assertFail (call: ApiCall<T>) {
        val body = call.execute().body()
        assertFalse(body?.success ?: true)
    }
    
    fun <T> assertData(call: ApiCall<T>) {
        val body = call.execute().body()
        assertNotNull(body?.data)
    }
    
    // MockApi#getSelf
    
    @Test fun getSelf_isSuccessful () {
        val res = api.getSelf().execute().body()
        assertTrue(res?.success ?: false)
    }
    @Test fun getSelf_isCurrentUser () {
        val res = api.getSelf().execute().body()
        assertEquals(EntityGenerator.currentUserId, res?.data?.id)
    }
    
    
    // MockApi#requestLogin
    
    @Test fun requestLogin_isSuccessful () {
        assertSuccess(api.requestLogin(RequestLoginRequest("07880123456", "GB")))
    }
    @Test fun requestLogin_canFail () {
        assertFail(api.requestLogin(RequestLoginRequest("fail", "GB")))
    }
    
    
    // MockApi#checkLogin
    
    @Test fun checkLogin_isSuccessful () {
        assertSuccess(api.checkLogin(CheckLoginRequest(123456)))
    }
    @Test fun checkLogin_canFail () {
        assertFail(api.checkLogin(CheckLoginRequest(-1)))
    }
    
    
    // MockApi#updateFcm
    
    @Test fun updateFcm_isSuccessful () {
        assertSuccess(api.updateFcm(UpdateFcmRequest("new-fcm-token")))
    }
    
    
    // MockApi#listOrganisations
    
    @Test fun listOrganisations_isSuccessful () {
        assertSuccess(api.listOrganisations())
    }
    @Test fun listOrganisations_isList () {
        val res = api.listOrganisations().execute().body()
        assertEquals(5, res?.data?.size ?: 0)
    }
    
    
    // MockApi#showOrganisation
    
    @Test fun showOrganisation_isSuccessful () {
        assertSuccess(api.showOrganisation("1"))
    }
    @Test fun showOrganisation_isOrganisation () {
        assertData(api.showOrganisation("1"))
    }
    
    
    // MockApi#createOrganisation
    
    @Test fun createOrganisation_isSuccessful () {
        assertSuccess(api.createOrganisation(createOrgBody))
    }
    @Test fun createOrganisation_isOrganisation () {
        assertData(api.createOrganisation(createOrgBody))
    }
    @Test fun createOrganisation_setsFields () {
        val res = api.createOrganisation(createOrgBody).execute().body()
        assertNotNull(res?.data)
        assertEquals(res?.data?.name, "Test name")
        assertEquals(res?.data?.info, "Test info")
    }
    
    
    // MockApi#destroyOrganisation
    
    @Test fun destroyOrganisation_isSuccessful () {
        assertSuccess(api.destroyOrganisation("1"))
    }
    
    
    // MockApi#createMember
    
    @Test fun createMember_isSuccessful () {
        assertSuccess(api.createMember(
            "1",
            CreateMemberRequest(
                MemberRole.SUBSCRIBER,
                "07880123456",
                "GB"
            )
        ))
    }
    @Test fun createMember_isMember () {
        val call = api.createMember(
            "1",
            CreateMemberRequest(
                MemberRole.SUBSCRIBER,
                "07880123456",
                "GB"
            )
        )
        val res = call.execute().body()
        assertNotNull(res?.data)
    }
    
    
    // MockApi#destroyMember
    
    @Test fun destroyMember_isSuccessful () {
        assertSuccess(api.destroyMember("1", "2"))
    }
    
    
    // MockApi#acceptMember
    
    @Test fun acceptMember_isSuccessful () {
        assertSuccess(api.acceptMember("1"))
    }
    @Test fun acceptMember_isUserAuth() {
        assertData(api.acceptMember("1"))
    }
    
    
    // MockApi#createMessage
    
    @Test fun createMessage_isSuccessful () {
        assertSuccess(api.createMessage(createMsgBody))
    }
    @Test fun createMessage_isMessage () {
        assertData(api.createMessage(createMsgBody))
    }
    
    
    // MockApi#listMessageAttempts
    
    @Test fun listPendingMessages_isSuccessful () {
        assertSuccess(api.listPendingMessages())
    }
    @Test fun listPendingMessages_isList () {
        assertData(api.listPendingMessages())
    }
    @Test fun listPendingMessages_has3Messages () {
        val res = api.listPendingMessages().execute().body()
        assertEquals(3, res?.data?.size)
    }
    
    
    // MockApi#updateMessageAttempts
    
    @Test fun updateMessageAttempts_isSuccessful () {
        assertSuccess(api.updateMessageAttempts(updateAttemptsBody))
    }
}