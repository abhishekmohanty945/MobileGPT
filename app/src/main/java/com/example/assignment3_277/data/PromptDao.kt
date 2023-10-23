package com.example.assignment3_277.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PromptDao {
    @Insert
    suspend fun addPrompt(prompt: Prompt)

    @Query("SELECT * FROM audit_prompt ORDER BY sequenceNumber ASC")
    fun readAllData(): LiveData<List<Prompt>>
}