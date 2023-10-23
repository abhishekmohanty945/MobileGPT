package com.example.assignment3_277

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.assignment3_277.models.MessageModel
import com.example.assignment3_277.ui.theme.Assignment3_277Theme
import com.example.assignment3_277.ui.theme.GPTViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val gptViewModel: GPTViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment3_277Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color(0Xff343541)
                ) {
                    Ui(gptViewModel = gptViewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun Ui(gptViewModel: GPTViewModel) {
    val imageModifier = Modifier.size(width = 70.dp, height = 70.dp)

    val conversationId by gptViewModel.currentConversationState.collectAsState()
    val messagesMap by gptViewModel.messagesState.collectAsState()
    val responseState by gptViewModel.response.collectAsState()

    var response = responseState.isNotEmpty()

    var messages: List<MessageModel> =
        if (messagesMap[conversationId] == null) listOf() else messagesMap[conversationId]!!

    var clearText by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(verticalArrangement = Arrangement.Top) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Fit
            )
            Text(
                text = "ChatGPT",
                color = Color.White,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween, // Adjust as needed
            verticalAlignment = Alignment.CenterVertically // Adjust as needed
        )
        {
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "Prompt: ",
                color = Color.White,
                fontSize = 30.sp
            )
        }
        val scope = rememberCoroutineScope()
        var text by remember { mutableStateOf(TextFieldValue("")) }

        val focusManager = LocalFocusManager.current
        Row(
            modifier = Modifier
                .fillMaxWidth(),
//                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text, onValueChange = { text = it },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFF767785),
                    textColor = Color.White
                ),
                label = null,
                placeholder = { Text("Ask me anything", fontSize = 12.sp, color = Color.White) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(100.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween, // Adjust as needed
        ) {
            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    clearText = false
                    scope.launch {
                        val textClone = text.text
                        text = TextFieldValue("")
                        gptViewModel.sendMessage(textClone)

                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B6C7B)),
                contentPadding = PaddingValues(
                    start = 28.dp,
                    end = 28.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = "Send",
                    fontSize = 30.sp,
                    color = Color.White
                )
            }

            Button(
                onClick = {
                    clearText = true
                    messages = emptyList()
                    gptViewModel._response.value = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B6C7B)),
                contentPadding = PaddingValues(
                    start = 28.dp,
                    end = 28.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 30.sp,
                    color = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "Response: ",
                color = Color.White,
                fontSize = 30.sp
            )

            Button(
                enabled = response,
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    clearText = false
                    scope.launch {
                        text = TextFieldValue("")
                        gptViewModel.saveData()

                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B6C7B)),
                contentPadding = PaddingValues(
                    start = 28.dp,
                    end = 28.dp,
                    top = 12.dp,
                    bottom = 12.dp
                ),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = "Save/Audit",
                    fontSize = 30.sp,
                    color = Color.White
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF767785))
            ) {

                messages.getOrNull(0)?.let {
                    if (!clearText) {
                        Text(
                            text = it.answer,
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 18.dp, vertical = 12.dp)
                                .verticalScroll(
                                    rememberScrollState()
                                ),

                            textAlign = TextAlign.Justify,
                        )
                    }
                }
            }
        }
    }
}