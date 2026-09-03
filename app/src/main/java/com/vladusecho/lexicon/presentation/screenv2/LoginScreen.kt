package com.vladusecho.lexicon.presentation.screenv2

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.vladusecho.lexicon.R
import com.vladusecho.lexicon.presentation.ui.theme.LexiconTheme
import com.vladusecho.lexicon.presentation.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginClick: () -> Unit
) {

    val context = LocalContext.current
    val webClientId = stringResource(id = R.string.default_web_client_id)
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is LoginViewModel.AuthState.Authenticated) {
            onLoginClick()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (authState is LoginViewModel.AuthState.Loading) {
            CircularProgressIndicator()
        } else {
            LoginButton(
                onLoginClick = {
                    coroutineScope.launch {
                        try {
                            // 1. Настройка параметров запроса Google
                            val googleIdOption =
                                GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(webClientId)
                                    .setAutoSelectEnabled(true)
                                    .build()

                            // 2. Создание запроса
                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(googleIdOption)
                                .build()

                            // 3. Вызов системного окна выбора аккаунта
                            val result = credentialManager.getCredential(context, request)

                            // 4. Извлечение токена
                            val credential = result.credential
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken

                            // 5. Передача токена во ViewModel для Firebase Auth
                            viewModel.signInWithGoogle(idToken)

                        } catch (e: GetCredentialException) {
                            Log.e("Auth", "Ошибка Credential Manager: ${e.message}")
                        } catch (e: Exception) {
                            Log.e("Auth", "Неизвестная ошибка: ${e.message}")
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun LoginButton(
    onLoginClick: () -> Unit
) {
    Button(
        onClick = onLoginClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_google),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = "Войти через Google",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )
    }
}

@Composable
@Preview
fun LoginScreenPreview() {
    LexiconTheme() {
        LoginScreen {

        }
    }
}