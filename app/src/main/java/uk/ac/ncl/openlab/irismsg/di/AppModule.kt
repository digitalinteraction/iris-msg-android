package uk.ac.ncl.openlab.irismsg.di

import android.app.Application
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import uk.ac.ncl.openlab.irismsg.api.*
import uk.ac.ncl.openlab.irismsg.common.MemberRole
import uk.ac.ncl.openlab.irismsg.jwt.AppJwtService
import uk.ac.ncl.openlab.irismsg.jwt.JwtAuthorisationInterceptor
import uk.ac.ncl.openlab.irismsg.jwt.JwtService
import uk.ac.ncl.openlab.irismsg.jwt.MockJwtService
import java.util.*

class MemberRoleJsonAdapter : JsonAdapter<MemberRole>() {
    @FromJson override fun fromJson(reader : JsonReader) : MemberRole? {
        return MemberRole.valueOf(reader.nextString().toUpperCase())
    }
    
    @ToJson override fun toJson(writer : JsonWriter, value : MemberRole?) {
        writer.value(value?.toString()?.toLowerCase())
    }
}

enum class AppMode {
    LIVE, MOCK
}

//private val currentMode: AppMode = AppMode.MOCK
private val currentMode: AppMode = AppMode.LIVE

@Module(includes = [
    ViewModelModule::class
])
class AppModule {
    
    @Singleton
    @Provides
    fun provideMoshi () : Moshi {
        return Moshi.Builder()
                .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
                .add(MemberRole::class.java, MemberRoleJsonAdapter())
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

//    @Singleton
//    @Provides
//    fun provideGithubService(): GithubService {
//        return Retrofit.Builder()
//                .baseUrl("https://api.github.com/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(LiveDataCallAdapterFactory())
//                .build()
//                .create(GithubService::class.java)
//    }
    
//    @Singleton
//    @Provides
//    fun provideDb(app: Application): GithubDb {
//        return Room
//                .databaseBuilder(app, GithubDb::class.java, "github.db")
//                .fallbackToDestructiveMigration()
//                .build()
//    }
    
//    @Singleton
//    @Provides
//    fun provideUserDao(db: GithubDb): UserDao {
//        return db.userDao()
//    }
    
//    @Singleton
//    @Provides
//    fun provideRepoDao(db: GithubDb): RepoDao {
//        return db.repoDao()
//    }
}
