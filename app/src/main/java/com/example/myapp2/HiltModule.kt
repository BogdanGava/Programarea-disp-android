package com.example.myapp2

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltModule {

    @Provides
    @Singleton
    fun context() = ExampleApplication.appContext

    @Provides
    @Singleton
    fun db(context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "database-name"
    ).build()
}