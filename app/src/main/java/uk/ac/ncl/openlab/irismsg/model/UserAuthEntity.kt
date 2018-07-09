package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json

data class UserAuthEntity(
    @Json(name="user") val user: UserEntity,
    @Json(name="token") val token: String
)