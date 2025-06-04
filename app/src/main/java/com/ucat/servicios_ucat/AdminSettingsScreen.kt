package com.ucat.servicios_ucat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucat.servicios_ucat.ui.theme.DarkGrey
import com.ucat.servicios_ucat.ui.theme.White
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminSettingsScreen(
    onVolverAlDashboard: () -> Unit,
    onCerrarSesionAdmin: () -> Unit
) {
    val context = LocalContext.current
    var nuevaContrasena by remember { mutableStateOf("") }
    var repNuevaContrasena by remember { mutableStateOf("") }
    val mostrarNuevaContrasena = remember { mutableStateOf(false) }
    val mostrarRepNuevaContrasena = remember { mutableStateOf(false) }
    var cambioContrasenaExitoso by remember { mutableStateOf<Boolean?>(null) }

    fun esContrasenaValida(contrasena: String): Boolean {
        val regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$"
        return contrasena.matches(regex.toRegex())
    }

    val contrasenasCoinciden = nuevaContrasena == repNuevaContrasena && nuevaContrasena.isNotBlank()

    val auth = FirebaseAuth.getInstance()

    fun cambiarContrasena() {
        if (nuevaContrasena.isNotBlank() && contrasenasCoinciden && esContrasenaValida(nuevaContrasena)) {
            val user = auth.currentUser
            user?.updatePassword(nuevaContrasena)
                ?.addOnSuccessListener {
                    cambioContrasenaExitoso = true
                    nuevaContrasena = ""
                    repNuevaContrasena = ""
                }
                ?.addOnFailureListener {
                    cambioContrasenaExitoso = false
                }
        } else {
            cambioContrasenaExitoso = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF042137),
                        Color(0xFF2C80C1),
                        Color(0xFF4C9BE3),
                        Color(0xFF042137)
                    )
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 0.dp, y = -65.dp)
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onVolverAlDashboard) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver al Dashboard", tint = White)
                }
                Text(
                    "AJUSTES DE ADMINISTRADOR",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            //Ajustes de administrador
            Text("Cambiar Contraseña", fontWeight = FontWeight.Bold, color = White, fontSize = 18.sp, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = nuevaContrasena,
                onValueChange = { nuevaContrasena = it },
                label = { Text("Nueva Contraseña") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                visualTransformation = if (mostrarNuevaContrasena.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { mostrarNuevaContrasena.value = !mostrarNuevaContrasena.value }) {
                        val icon = if (mostrarNuevaContrasena.value) R.drawable.eye_show else R.drawable.hidden_eye
                        Image(painter = painterResource(id = icon), contentDescription = "Mostrar/Ocultar contraseña")
                    }
                }
            )
            if (nuevaContrasena.isNotBlank() && !esContrasenaValida(nuevaContrasena)) {
                Text(
                    text = "Debe tener al menos 1 mayúscula, 1 número y 1 símbolo.",
                    color = White,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = repNuevaContrasena,
                onValueChange = { repNuevaContrasena = it },
                label = { Text("Repetir Nueva Contraseña") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                visualTransformation = if (mostrarRepNuevaContrasena.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { mostrarRepNuevaContrasena.value = !mostrarRepNuevaContrasena.value }) {
                        val icon = if (mostrarRepNuevaContrasena.value) R.drawable.eye_show else R.drawable.hidden_eye
                        Image(painter = painterResource(id = icon), contentDescription = "Mostrar/Ocultar contraseña")
                    }
                }
            )
            if (!contrasenasCoinciden && repNuevaContrasena.isNotBlank()) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = White,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { cambiarContrasena() },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGrey),
                shape = RectangleShape
            ) {
                Text("Cambiar Contraseña", fontSize = 18.sp, color = White)
            }

            if (cambioContrasenaExitoso == true) {
                Text("Contraseña cambiada exitosamente.", color = White)
            } else if (cambioContrasenaExitoso == false) {
                Text("Error al cambiar la contraseña. Verifica los requisitos.", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Divider(color = White.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCerrarSesionAdmin,
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.7f)),
                shape = RectangleShape
            ) {
                Text("Eliminar Cuenta", fontSize = 18.sp, color = White)
            }
        }
    }
}