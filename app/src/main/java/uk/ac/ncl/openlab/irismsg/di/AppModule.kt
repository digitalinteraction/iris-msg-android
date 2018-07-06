package uk.ac.ncl.openlab.irismsg.di

import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import uk.ac.ncl.openlab.irismsg.api.*

@Module(includes = [
    ViewModelModule::class
])
class AppModule {
    
    @Singleton
    @Provides
    fun provideIrisApiService() : IrisMsgService {
        return MockIrisMsgService()
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
