package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json
import java.util.Date

//data class CommentEntity(@Json(name = "postId") val postId: String,
//                         @Json(name = "id") val id: String,
//                         @Json(name = "name") val name: String,
//                         @Json(name = "email") val email: String,
//                         @Json(name = "body") val body: String)

data class UserEntity(
        @Json(name = "_id") override val id: String,
        @Json(name = "createdAt") override val createdAt: Date,
        @Json(name = "updatedAt") override val updatedAt: Date,
        @Json(name = "countryCode") val countryCode: String,
        @Json(name = "phoneNumber") val phoneNumber: String,
        @Json(name = "verifiedOn") val verifiedOn: Date?,
        @Json(name = "fcmToken") val fcmToken: String?
) : ApiEntity
