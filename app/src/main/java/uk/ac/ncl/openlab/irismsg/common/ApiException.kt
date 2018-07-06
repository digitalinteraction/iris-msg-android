package uk.ac.ncl.openlab.irismsg.common

class ApiException(val messages: List<String>): Exception(messages.joinToString())