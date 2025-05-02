package com.ucat.servicios_ucat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucat.servicios_ucat.ui.theme.BlueButton
import com.ucat.servicios_ucat.ui.theme.DarkGrey
import com.ucat.servicios_ucat.ui.theme.White
import androidx.compose.ui.text.input.ImeAction

@Composable
fun AccountSettingsScreen(
    onVolverAlDashboard: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser
    val userId = currentUser?.uid

    var nombre by remember { mutableStateOf("") }
    var codigo by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var repNuevaContrasena by remember { mutableStateOf("") }
    val mostrarNuevaContrasena = remember { mutableStateOf(false) }
    val mostrarRepNuevaContrasena = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var mostrarConfirmacionEliminar by remember { mutableStateOf(false) }

    val contrasenasCoinciden = nuevaContrasena == repNuevaContrasena && nuevaContrasena.isNotBlank()

    fun esContrasenaValida(contrasena: String): Boolean {
        val regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$"
        return contrasena.matches(regex.toRegex())
    }

    LaunchedEffect(Unit) {
        isLoading = true
        userId?.let { uid ->
            db.collection("usuarios").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        nombre = ""
                        codigo = ""
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al cargar los datos de la cuenta.", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
        } ?: run {
            Toast.makeText(context, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF2C80C1),
                Color(0xFF4C9BE3),
                Color(0xFF042137)
            )
        )
    )) {
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
            Spacer(modifier = Modifier.height(50.dp))

            Text("AJUSTES DE CUENTA", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = White)

            Spacer(modifier = Modifier.height(30.dp))

            if (isLoading) {
                CircularProgressIndicator(color = White)
            } else {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier
                        .width(380.dp)
                        .height(60.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { /* TODO: Focus next */ }) // Opcional
                )
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código Estudiantil") },
                    modifier = Modifier
                        .width(380.dp)
                        .height(60.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { /* TODO: Submit */ }) // Opcional
                )
                Spacer(modifier = Modifier.height(32.dp))

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
                    onClick = {
                        isLoading = true
                        val user = auth.currentUser
                        val updates = hashMapOf<String, Any>(
                            "nombre" to nombre,
                            "codigo" to codigo
                        )
                        userId?.let { uid ->
                            db.collection("usuarios").document(uid).update(updates)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Datos de la cuenta actualizados.", Toast.LENGTH_SHORT).show()
                                    if (nuevaContrasena.isNotBlank() && contrasenasCoinciden && esContrasenaValida(nuevaContrasena)) {
                                        user?.updatePassword(nuevaContrasena)
                                            ?.addOnSuccessListener {
                                                Toast.makeText(context, "Contraseña actualizada.", Toast.LENGTH_SHORT).show()
                                                nuevaContrasena = ""
                                                repNuevaContrasena = ""
                                            }
                                            ?.addOnFailureListener { e ->
                                                Toast.makeText(context, "Error al actualizar la contraseña: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                    isLoading = false
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Error al actualizar los datos de la cuenta: ${e.message}", Toast.LENGTH_SHORT).show()
                                    isLoading = false
                                }
                        } ?: run {
                            Toast.makeText(context, "Usuario no autenticado.", Toast.LENGTH_SHORT).show()
                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .width(190.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueButton,
                        disabledContainerColor = DarkGrey
                    ),
                    shape = RectangleShape,
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Actualizar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Divider(color = White.copy(alpha = 0.3f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { mostrarConfirmacionEliminar = true },
                    modifier = Modifier
                        .width(190.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.7f),
                        disabledContainerColor = DarkGrey
                    ),
                    shape = RectangleShape
                ) {
                    Text("Eliminar Cuenta", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = White)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onVolverAlDashboard,
                        modifier = Modifier
                            .width(120.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGrey),
                        shape = RectangleShape
                    ) {
                        Text("Volver", fontSize = 18.sp, color = White)
                    }
                    Button(
                        onClick = onCerrarSesion,
                        modifier = Modifier
                            .width(200.dp)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = RectangleShape
                    ) {
                        Text("Cerrar Sesión", fontSize = 18.sp, color = White)
                    }
                }
            }

            // Dialog de confirmación para eliminar cuenta
            if (mostrarConfirmacionEliminar) {
                AlertDialog(
                    onDismissRequest = { mostrarConfirmacionEliminar = false },
                    title = { Text("Confirmar Eliminación", color = White) },
                    text = { Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción es irreversible.", color = White) },
                    confirmButton = {
                        Button(
                            onClick = {
                                mostrarConfirmacionEliminar = false
                                isLoading = true
                                currentUser?.delete()
                                    ?.addOnSuccessListener {
                                        Toast.makeText(context, "Cuenta eliminada exitosamente.", Toast.LENGTH_SHORT).show()
                                        onCerrarSesion() // Navegar a la pantalla de inicio de sesión
                                    }
                                    ?.addOnFailureListener { e ->
                                        Toast.makeText(context, "Error al eliminar la cuenta: ${e.message}", Toast.LENGTH_SHORT).show()
                                        isLoading = false
                                    }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Eliminar", color = White)
                        }
                    },
                    dismissButton = {
                        Button(onClick = { mostrarConfirmacionEliminar = false }, colors = ButtonDefaults.buttonColors(containerColor = DarkGrey)) {
                            Text("Cancelar", color = White)
                        }
                    },
                    containerColor = DarkGrey
                )
            }
        }
    }
}
