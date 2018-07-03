package uk.ac.ncl.openlab.irismsg.model

import java.util.*

interface ApiEntity {
    val id: String
    val createdAt: Date
    val updatedAt: Date
}