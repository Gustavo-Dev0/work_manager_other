package com.vacuno_app.di;

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.vacuno_app.data.remote.repository.*
import com.vacuno_app.domain.repository.*
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
        return firebaseDatabase.getReference("users")
    }

    @Provides
    @Singleton
    @Named("userRegisteredReference")
    fun provideUserRegisteredReference(firebaseDatabase: FirebaseDatabase): DatabaseReference {
        return firebaseDatabase.getReference("users_registered")
    }

    @Provides
    @Singleton
    fun provideUserRepository(@Named("userReference") userReference: DatabaseReference,
                              @Named("userRegisteredReference") userRegisteredReference: DatabaseReference): UserRepository {
        return UserRepositoryImpl(userReference, userRegisteredReference)
    }


    @Provides
    @Singleton
    @Named("farmReference")
    fun provideFarmReference(firebaseDatabase: FirebaseDatabase): DatabaseReference {
        return firebaseDatabase.getReference("farms")
    }

    @Provides
    @Singleton
    @Named("userFarmReference")
    fun provideUserFarmReference(firebaseDatabase: FirebaseDatabase): DatabaseReference {
        return firebaseDatabase.getReference("users_farms")
    }

    @Provides
    @Singleton
    fun provideFarmRepository(@Named("farmReference") farmReference: DatabaseReference,
                              @Named("userFarmReference") userFarmReference: DatabaseReference ): FarmRepository {
        return FarmRepositoryImpl(farmReference, userFarmReference)
    }

    @Provides
    @Singleton
    @Named("sheetReference")
    fun provideSheetReference(firebaseDatabase: FirebaseDatabase): DatabaseReference {
        return firebaseDatabase.getReference("sheets")
    }

    @Provides
    @Singleton
    fun provideSheetRepository(@Named("sheetReference") sheetReference: DatabaseReference): SheetRepository {
        return SheetRepositoryImpl(sheetReference)
    }

    @Provides
    @Singleton
    @Named("productionReference")
    fun provideProductionReference(firebaseDatabase: FirebaseDatabase): DatabaseReference {
        return firebaseDatabase.getReference("productions")
    }

    @Provides
    @Singleton
    fun provideProductionRepository(@Named("productionReference") productionReference: DatabaseReference): ProductionRepository {
        return ProductionRepositoryImpl(productionReference)
    }

    @Provides
    @Singleton
    @Named("alarmReference")
    fun provideAlarmReference(firebaseDatabase: FirebaseDatabase): DatabaseReference {
        return firebaseDatabase.getReference("alarms")
    }

    @Provides
    @Singleton
    fun provideAlarmRepository(@Named("alarmReference") alarmReference: DatabaseReference): AlarmRepository {
        return AlarmRepositoryImpl(alarmReference)
    }


}
