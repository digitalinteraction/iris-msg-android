package uk.ac.ncl.openlab.irismsg.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton
import uk.ac.ncl.openlab.irismsg.api.*
import java.util.*

@Module(includes = [
    ViewModelModule::class
])
class AppModule {
    
    @Singleton
    @Provides
    fun provideMoshi () : Moshi {
        return Moshi.Builder()
                .add(Date::class.java, Rfc3339DateJsonAdapter())
                .build()
    }
    
    @Singleton
    @Provides
    fun provideRetrofit () : Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://api.dev.irismsg.io")
                .addConverterFactory(MoshiConverterFactory.create(provideMoshi()))
                .build()
    }
    
    @Singleton
    @Provides
    fun provideIrisApiService() : IrisMsgService {
        return provideRetrofit().create(IrisMsgService::class.java)
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
