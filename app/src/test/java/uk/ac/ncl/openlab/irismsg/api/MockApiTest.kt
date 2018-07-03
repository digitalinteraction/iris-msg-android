package uk.ac.ncl.openlab.irismsg.api

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MockApiTest {
    
    lateinit var api: ApiInterface
    
    @Before fun setUp () {
        api = MockApi()
    }
    
    
    @Test fun getSelf_isSuccessful () {
        val res = api.getSelf().execute().body()
        assertTrue(res?.success ?: false)
    }
    @Test fun getSelf_isCurrentUser () {
        val res = api.getSelf().execute().body()
        assertEquals(EntityGenerator.currentUserId, res?.data?.id)
    }
    
    
    @Test fun requestLogin_isSuccessful () {
        val res = api.requestLogin("07880123456", "GB").execute().body()
        assertTrue(res?.success ?: false)
    }
    @Test fun requestLogin_canFail () {
        val res = api.requestLogin("fail", "GB").execute().body()
        assertFalse(res?.success ?: true )
    }
    
    
    @Test fun checkLogin_isSuccessful () {
        val res = api.checkLogin(123456).execute().body()
        assertTrue(res?.success ?: false)
    }
    @Test fun checkLogin_canFail () {
        val res = api.checkLogin(-1).execute().body()
        assertFalse(res?.success ?: true)
    }
    
    
    @Test fun updateFcm_isSuccessful () {
        val res = api.updateFcm("new-fcm-token").execute().body()
        assertTrue(res?.success ?: false)
    }
}