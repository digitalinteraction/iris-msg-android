package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json
import java.util.Date

data class OrganisationEntity(
    @Json(name = "_id") override val id: String,
    @Json(name = "createdAt") override val createdAt: Date,
    @Json(name = "updatedAt") override val updatedAt: Date,
    @Json(name = "name") var name: String,
    @Json(name = "info") var info: String,
    @Json(name = "members") val members: List<MemberEntity>
) : ApiEntity