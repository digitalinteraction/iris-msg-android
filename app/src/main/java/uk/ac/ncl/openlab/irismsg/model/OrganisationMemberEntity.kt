package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import java.util.Date

data class OrganisationMemberEntity(
    @Json(name = "_id") override val id: String,
    @Json(name = "createdAt") override val createdAt: Date,
    @Json(name = "updatedAt") override val updatedAt: Date,
    @Json(name = "role") val role: MemberRole,
    @Json(name = "userId") val userId: String,
    @Json(name = "phoneNumber") val phoneNumber: String,
    @Json(name = "locale") val locale: String,
    @Json(name = "label") val label: String = ""
) : ApiEntity