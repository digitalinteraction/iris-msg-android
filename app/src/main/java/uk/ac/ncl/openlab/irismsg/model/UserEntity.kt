package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json
import java.util.Date

data class UserEntity(
    @Json(name = "_id") override val id: String,
    @Json(name = "createdAt") override val createdAt: Date,
    @Json(name = "updatedAt") override val updatedAt: Date,
    @Json(name = "countryCode") val countryCode: String,
    @Json(name = "phoneNumber") val phoneNumber: String,
    @Json(name = "verifiedOn") val verifiedOn: Date?,
    @Json(name = "fcmToken") val fcmToken: String?
) : ApiEntity {
    
    companion object {
        var current : UserEntity? = null
    }
}
