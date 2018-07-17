package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json

class MemberInviteEntity (
    @Json(name = "organisation") val organisation: OrganisationEntity,
    @Json(name = "member") val member: MemberEntity,
    @Json(name = "user") val user: UserEntity
)