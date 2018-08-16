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
    @Json(name = "members") val members: MutableList<MemberEntity>
) : ApiEntity {
    
    val shortInfo: String
        get () = info.substring(0, Math.min(info.length, shortInfoLength)) +
                if (info.length > shortInfoLength) "..." else ""
    
    fun isMember (userId: String, role: MemberRole): Boolean = members.any { member ->
        member.role == role
                && member.userId == userId
                && member.isActive()
    }
    
    fun membershipFor (userId: String) = members.filter { member ->
        member.userId == userId
                && member.isActive()
    }
    
    fun primaryMembership (userId: String) : MemberEntity? {
        val roles = mutableMapOf<MemberRole, MemberEntity>()
        members.forEach { member ->
            if (member.userId == userId && member.isActive()) {
                roles[member.role] = member
            }
        }
        
        val roleOrder = listOf(
            MemberRole.COORDINATOR, MemberRole.DONOR, MemberRole.SUBSCRIBER
        )
        
        for (role in roleOrder) {
            if (roles[role] != null) return roles[role]
        }
        
        return null
    }
}