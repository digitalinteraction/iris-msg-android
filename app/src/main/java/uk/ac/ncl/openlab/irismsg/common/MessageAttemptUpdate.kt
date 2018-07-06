package uk.ac.ncl.openlab.irismsg.common

data class MessageAttemptUpdate(
    val attemptId: String,
    val newState: MessageAttemptState
)