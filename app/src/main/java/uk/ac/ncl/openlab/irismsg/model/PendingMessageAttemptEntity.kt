package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json
import java.util.*

data class PendingMessageAttemptEntity(
    @Json(name = "_id") override val id: String,
    @Json(name = "createdAt") override val createdAt: Date,
    @Json(name = "updatedAt") override val updatedAt: Date,
    @Json(name = "recipient") val recipientId: String,
    @Json(name = "phoneNumber") val phoneNumber: String
) : ApiEntity