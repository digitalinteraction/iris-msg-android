package uk.ac.ncl.openlab.irismsg.api

import uk.ac.ncl.openlab.irismsg.MemberRole
import uk.ac.ncl.openlab.irismsg.model.*
import java.util.*

enum class GenDateType {
    NOW,
    PAST,
    FUTURE,
    DISTANT_FUTURE,
    DISTANT_PAST
}

enum class GenUserType {
    CURRENT, VERIFIED, UNVERIFIED
}

class EntityGenerator {
    
    companion object {
        var nextEntityId = 1
        val currentUserId = "current-user-id"
    }
    
    fun makeId () : String {
        return (nextEntityId++).toString()
    }
    
    fun makeDate (type: GenDateType) : Date {
        val cal = Calendar.getInstance()
    
        when (type) {
            GenDateType.FUTURE -> cal.set(Calendar.DAY_OF_YEAR, 7)
            GenDateType.PAST -> cal.set(Calendar.DAY_OF_YEAR, -7)
            GenDateType.DISTANT_FUTURE-> cal.set(Calendar.DAY_OF_YEAR, 100)
            GenDateType.DISTANT_PAST-> cal.set(Calendar.DAY_OF_YEAR, -100)
        }
        
        return cal.time
    }
    
    fun makeUser (type: GenUserType) : UserEntity {
        return UserEntity(
                if (type === GenUserType.CURRENT) EntityGenerator.currentUserId else makeId(),
                makeDate(GenDateType.PAST),
                makeDate(GenDateType.PAST),
                "gb",
                "07880123456",
                if (type !== GenUserType.UNVERIFIED) makeDate(GenDateType.NOW) else null,
                if (type !== GenUserType.UNVERIFIED) "abcdef-123456" else null
        )
    }
    
    fun makeUserAuth () : UserAuthEntity {
        return UserAuthEntity(makeUser(), "some-really-long-jsonwebtoken")
    }
    
    fun makeOrganisation () : OrganisationEntity {
        val nameLetter = 65 + ((EntityGenerator.nextEntityId) % 24)
        return OrganisationEntity(
                makeId(),
                makeDate(GenDateType.PAST),
                makeDate(GenDateType.PAST),
                "Organisation $nameLetter",
                "Maecenas faucibus mollis interdum. Etiam porta sem malesuada magna mollis euismod.",
                listOf(
                        makeMember(MemberRole.COORDINATOR, GenUserType.CURRENT),
                        makeMember(MemberRole.DONOR, GenUserType.CURRENT),
                        makeMember(MemberRole.SUBSCRIBER, GenUserType.VERIFIED),
                        makeMember(MemberRole.SUBSCRIBER, GenUserType.VERIFIED)
                )
        )
    }
    
    fun makeMember (role: MemberRole, type: GenUserType) : MemberEntity {
        return MemberEntity(
                makeId(),
                makeDate(GenDateType.PAST),
                makeDate(GenDateType.PAST),
                role,
                if (type === GenUserType.CURRENT) EntityGenerator.currentUserId else makeId(),
                if (type !== GenUserType.UNVERIFIED) makeDate(GenDateType.PAST) else null,
                null
        )
    }
}