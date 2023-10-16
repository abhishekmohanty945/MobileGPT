package com.example.assignment3_277.data.api

import com.example.assignment3_277.constants.textCompletionsTurboEndpoint
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OpenAIApi {
    @POST(textCompletionsTurboEndpoint)
    @Streaming
    fun textCompletionsTurboWithStream(@Body body: JsonObject): Call<ResponseBody>
}