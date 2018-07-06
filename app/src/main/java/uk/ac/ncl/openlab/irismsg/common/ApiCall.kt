package uk.ac.ncl.openlab.irismsg.common

import retrofit2.Call
import uk.ac.ncl.openlab.irismsg.api.ApiResponse

typealias ApiCall<T> = Call<ApiResponse<T>>