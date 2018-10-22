package uk.ac.ncl.openlab.irismsg.model

import android.support.annotation.Nullable
import com.squareup.moshi.Json
import java.util.Date

data class UserEntity(
    @Json(name = "_id") override val id: String,
    @Json(name = "createdAt") override val createdAt: Date,
    @Json(name = "updatedAt") override val updatedAt: Date,
    @Json(name = "phoneNumber") val phoneNumber: String,
    @Json(name = "locale") @Nullable val locale: String,
    @Json(name = "verifiedOn") @Nullable val verifiedOn: Date?,
    @Json(name = "fcmToken") @Nullable val fcmToken: String?
) : ApiEntity {
    
    companion object {
        var current : UserEntity? = null
    }
}
