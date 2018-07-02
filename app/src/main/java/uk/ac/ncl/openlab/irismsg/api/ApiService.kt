package uk.ac.ncl.openlab.irismsg.api

enum class ApiMode {
    LIVE,
    MOCK
}

val currentApiMode = ApiMode.MOCK

class ApiService {
    
    companion object {
        fun apiMode(): ApiMode {
            return currentApiMode // TODO: Properly parameterise ...
        }
    
        fun makeClient(): ApiInterface {
            return if (apiMode() === ApiMode.LIVE) LiveApi() else MockApi()
        }
    }
    
}