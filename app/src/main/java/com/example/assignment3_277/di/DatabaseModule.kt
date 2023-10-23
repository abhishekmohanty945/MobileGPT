package com.example.assignment3_277.di

import android.content.Context
import com.example.assignment3_277.data.Db
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        Db.getDatabase(appContext)

    @Singleton
    @Provides
    fun providePromptDao(database: Db) = database.promptDao()
    @Singleton
    @Provides
    fun provideResponseDao(database: Db) = database.responseDao()
}