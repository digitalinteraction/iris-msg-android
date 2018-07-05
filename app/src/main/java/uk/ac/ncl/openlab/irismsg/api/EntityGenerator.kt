package uk.ac.ncl.openlab.irismsg.api

import uk.ac.ncl.openlab.irismsg.MemberRole
import uk.ac.ncl.openlab.irismsg.MessageAttemptState
import uk.ac.ncl.openlab.irismsg.model.*
import java.util.*

enum class DateGen {
    NOW,
    PAST,
    FUTURE,
    DISTANT_FUTURE,
    DISTANT_PAST
}

enum class UserGen {
    CURRENT, VERIFIED, UNVERIFIED
}

enum class OrganisationGen {
    COORDINATOR, DONOR
}

class EntityGenerator {
    
    companion object {
        private const val currentUserId = "current-user-id"
    }
    
    var nextEntityId = 1
    
    fun makeId () : String {
        return (nextEntityId++).toString()
    }
    
    fun makeDate (type: DateGen) : Date {
        val cal = Calendar.getInstance()
    
        when (type) {
            DateGen.FUTURE -> cal.set(Calendar.DAY_OF_YEAR, 7)
            DateGen.PAST -> cal.set(Calendar.DAY_OF_YEAR, -7)
            DateGen.DISTANT_FUTURE-> cal.set(Calendar.DAY_OF_YEAR, 100)
            DateGen.DISTANT_PAST-> cal.set(Calendar.DAY_OF_YEAR, -100)
            else -> {}
        }
        
        return cal.time
    }
    
    fun makeUser (type: UserGen) : UserEntity {
        return UserEntity(
            if (type === UserGen.CURRENT) currentUserId else makeId(),
            makeDate(DateGen.PAST),
            makeDate(DateGen.PAST),
            "gb",
            "07880123456",
            if (type !== UserGen.UNVERIFIED) makeDate(DateGen.NOW) else null,
            if (type !== UserGen.UNVERIFIED) "abcdef-123456" else null
        )
    }
    
    fun makeUserAuth () : UserAuthEntity {
        return UserAuthEntity(
            makeUser(UserGen.CURRENT),
            "some-really-long-jsonwebtoken"
        )
    }
    
    fun makeOrganisation (type: OrganisationGen) : OrganisationEntity {
        val nameLetter = (65 + ((nextEntityId - 1) % 24)).toChar()
        return OrganisationEntity(
            makeId(),
            makeDate(DateGen.PAST),
            makeDate(DateGen.PAST),
            "Organisation $nameLetter",
            "Maecenas faucibus mollis interdum. Etiam porta sem malesuada magna mollis euismod.",
            listOfNotNull(
                if (type == OrganisationGen.COORDINATOR) {
                    makeMember(MemberRole.COORDINATOR, UserGen.CURRENT)
                } else null,
                makeMember(MemberRole.DONOR, UserGen.CURRENT),
                makeMember(MemberRole.SUBSCRIBER, UserGen.VERIFIED),
                makeMember(MemberRole.SUBSCRIBER, UserGen.VERIFIED)
            )
        )
    }
    
    fun makeMember (role: MemberRole, type: UserGen) : MemberEntity {
        return MemberEntity(
            id = makeId(),
            createdAt = makeDate(DateGen.PAST),
            updatedAt = makeDate(DateGen.PAST),
            role = role,
            userId = if (type === UserGen.CURRENT) EntityGenerator.currentUserId else makeId(),
            confirmedOn = if (type !== UserGen.UNVERIFIED) makeDate(DateGen.PAST) else null,
            deletedOn = null
        )
    }
    
    fun makeMessage (organisationId: String) : MessageEntity {
        return MessageEntity(
            id = makeId(),
            createdAt = makeDate(DateGen.PAST),
            updatedAt = makeDate(DateGen.PAST),
            content = "Hello, World!",
            organisationId = organisationId,
            authorId = currentUserId,
            attempts = listOf()
        )
    }
    
    fun makePendingMessage (organisationId: String, content: String) : PendingMessageEntity {
        return PendingMessageEntity(
            id = makeId(),
            createdAt = makeDate(DateGen.PAST),
            updatedAt = makeDate(DateGen.PAST),
            content = content,
            organisationId = organisationId,
            authorId = currentUserId,
            attempts = listOf(
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt()
            )
        )
    }
    
    fun makePendingMessageAttempt () : PendingMessageAttemptEntity {
        return PendingMessageAttemptEntity(
            id = makeId(),
            createdAt = makeDate(DateGen.PAST),
            updatedAt = makeDate(DateGen.PAST),
            recipientId = makeId(),
            phoneNumber = "+447880123456"
        )
    }
}