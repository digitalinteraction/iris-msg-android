package uk.ac.ncl.openlab.irismsg.jwt

import com.auth0.android.jwt.JWT

/**
 * A fake JwtService which mocks the current jwt and pretends to save
 */
class MockJwtService (fakeToken: String) : JwtService {
    
    override var current: JWT? = JWT(fakeToken)
    
    override fun save(rawToken : String?) : JWT? {
        current = if (rawToken != null) JWT(rawToken) else null
        return current
    }
}