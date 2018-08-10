package uk.ac.ncl.openlab.irismsg.common

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import javax.inject.Inject

class PermissionsManager @Inject constructor() {
    
    val defaultPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.SEND_SMS
    )
    
    fun request (
        ctx: Activity, permissions: Array<String>, requestCode: Int, then: () -> Unit) {
        
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(ctx, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        
        if (missingPermissions.isNotEmpty()) {
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ctx.requestPermissions(missingPermissions, requestCode)
            } else {
                TODO("Handle really old permissions ...")
            }
            
        } else {
            then()
        }
    }
    
    fun checkResult (required: Array<String>, permissions: Array<out String>, grantResults: IntArray) : Boolean {
        return grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    }
}