package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json
import java.util.*

data class MessageEntity(
        @Json(name = "_id") override val id: String,
        @Json(name = "createdAt") override val createdAt: Date,
        @Json(name = "updatedAt") override val updatedAt: Date,
        @Json(name = "content") val content: String,
        @Json(name = "organisation") val organisationId: String,
        @Json(name = "author") val authorId: String,
        @Json(name = "attempts") val attempts: List<MessageAttemptEntity>
) : ApiEntity