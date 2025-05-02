package com.ucat.servicios_ucat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.ucat.servicios_ucat.ui.theme.BlueButton
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import com.ucat.servicios_ucat.ui.theme.White

@Composable
fun Login(
    modifier: Modifier = Modifier,
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
    onRecuperar: () -> Unit,
    onError: (String) -> Unit
) {
    val focusCorreo = remember { FocusRequester() }
    val focusContrasena = remember { FocusRequester() }

    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val mostrarContrasena = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    fun VerificarCorreo(onExito: () -> Unit) {
        val user = auth.currentUser
        if (user?.isEmailVerified == true) {
            onExito()
        } else {
            isLoading = false // Asegúrate de resetear el estado de carga aquí
            Toast.makeText(
                context,
                "Por favor, verifica tu correo electrónico antes de iniciar sesión.",
                Toast.LENGTH_LONG
            ).show()
            auth.signOut() // Cierra la sesión del usuario no verificado
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 0.dp, y = -65.dp)
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.FillWidth,
        )

        Column(
            modifier = modifier
                .padding(36.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Inicio formulario inisio de sesión valida por medio de FireAuth
            Text("INICIA SESIÓN", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = White)
            Spacer(modifier = Modifier.height(70.dp))

            TextField(
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp)
                    .focusRequester(focusCorreo),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusContrasena.requestFocus() }
                ),
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Institucional") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp)
                    .focusRequester(focusContrasena),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                ),
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                visualTransformation = if (mostrarContrasena.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (mostrarContrasena.value) R.drawable.eye_show else R.drawable.hidden_eye
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            mostrarContrasena.value = !mostrarContrasena.value
                        }
                    )
                }
            )

            Spacer(modifier = Modifier.height(70.dp))

            Button(
                onClick = {
                    isLoading = true
                    auth.signInWithEmailAndPassword(correo, contrasena)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                VerificarCorreo {
                                    isLoading = false
                                    onLoginExitoso()
                                }
                            } else {
                                isLoading = false
                                Toast.makeText(
                                    context,
                                    "UPS!!! Parece que el correo o la contraseña son incorrectos.",
                                    Toast.LENGTH_LONG
                                ).show()
                                onError("Login fallido")
                            }
                        }
                },
                modifier = Modifier
                    .width(190.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueButton
                ),
                shape = RectangleShape,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        "Iniciar sesión",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(34.dp))
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onRecuperar() }
            )
            Text(
                text = "¿No tienes una cuenta? Regístrate aquí",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onIrARegistro() }
            )
        }
    }
}
