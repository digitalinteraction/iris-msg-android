package uk.ac.ncl.openlab.irismsg.model

import android.support.annotation.Nullable
import com.squareup.moshi.Json
import uk.ac.ncl.openlab.irismsg.common.MessageAttemptState
import java.util.*

data class MessageAttemptEntity(
    @Json(name = "_id") override val id: String,
    @Json(name = "createdAt") override val createdAt: Date,
    @Json(name = "updatedAt") override val updatedAt: Date,
    @Json(name = "donor") @Nullable val donorId: String?,
    @Json(name = "recipient") val recipientId: String,
    @Json(name = "state") val state: MessageAttemptState = MessageAttemptState.PENDING,
    @Json(name = "previous") @Nullable val previousId: String? = null
) : ApiEntity