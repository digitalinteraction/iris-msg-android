package uk.ac.ncl.openlab.irismsg.api

import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.common.MessageAttemptState


class MessageAttemptUpdate(
    val attemptId: String,
    val newState: MessageAttemptState
)



data class RequestLoginRequest(
    val phoneNumber: String,
    val countryCode: String
)

data class CheckLoginRequest(
    val code: Int
)

data class UpdateFcmRequest(
    val newToken: String
)

data class CreateOrganisationRequest(
    val name: String,
    val info: String
)

data class CreateMemberRequest(
    val role: MemberRole,
    val phoneNumber: String,
    val countryCode: String
)

data class CreateMessageRequest(
    val content: String,
    val orgId: String
)

data class UpdateMessageAttemptsRequest(
    val updates: List<MessageAttemptUpdate>
)