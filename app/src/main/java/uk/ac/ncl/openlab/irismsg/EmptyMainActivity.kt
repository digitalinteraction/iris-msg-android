package uk.ac.ncl.openlab.irismsg

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import uk.ac.ncl.openlab.irismsg.api.JsonWebToken

class EmptyMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jwt = JsonWebToken.load(this)
        startActivity(when (jwt) {
            null -> Intent(this, LoginActivity::class.java)
            else -> Intent(this, OrganisationListActivity::class.java)
        })
        finish()
    }
}