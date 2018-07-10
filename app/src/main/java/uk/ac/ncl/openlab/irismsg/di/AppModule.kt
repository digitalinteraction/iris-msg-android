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
import uk.ac.ncl.openlab.irismsg.common.ViewsUtil
import java.util.*

class MemberRoleJsonAdapter : JsonAdapter<MemberRole>() {
    @FromJson override fun fromJson(reader : JsonReader) : MemberRole? {
        return MemberRole.valueOf(reader.nextString().toUpperCase())
    }
    
    @ToJson override fun toJson(writer : JsonWriter, value : MemberRole?) {
        writer.value(value?.toString()?.toLowerCase())
    }
}

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
    fun provideRetrofit (moshi: Moshi, application: Application) : Retrofit {
        val httpClient = OkHttpClient.Builder()
                .addInterceptor(JwtAuthorisationInterceptor(application))
                .build()
        
        return Retrofit.Builder()
                .baseUrl("https://api.dev.irismsg.io")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(httpClient)
                .build()
    }
    
    @Singleton
    @Provides
    fun provideIrisApiService(retrofit: Retrofit) : IrisMsgService {
        return retrofit.create(IrisMsgService::class.java)
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
