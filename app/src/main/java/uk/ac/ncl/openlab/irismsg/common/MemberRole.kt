package uk.ac.ncl.openlab.irismsg.common

enum class MemberRole {
    SUBSCRIBER,
    DONOR,
    COORDINATOR;
    
    val humanized: String get() = when (this) {
        SUBSCRIBER -> "Subscriber"
        DONOR -> "Donor"
        COORDINATOR -> "Coordinator"
    }
}