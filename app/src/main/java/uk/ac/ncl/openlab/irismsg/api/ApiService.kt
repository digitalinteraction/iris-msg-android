package uk.ac.ncl.openlab.irismsg.api

enum class ApiMode {
    LIVE,
    MOCK
}

class ApiService {
    
    companion object {
        val currentApiMode = ApiMode.MOCK
        
        fun apiMode(): ApiMode {
            return currentApiMode // TODO: Properly parameterise ...
        }
    
        fun makeClient(): ApiInterface {
            return if (apiMode() === ApiMode.LIVE) LiveApi() else MockApi()
        }
    }
    
}