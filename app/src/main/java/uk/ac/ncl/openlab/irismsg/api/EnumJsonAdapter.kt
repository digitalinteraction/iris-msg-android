package uk.ac.ncl.openlab.irismsg.api

import com.squareup.moshi.*

// Converts enums to json and vice versa, lower casing them for the api
class EnumJsonAdapter<T> (val builder: (String) -> T) : JsonAdapter<T>() {
    
    @FromJson
    override fun fromJson(reader : JsonReader) : T? {
        val str = reader.nextString().toUpperCase()
        return builder(str)
    }
    
    @ToJson
    override fun toJson(writer : JsonWriter, value : T?) {
        val str = value?.toString()?.toLowerCase()
        writer.value(str)
    }
}