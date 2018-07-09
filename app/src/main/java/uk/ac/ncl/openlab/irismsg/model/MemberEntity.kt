package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import java.util.Date

data class MemberEntity(
    @Json(name = "_id") override val id: String,
    @Json(name = "createdAt") override val createdAt: Date,
    @Json(name = "updatedAt") override val updatedAt: Date,
    @Json(name = "role") val role: MemberRole,
    @Json(name = "user") val userId: String,
    @Json(name = "confirmedOn") val confirmedOn: Date?,
    @Json(name = "deletedOn") val deletedOn: Date?
) : ApiEntity