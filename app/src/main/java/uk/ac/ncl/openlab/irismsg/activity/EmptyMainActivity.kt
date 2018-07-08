package uk.ac.ncl.openlab.irismsg.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.ac.ncl.openlab.irismsg.api.ApiCallback
import uk.ac.ncl.openlab.irismsg.api.ApiResponse
import uk.ac.ncl.openlab.irismsg.api.IrisMsgService
import uk.ac.ncl.openlab.irismsg.api.JsonWebToken
import uk.ac.ncl.openlab.irismsg.common.ApiCall
import uk.ac.ncl.openlab.irismsg.model.UserEntity
import javax.inject.Inject

class EmptyMainActivity : AppCompatActivity(), HasSupportFragmentInjector {
    
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    
    @Inject
    lateinit var irisService : IrisMsgService
    
    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jwt = JsonWebToken.load(this)

        if (jwt == null) {
            pushOnboard()
        } else {
            irisService.getSelf().enqueue(ApiCallback({ res ->
                UserEntity.current = res?.data
                when (UserEntity.current) {
                    null -> pushOnboard()
                    else -> pushOrgList()
                }
            }, { t ->
                Toast.makeText(applicationContext, t.toString(), Toast.LENGTH_SHORT).show()
                pushOnboard()
            }))
        }
    }
    
    fun pushOnboard () {
        startActivity(Intent(this, OnboardActivity::class.java))
        finish()
    }
    
    fun pushOrgList () {
        startActivity(Intent(this, OrganisationListActivity::class.java))
        finish()
    }
}