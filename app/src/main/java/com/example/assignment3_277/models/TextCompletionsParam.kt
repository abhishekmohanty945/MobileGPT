package com.example.assignment3_277.models

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class TextCompletionsParam(
    @SerializedName("n")
    val n: Int = 1,
    @SerializedName("stream")
    var stream: Boolean = true,
    @SerializedName("maxTokens")
    val maxTokens: Int = 2048,
    @SerializedName("model")
    val model: GPTModel = GPTModel.gpt35Turbo,
    @SerializedName("messages")
    val messagesTurbo: List<MessageTurbo> = emptyList(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextCompletionsParam

        if (n != other.n) return false
        if (stream != other.stream) return false
        if (maxTokens != other.maxTokens) return false
        if (model != other.model) return false
        if (messagesTurbo != other.messagesTurbo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = n.hashCode()
        result = 31 * result + stream.hashCode()
        result = 31 * result + maxTokens
        result = 31 * result + model.hashCode()
        result = 31 * result + messagesTurbo.hashCode()
        return result
    }
}

fun TextCompletionsParam.toJson(): JsonObject {
    val json = JsonObject()
    json.addProperty("stream", stream)
    json.addProperty("model", model.model)
    val jsonArray = JsonArray()
    for (message in messagesTurbo) jsonArray.add(message.toJson())
    json.add("messages", jsonArray)
    return json
}