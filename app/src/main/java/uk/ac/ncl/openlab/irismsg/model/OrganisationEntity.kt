package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import java.util.Date

private const val shortInfoLength = 120

data class OrganisationEntity(
    @Json(name = "_id") override val id: String,
    @Json(name = "createdAt") override val createdAt: Date,
    @Json(name = "updatedAt") override val updatedAt: Date,
    @Json(name = "name") var name: String,
    @Json(name = "info") var info: String,
    @Json(name = "members") val members: List<MemberEntity>
) : ApiEntity {
    
    val shortInfo: String
        get () = info.substring(0, Math.min(info.length, shortInfoLength)) +
                if (info.length > shortInfoLength) "..." else ""
    
    fun isCoordinator (userId: String) : Boolean = members.any { member ->
        member.role == MemberRole.COORDINATOR
                && member.userId == userId
                && member.isActive()
    }
}