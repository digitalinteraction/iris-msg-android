package uk.ac.ncl.openlab.irismsg.api

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import uk.ac.ncl.openlab.irismsg.MemberRole

class MockApiTest {
    
    lateinit var api: ApiInterface
    
    @Before fun setUp () {
        api = MockApi()
    }
    
    fun <T> assertSuccess (call: ApiCall<T>) {
        var body = call.execute().body()
        assertTrue(body?.success ?: false)
    }
    
    fun <T> assertFail (call: ApiCall<T>) {
        var body = call.execute().body()
        assertFalse(body?.success ?: true)
    }
    
    fun <T> assertData(call: ApiCall<T>) {
        var body = call.execute().body()
        assertNotNull(body?.data?: null)
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
        assertSuccess(api.requestLogin("07880123456", "GB"))
    }
    @Test fun requestLogin_canFail () {
        assertFail(api.requestLogin("fail", "GB"))
    }
    
    
    // MockApi#checkLogin
    
    @Test fun checkLogin_isSuccessful () {
        assertSuccess(api.checkLogin(123456))
    }
    @Test fun checkLogin_canFail () {
        assertFail(api.checkLogin(-1))
    }
    
    
    // MockApi#updateFcm
    
    @Test fun updateFcm_isSuccessful () {
        assertSuccess(api.updateFcm("new-fcm-token"))
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
        assertSuccess(api.createOrganisation("Test name", "Test info"))
    }
    @Test fun createOrganisation_isOrganisation () {
        assertData(api.createOrganisation("Test name", "Test info"))
    }
    @Test fun createOrganisation_setsFields () {
        val res = api.createOrganisation("Test name", "Test info").execute().body()
        assertNotNull(res?.data ?: null)
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
            MemberRole.SUBSCRIBER,
            "07880123456",
            "GB"
        ))
    }
    @Test fun createMember_isMember () {
        val call = api.createMember(
            "1",
            MemberRole.SUBSCRIBER,
            "07880123456",
            "GB"
        )
        val res = call.execute().body()
        assertNotNull(res?.data?: null)
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
        assertSuccess(api.createMessage("Hello, World!", "1"))
    }
    @Test fun createMessage_isMessage () {
        assertData(api.createMessage("Hey", "1"))
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
    
    
    // MokcApi#updateMessageAttempts
    
    @Test fun updateMessageAttempts_isSuccessful () {
        assertSuccess(api.updateMessageAttempts(listOf()))
    }
}