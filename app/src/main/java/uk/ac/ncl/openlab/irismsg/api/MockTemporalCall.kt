package uk.ac.ncl.openlab.irismsg.api

import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uk.ac.ncl.openlab.irismsg.AppExecutors
import uk.ac.ncl.openlab.irismsg.AppExecutors_Factory
import uk.ac.ncl.openlab.irismsg.di.DaggerAppComponent
import uk.ac.ncl.openlab.irismsg.di.Injectable
import java.io.IOException
import java.lang.IllegalStateException
import javax.inject.Inject

const val NETWORK_DELAY = 500.toLong()

class MockTemporalCall <T> @Inject constructor(val value: T) : Call<T>, Injectable {
    
    private var executors = AppExecutors_Factory.newAppExecutors()!!
    
    private val response = Response.success(value)!!
    private var cancelled = false
    private var executed = false
    private var inProgress = false
    private var callbacks = mutableListOf<Callback<T>>()
    
    override fun execute() : Response<T> {
        return when {
            isCanceled -> throw IOException("canceled")
            executed -> throw IllegalStateException("Already executed")
            else -> {
                Thread.sleep(NETWORK_DELAY)
                executed = true
                executors.mainThread().execute {
                    callbacks.forEach { it.onResponse(this, response) }
                }
                response
            }
        }
    }
    
    override fun enqueue(callback : Callback<T>) {
        when {
            isCanceled -> callback.onFailure(this, IOException("canceled"))
            executed -> callback.onResponse(this, response)
            inProgress -> callbacks.add(callback)
            else -> {
                callbacks.add(callback)
                executors.networkIO().execute {
                    val res = execute()
                }
                inProgress = true
            }
        }
    }
    
    override fun isExecuted() : Boolean = executed
    override fun clone() : Call<T> = MockTemporalCall(value)
    override fun isCanceled() = cancelled
    override fun cancel() { this.cancelled = true }
    override fun request() : Request = Request.Builder().url("http://localhost").build()
}