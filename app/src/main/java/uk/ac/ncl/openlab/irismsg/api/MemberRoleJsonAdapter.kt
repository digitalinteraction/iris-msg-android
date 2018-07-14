package uk.ac.ncl.openlab.irismsg.api

import com.squareup.moshi.*
import uk.ac.ncl.openlab.irismsg.common.MemberRole

class MemberRoleJsonAdapter : JsonAdapter<MemberRole>() {
    @FromJson
    override fun fromJson(reader : JsonReader) : MemberRole? {
        return MemberRole.valueOf(reader.nextString().toUpperCase())
    }
    
    @ToJson
    override fun toJson(writer : JsonWriter, value : MemberRole?) {
        writer.value(value?.toString()?.toLowerCase())
    }
}