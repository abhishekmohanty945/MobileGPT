package com.example.assignment3_277.di

import com.example.assignment3_277.data.remote.OpenAIRepository
import com.example.assignment3_277.data.remote.OpenAIRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun openAIRepository(
        repo: OpenAIRepositoryImpl
    ): OpenAIRepository
}
