package com.example.assignment3_277.data.remote

import android.util.Log
import com.example.assignment3_277.data.api.OpenAIApi
import com.example.assignment3_277.models.TextCompletionsParam
import com.example.assignment3_277.models.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject


class OpenAIRepositoryImpl @Inject constructor(
    private val openAIApi: OpenAIApi,
) : OpenAIRepository {
    override fun textCompletionsWithStream(params: TextCompletionsParam): Flow<String> =
        callbackFlow {
            withContext(Dispatchers.IO) {
                val response =  openAIApi.textCompletionsTurboWithStream(params.toJson()).execute()

                if (response.isSuccessful) {
                    val input = response.body()?.byteStream()?.bufferedReader() ?: throw Exception()
                    try {
                        while (true) {
                            val line = withContext(Dispatchers.IO) {
                                input.readLine()
                            } ?: continue
                            if (line == "data: [DONE]") {
                                close()
                            } else if (line.startsWith("data:")) {
                                try {
                                    // Handle & convert data -> emit to client
                                    val value =  lookupDataFromResponseTurbo(line)

                                    if (value.isNotEmpty()) {
                                        trySend(value)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Log.e("ChatGPT BUG", e.toString())
                                }
                            }
                        }
                    } catch (e: IOException) {
                        Log.e("ChatGPT BUG", e.toString())
                        throw Exception(e)
                    } finally {
                        withContext(Dispatchers.IO) {
                            input.close()
                        }
                        close()
                    }
                } else {
                    if (!response.isSuccessful) {
                        var jsonObject: JSONObject? = null
                        try {
                            jsonObject = JSONObject(response.errorBody()!!.string())
                            println(jsonObject)
                            trySend("Failure! Try again. $jsonObject")
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    trySend("Failure! Try again")
                    close()
                }
            }

            close()
        }

    private fun lookupDataFromResponseTurbo(jsonString: String): String {
        val regex = """"content"\s*:\s*"([^"]+)"""".toRegex()
        val matchResult = regex.find(jsonString)

        if (matchResult != null && matchResult.groupValues.size > 1) {
            val extractedText = matchResult.groupValues[1]
            return extractedText
                .replace("\\n\\n", "\n\n")
                .replace("\\n", "\n")
        }

        return " "
    }
}