package com.ucat.servicios_ucat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

@Composable
fun Login(
    modifier: Modifier = Modifier,
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
    onRecuperar: () -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val mostrarContrasena = remember { mutableStateOf(false) }

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
            Text("INICIA SESIÓN", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(70.dp))

            TextField(
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Institucional") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
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
                    auth.signInWithEmailAndPassword(correo, contrasena)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                onLoginExitoso()
                            } else {
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
                shape = RectangleShape
            ) {
                Text(
                    "Iniciar sesión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(44.dp))
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
