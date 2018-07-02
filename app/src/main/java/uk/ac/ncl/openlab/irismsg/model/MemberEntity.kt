package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json
import uk.ac.ncl.openlab.irismsg.MemberRole
import java.util.Date

data class MemberEntity(
        @Json(name = "_id") val id: String,
        @Json(name = "createdAt") val createdAt: Date,
        @Json(name = "updatedAt") val updatedAt: Date,
        @Json(name = "role") val role: MemberRole,
        @Json(name = "user") val userId: String,
        @Json(name = "confirmedOn") val confirmedOn: Date?,
        @Json(name = "deletedOn") val deletedOn: Date?
)