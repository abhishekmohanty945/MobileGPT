package com.example.assignment3_277.di

import com.example.assignment3_277.data.IPromptRepository
import com.example.assignment3_277.data.IResponseRepository
import com.example.assignment3_277.data.PromptRepository
import com.example.assignment3_277.data.ResponseRepository
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

    @Binds
    abstract fun bindPromptRepository(
        homeRepositoryImpl: PromptRepository,
    ): IPromptRepository

    @Binds
    abstract fun bindResponseRepository(
        homeRepositoryImpl: ResponseRepository,
    ): IResponseRepository
}
