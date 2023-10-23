package com.example.assignment3_277.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResponseDao {
    @Insert
    suspend fun addResponse(response: Response)

    @Query("SELECT * FROM responses ORDER BY sequenceNumber ASC")
    fun readAllData(): LiveData<List<Response>>
}