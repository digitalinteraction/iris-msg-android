package uk.ac.ncl.openlab.irismsg.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import uk.ac.ncl.openlab.irismsg.R

class EditOrganisationActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_organisation)
    }
    
    companion object {
        const val REQUEST_ORG_ID = 1
    }
}
