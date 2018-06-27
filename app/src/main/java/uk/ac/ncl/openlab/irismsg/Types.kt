package uk.ac.ncl.openlab.irismsg

enum class MemberRole {
    SUBSCRIBER,
    DONOR,
    COORDINATOR
}

enum class MessageAttemptState {
    PENDING,
    REJECTEd,
    FAILED,
    SUCCESS,
    NO_SERVICE,
    NO_SMS_DATA,
    RADIO_OFF,
    TWILIO,
    NO_RESPONSE
}

enum class FcmType {
    NEW_DONATIONS
}