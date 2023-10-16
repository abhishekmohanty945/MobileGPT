package com.example.assignment3_277.ui.theme

import androidx.lifecycle.ViewModel
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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class GPTViewModel @Inject constructor(
    private val openAIRepo: OpenAIRepositoryImpl,
) : ViewModel() {

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
}