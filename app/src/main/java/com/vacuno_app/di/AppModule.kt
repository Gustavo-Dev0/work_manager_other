package com.vacuno_app.di;

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vacuno_app.data.remote.repository.LoginRepositoryImpl
import com.vacuno_app.data.remote.repository.UserRepositoryImpl
import com.vacuno_app.domain.repository.LoginRepository
import com.vacuno_app.domain.repository.UserRepository
import dagger.Module;
import dagger.Provides
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideLoginRepository(firebaseAuth: FirebaseAuth): LoginRepository {
        return LoginRepositoryImpl(firebaseAuth)
    }


    @Provides
    @Singleton
    @Named("userReference")
    fun provideUserReference(firebaseDatabase: FirebaseDatabase): DatabaseReference {
        return firebaseDatabase.getReference("users_registered")
    }

    @Provides
    @Singleton
    fun provideUserRepository(@Named("userReference") userReference: DatabaseReference): UserRepository {
        return UserRepositoryImpl(userReference)
    }

    @Provides
    @Singleton
    @Named("farmReference")
    fun provideFarmReference(firebaseDatabase: FirebaseDatabase): DatabaseReference {
        return firebaseDatabase.getReference("farms")
    }

    /*@Provides
    @Singleton
    fun provideFarmRepository(userReference: DatabaseReference): FarmRepository {
        return FarmRepositoryImpl(userReference)
    }*/

}
