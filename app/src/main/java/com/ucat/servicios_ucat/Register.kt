package com.ucat.servicios_ucat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.firestore.FirebaseFirestore
import com.ucat.servicios_ucat.ui.theme.BlueButton
import com.ucat.servicios_ucat.ui.theme.DarkGrey

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    onIrALogin: () -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var repContrasena by remember { mutableStateOf("") }
    val lockContrasena = remember { mutableStateOf(false) }
    val lockRepContrasena = remember { mutableStateOf(false) }
    var aceptaTerminos by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val contrasenasCoinciden = contrasena == repContrasena && contrasena.isNotBlank()
    val camposLlenos = nombre.isNotBlank() && apellido.isNotBlank() && correo.isNotBlank() &&
            contrasena.isNotBlank() && repContrasena.isNotBlank()

    fun esContrasenaValida(contrasena: String): Boolean {
        val regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$"
        return contrasena.matches(regex.toRegex())
    }

    fun esCorreoValido(correo: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
        return correo.matches(emailRegex.toRegex())
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            modifier = modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Text("REGÍSTRATE", fontSize = 30.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Institucional") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
            )
            if (correo.isNotBlank() && !esCorreoValido(correo)) {
                Text(
                    text = "El correo no es válido",
                    color = BlueButton,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                trailingIcon = {
                    Image(
                        painter = painterResource(if (lockContrasena.value) R.drawable.eye_show else R.drawable.hidden_eye),
                        contentDescription = "Mostrar/Ocultar contraseña",
                        modifier = Modifier.clickable { lockContrasena.value = !lockContrasena.value }
                    )
                },
                visualTransformation = if (lockContrasena.value) VisualTransformation.None else PasswordVisualTransformation()
            )
            if (contrasena.isNotBlank() && !esContrasenaValida(contrasena)) {
                Text(
                    text = "Debe tener al menos 1 mayúscula, 1 número y 1 símbolo.",
                    color = BlueButton,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = repContrasena,
                onValueChange = { repContrasena = it },
                label = { Text("Repite tu Contraseña") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                trailingIcon = {
                    Image(
                        painter = painterResource(if (lockRepContrasena.value) R.drawable.eye_show else R.drawable.hidden_eye),
                        contentDescription = "Mostrar/Ocultar contraseña",
                        modifier = Modifier.clickable { lockRepContrasena.value = !lockRepContrasena.value }
                    )
                },
                visualTransformation = if (lockRepContrasena.value) VisualTransformation.None else PasswordVisualTransformation()
            )
            if (!contrasenasCoinciden && repContrasena.isNotBlank()) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = BlueButton,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = aceptaTerminos,
                    onCheckedChange = { aceptaTerminos = it }
                )
                Text("Acepto los términos y condiciones", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    RegistrarUsuario(
                        nombre = nombre,
                        apellido = apellido,
                        correo = correo,
                        contrasena = contrasena,
                        onSuccess = {
                            Toast.makeText(context, "Cuenta creada exitosamente", Toast.LENGTH_LONG).show()
                            nombre = ""
                            apellido = ""
                            correo = ""
                            contrasena = ""
                            repContrasena = ""
                            aceptaTerminos = false
                            isLoading = false
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            isLoading = false
                        }
                    )
                },
                modifier = Modifier
                    .width(190.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueButton,
                    disabledContainerColor = DarkGrey
                ),
                shape = RectangleShape,
                enabled = !isLoading &&
                        camposLlenos &&
                        contrasenasCoinciden &&
                        aceptaTerminos &&
                        esContrasenaValida(contrasena) &&
                        esCorreoValido(correo)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "REGISTRARSE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¿Ya tienes una cuenta? Inicia sesión",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onIrALogin() }
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

fun RegistrarUsuario(
        modifier: Modifier = Modifier,
        nombre: String,
        apellido: String,
        correo: String,
        contrasena: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val user = hashMapOf(
                        "nombre" to nombre,
                        "apellido" to apellido,
                        "correo" to correo
                    )
                    db.collection("usuarios").document(uid).set(user)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onError(e.message ?: "Error al guardar datos") }
                } else {
                    onError(task.exception?.message ?: "Error al crear usuario")
                }
            }
}


