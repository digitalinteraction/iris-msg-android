package uk.ac.ncl.openlab.irismsg.model

import com.squareup.moshi.Json

data class AppVersion(
        @Json(name = "version") val version: String,
        @Json(name = "url") val url: String
)