package com.example.assignment3_277.data

import androidx.lifecycle.LiveData
import javax.inject.Inject

class PromptRepository @Inject constructor(private val promptDao: PromptDao) : IPromptRepository {
    override val readAllData: LiveData<List<Prompt>> = promptDao.readAllData()

    override suspend fun addPrompt(prompt: Prompt){
        promptDao.addPrompt(prompt)
    }
}