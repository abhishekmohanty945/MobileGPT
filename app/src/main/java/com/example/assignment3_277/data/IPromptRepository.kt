package com.example.assignment3_277.data

import androidx.lifecycle.LiveData

interface IPromptRepository {
    val readAllData: LiveData<List<Prompt>>
    suspend fun addPrompt(prompt: Prompt)
}