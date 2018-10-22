package uk.ac.ncl.openlab.irismsg.api

import uk.ac.ncl.openlab.irismsg.common.MemberRole
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

/**
 * A util for generating entities for testing, uses an incremental id so every object is unique
 */
class EntityGenerator {
    
    companion object {
        const val currentUserId = "current-user-id"
        const val fakeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c3IiOiJjdXJyZW50LXVzZXItaWQiLCJpYXQiOjE1MzA3MTM1NzZ9.swsQwUsEVghOiABq2Dokm3iM3aIaDQ9X4jd_B5RRH8g"
    }
    
    private var nextEntityId = 1
    
    fun makeId () : String {
        return (nextEntityId++).toString()
    }
    
    /** Make a date relative to today */
    private fun makeDate (type: DateGen) : Date {
        val cal = Calendar.getInstance()
        
        var size: Int? = null
    
        when (type) {
            DateGen.FUTURE -> size = 7
            DateGen.PAST -> size = -7
            DateGen.DISTANT_FUTURE-> size = 100
            DateGen.DISTANT_PAST-> size = -100
            else -> {}
        }
        
        if (size != null) cal.set(
            Calendar.DAY_OF_YEAR,
            cal.get(Calendar.DAY_OF_YEAR) + size
        )
        
        return cal.time
    }
    
    fun makeUser (type: UserGen) : UserEntity {
        return UserEntity(
            id = if (type === UserGen.CURRENT) currentUserId else makeId(),
            createdAt = makeDate(DateGen.PAST),
            updatedAt = makeDate(DateGen.PAST),
            phoneNumber = "07880123456",
            verifiedOn = if (type !== UserGen.UNVERIFIED) makeDate(DateGen.NOW) else null,
            fcmToken = if (type !== UserGen.UNVERIFIED) "abcdef-123456" else null
        )
    }
    
    fun makeUserAuth () : UserAuthEntity {
        return UserAuthEntity(
            user = makeUser(UserGen.CURRENT),
            token = fakeJwt
        )
    }
    
    fun makeOrganisation (type: OrganisationGen = OrganisationGen.COORDINATOR) : OrganisationEntity {
        val nameLetter = (65 + ((nextEntityId - 1) % 24)).toChar()
        
        val owningMember = when (type) {
            OrganisationGen.COORDINATOR -> UserGen.CURRENT
            else -> UserGen.VERIFIED
        }
        
        return OrganisationEntity(
            id = makeId(),
            createdAt = makeDate(DateGen.PAST),
            updatedAt = makeDate(DateGen.PAST),
            name = "Organisation $nameLetter",
            info = "Maecenas faucibus mollis interdum. Etiam porta sem malesuada magna mollis euismod.",
            members = listOfNotNull(
                makeMember(MemberRole.COORDINATOR, owningMember),
                makeMember(MemberRole.DONOR, UserGen.CURRENT),
                makeMember(MemberRole.SUBSCRIBER, UserGen.VERIFIED),
                makeMember(MemberRole.SUBSCRIBER, UserGen.VERIFIED)
            ).toMutableList()
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
    
    fun makePendingMessage (content: String) : PendingMessageEntity {
        val range: Int = 5 + Random().nextInt(5)
        
        return PendingMessageEntity(
            id = makeId(),
            createdAt = makeDate(DateGen.PAST),
            updatedAt = makeDate(DateGen.PAST),
            content = content,
            organisation = makeOrganisation(OrganisationGen.DONOR),
            authorId = currentUserId,
            attempts = listOf(
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt(),
                makePendingMessageAttempt()
            ).slice(IntRange(5, range))
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
    
    fun makeMemberInvite () : MemberInviteEntity {
        val org = makeOrganisation(OrganisationGen.DONOR)
        return MemberInviteEntity(
            organisation = org,
            member = org.members.find { it.role === MemberRole.DONOR }!!,
            user = makeUser(UserGen.CURRENT)
        )
    }
    
    fun makeOrganisationMember (role: MemberRole, type: UserGen) : OrganisationMemberEntity {
        return OrganisationMemberEntity(
            id = makeId(),
            createdAt = makeDate(DateGen.PAST),
            updatedAt = makeDate(DateGen.PAST),
            role = role,
            userId = if (type === UserGen.CURRENT) EntityGenerator.currentUserId else makeId(),
            phoneNumber = "07880123456",
            locale = "en"
        )
    }
}