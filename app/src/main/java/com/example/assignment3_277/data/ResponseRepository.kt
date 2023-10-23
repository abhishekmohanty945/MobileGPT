package com.example.assignment3_277.data

import androidx.lifecycle.LiveData
import javax.inject.Inject

class ResponseRepository @Inject constructor(private val responseDao: ResponseDao) : IResponseRepository {
    override val readAllData: LiveData<List<Response>> = responseDao.readAllData()

    override suspend fun addResponse(response: Response){
        responseDao.addResponse(response)
    }
}