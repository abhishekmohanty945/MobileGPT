package com.example.assignment3_277.data

import androidx.lifecycle.LiveData

interface IResponseRepository {
    val readAllData: LiveData<List<Response>>

    suspend fun addResponse(response: Response)

}