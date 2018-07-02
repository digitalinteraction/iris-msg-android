package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json
import java.util.Date

data class OrganisationEntity(
        @Json(name = "_id") val id: String,
        @Json(name = "createdAt") val createdAt: Date,
        @Json(name = "updatedAt") val updatedAt: Date,
        @Json(name = "name") val name: String,
        @Json(name = "info") val info: String,
        @Json(name = "members") val members: List<MemberEntity>
)