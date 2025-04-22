package com.ucat.servicios_ucat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ucat.servicios_ucat.ui.theme.BlueButton

@Composable
fun Recover(
    onVolverAlLogin: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 0.dp, y = -50.dp)
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.FillWidth,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("RECUPERAR CONTRASEÑA", fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(76.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo institucional") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(76.dp))


            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    color = if (mensaje.startsWith("Error")) BlueButton else BlueButton,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        FirebaseAuth.getInstance()
                            .sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    mensaje = "Correo de recuperación enviado."
                                } else {
                                    mensaje = "Error: ${task.exception?.message}"
                                }
                            }
                    } else {
                        mensaje = "Por favor ingresa tu correo."
                    }
                },
                modifier = Modifier
                    .width(190.dp)
                    .height(50.dp),
                shape = RectangleShape
            ) {
                Text("Enviar correo")
            }

            Spacer(modifier = Modifier.height(46.dp))

            Text(
                text = "¿Ya tienes una cuenta? Inicia sesión",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onVolverAlLogin() }
            )
        }
    }
}
