package com.example.assignment3_277.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.example.assignment3_277.data.Prompt
import com.example.assignment3_277.data.PromptRepository
import com.example.assignment3_277.data.Response
import com.example.assignment3_277.data.ResponseRepository
import com.example.assignment3_277.data.remote.OpenAIRepositoryImpl
import com.example.assignment3_277.models.ConversationModel
import com.example.assignment3_277.models.MessageModel
import com.example.assignment3_277.models.MessageTurbo
import com.example.assignment3_277.models.TextCompletionsParam
import com.example.assignment3_277.models.TurboRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GPTViewModel @Inject constructor(
    private val openAIRepo: OpenAIRepositoryImpl,
    private val promptRepo: PromptRepository,
    private val responseRepo: ResponseRepository
) : ViewModel() {

    private var prompt = ""
    var _response: MutableStateFlow<String> = MutableStateFlow("")
    var response: StateFlow<String> = _response.asStateFlow()


    private val _currentConversation: MutableStateFlow<String> =
        MutableStateFlow(Date().time.toString())

    private val _conversations: MutableStateFlow<MutableList<ConversationModel>> = MutableStateFlow(
        mutableListOf()
    )

    private val _messages: MutableStateFlow<HashMap<String, MutableList<MessageModel>>> =
        MutableStateFlow(HashMap())

    val currentConversationState: StateFlow<String> = _currentConversation.asStateFlow()
    val messagesState: StateFlow<java.util.HashMap<String, MutableList<MessageModel>>> =
        _messages.asStateFlow()

    private fun getMessagesByConversation(conversationId: String): MutableList<MessageModel> {
        if (_messages.value[conversationId] == null) return mutableListOf()

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.clone() as HashMap<String, MutableList<MessageModel>>

        return messagesMap[conversationId]!!
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun sendMessage(message: String) {

        val newMessageModel = MessageModel(
            question = message,
            answer = "Let me think...",
            conversationId = _currentConversation.value,
        )

        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        // Insert message to list
        currentListMessage.add(0, newMessageModel)
        setMessages(currentListMessage)

//        System.out.println(_currentConversation.value)
//        promptRepo.addPrompt(Prompt(datetime = LocalDateTime.now().toString(), prompt = message))
        prompt = message

        // Execute API OpenAI
        val flow: Flow<String> = openAIRepo.textCompletionsWithStream(
            TextCompletionsParam(
                messagesTurbo = getMessagesParamsTurbo(_currentConversation.value)
            )
        )
        var answerFromGPT: String = ""
        // When flow collecting updateLocalAnswer including FAB behavior expanded.
        // On completion FAB == false
        flow.onCompletion {}.collect { value ->
            answerFromGPT += value
            updateLocalAnswer(answerFromGPT.trim())
        }
//        responseRepo.addResponse(Response(datetime = LocalDateTime.now().toString(), response = answerFromGPT))
        _response.value = answerFromGPT
    }

    private fun updateLocalAnswer(answer: String) {
        val currentListMessage: MutableList<MessageModel> =
            getMessagesByConversation(_currentConversation.value).toMutableList()

        currentListMessage[0] = currentListMessage[0].copy(answer = answer)

        setMessages(currentListMessage)
    }

    private fun getMessagesParamsTurbo(conversationId: String): List<MessageTurbo> {
        if (_messages.value[conversationId] == null) return listOf()

        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.clone() as HashMap<String, MutableList<MessageModel>>

        val response: MutableList<MessageTurbo> = mutableListOf(
            MessageTurbo(
                role = TurboRole.system, content = "Markdown style if exists code"
            )
        )

        for (message in messagesMap[conversationId]!!.reversed()) {
            response.add(MessageTurbo(content = message.question))

            if (message.answer != "Let me think...") {
                response.add(MessageTurbo(content = message.answer, role = TurboRole.user))
            }
        }
        return response.toList()
    }

    private fun setMessages(messages: MutableList<MessageModel>) {
        val messagesMap: HashMap<String, MutableList<MessageModel>> =
            _messages.value.clone() as HashMap<String, MutableList<MessageModel>>

        messagesMap[_currentConversation.value] = messages

        _messages.value = messagesMap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveData() {
        promptRepo.addPrompt(Prompt(datetime = LocalDateTime.now().toString(), prompt = prompt))
        responseRepo.addResponse(
            Response(
                datetime = LocalDateTime.now().toString(),
                response = response.value
            )
        )
    }
}