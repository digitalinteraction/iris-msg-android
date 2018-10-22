package uk.ac.ncl.openlab.irismsg.di

import android.app.Application
import com.squareup.moshi.*
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import uk.ac.ncl.openlab.irismsg.api.*
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.common.MessageAttemptState
import uk.ac.ncl.openlab.irismsg.jwt.AppJwtService
import uk.ac.ncl.openlab.irismsg.jwt.JwtAuthorisationInterceptor
import uk.ac.ncl.openlab.irismsg.jwt.JwtService
import uk.ac.ncl.openlab.irismsg.jwt.MockJwtService
import java.util.*
import javax.inject.Singleton

enum class AppMode {
    LIVE, MOCK
}

private val currentMode: AppMode = AppMode.MOCK
//private val currentMode: AppMode = AppMode.LIVE

/**
 * A Dagger Module to provide custom types
 */
@Module(includes = [
    ViewModelModule::class
])
class AppModule {
    
    @Singleton
    @Provides
    fun provideMoshi () : Moshi {
        return Moshi.Builder()
                .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
                .add(MemberRole::class.java, EnumJsonAdapter {MemberRole.valueOf(it) })
                .add(MessageAttemptState::class.java, EnumJsonAdapter { MessageAttemptState.valueOf(it) })
                .add(KotlinJsonAdapterFactory())
                .build()
    }
    
    @Singleton
    @Provides
    fun provideRetrofit (moshi: Moshi, jwtService: JwtService) : Retrofit {
        val httpClient = OkHttpClient.Builder()
                .addInterceptor(JwtAuthorisationInterceptor(jwtService))
                .build()
        
        return Retrofit.Builder()
                .baseUrl("https://api.dev.irismsg.io")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(httpClient)
                .build()
    }
    
    @Singleton
    @Provides
    fun provideIrisApiService (retrofit: Retrofit) : IrisMsgService {
        return when (currentMode) {
            AppMode.LIVE -> retrofit.create(IrisMsgService::class.java)
            AppMode.MOCK -> MockIrisMsgService()
        }
    }
    
    @Singleton
    @Provides
    fun provideJwtService (app: Application) : JwtService {
        return when (currentMode) {
            AppMode.LIVE -> AppJwtService(app)
            AppMode.MOCK -> MockJwtService(EntityGenerator.fakeJwt)
        }
    }
}
