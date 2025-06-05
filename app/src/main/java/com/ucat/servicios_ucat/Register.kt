package com.ucat.servicios_ucat

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import com.ucat.servicios_ucat.ui.theme.White

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    onIrALogin: () -> Unit
) {
    val context = LocalContext.current
    val focusNombre = remember { FocusRequester() }
    val focusCodigoEstudiante = remember { FocusRequester() }
    val focusCodigoAdmin = remember { FocusRequester() }
    val focusCorreo = remember { FocusRequester() }
    val focusContrasena = remember { FocusRequester() }
    val focusRepContrasena = remember { FocusRequester() }

    var nombre by remember { mutableStateOf("") }
    var codigoEstudiante by remember { mutableStateOf("") }
    var codigoAdmin by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var repContrasena by remember { mutableStateOf("") }
    val lockContrasena = remember { mutableStateOf(false) }
    val lockRepContrasena = remember { mutableStateOf(false) }
    var aceptaTerminos by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }


    var rolSeleccionado by remember { mutableStateOf("Estudiante") }
    val roles = listOf("Estudiante", "Administrador")
    var expandedRol by remember { mutableStateOf(false) }

    val CodigoA = "ADMINUCAT2025"

    val contrasenasCoinciden = contrasena == repContrasena && contrasena.isNotBlank()
    val camposLlenosEstudiante = nombre.isNotBlank() && codigoEstudiante.isNotBlank() && correo.isNotBlank() &&
            contrasena.isNotBlank() && repContrasena.isNotBlank()
    val camposLlenosAdmin = nombre.isNotBlank() && codigoAdmin.isNotBlank() && correo.isNotBlank() &&
            contrasena.isNotBlank() && repContrasena.isNotBlank()
    val camposLlenos by derivedStateOf {
        if (rolSeleccionado == "Estudiante") camposLlenosEstudiante else camposLlenosAdmin
    }


    val isCodigoAdminValido by derivedStateOf {
        if (rolSeleccionado == "Estudiante") true else codigoAdmin == CodigoA
    }

    fun esContrasenaValida(contrasena: String): Boolean {
        val regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).+$"
        return contrasena.matches(regex.toRegex())
    }

    fun esCorreoValido(correo: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|co)$"
        return correo.matches(emailRegex.toRegex()) && correo.endsWith("@ucatolica.edu.co")
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

            //Inicio formulario de registro, conectado con firebase
            Text("REGÍSTRATE", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = White)

            Spacer(modifier = Modifier.height(30.dp))

            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp)
                    .focusRequester(focusNombre),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        if (rolSeleccionado == "Estudiante") focusCodigoEstudiante.requestFocus()
                        else focusCodigoAdmin.requestFocus()
                    }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (rolSeleccionado == "Estudiante") {
                TextField(
                    value = codigoEstudiante,
                    onValueChange = { codigoEstudiante = it },
                    label = { Text("Código estudiantil") },
                    modifier = Modifier
                        .width(380.dp)
                        .height(60.dp)
                        .focusRequester(focusCodigoEstudiante),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusCorreo.requestFocus() }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                TextField(
                    value = codigoAdmin,
                    onValueChange = { codigoAdmin = it },
                    label = { Text("Código de administrador") },
                    modifier = Modifier
                        .width(380.dp)
                        .height(60.dp)
                        .focusRequester(focusCodigoAdmin),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusCorreo.requestFocus() }
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            TextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo Institucional") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp)
                    .focusRequester(focusCorreo),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusContrasena.requestFocus() }
                )
            )
            if (correo.isNotBlank() && !esCorreoValido(correo)) {
                Text(
                    text = "El correo institucional debe terminar en @ucatolica.edu.co",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp)
                    .focusRequester(focusContrasena),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusRepContrasena.requestFocus() }
                ),
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
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = repContrasena,
                onValueChange = { repContrasena = it },
                label = { Text("Repite tu Contraseña") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp)
                    .focusRequester(focusRepContrasena),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))


            ExposedDropdownMenuBox(
                expanded = expandedRol,
                onExpandedChange = { expandedRol = !expandedRol },
            ) {
                TextField(
                    readOnly = true,
                    value = rolSeleccionado,
                    onValueChange = {},
                    label = { Text("Rol") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRol) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                        .width(380.dp)
                        .height(60.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedRol,
                    onDismissRequest = { expandedRol = false }
                ) {
                    roles.forEach { rol ->
                        DropdownMenuItem(
                            text = { Text(text = rol) },
                            onClick = {
                                rolSeleccionado = rol
                                expandedRol = false
                            }
                        )
                    }
                }
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

                    if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank() || repContrasena.isBlank()) {
                        Toast.makeText(context, "Por favor, complete todos los campos obligatorios.", Toast.LENGTH_SHORT).show()
                        isLoading = false
                        return@Button
                    }

                    val codigoParaRegistro = if (rolSeleccionado == "Estudiante") codigoEstudiante else codigoAdmin
                    if (codigoParaRegistro.isBlank()) {
                        Toast.makeText(context, "Por favor, ingrese su código.", Toast.LENGTH_SHORT).show()
                        isLoading = false
                        return@Button
                    }

                    if (!contrasenasCoinciden) {
                        Toast.makeText(context, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                        isLoading = false
                        return@Button
                    }
                    if (!esContrasenaValida(contrasena)) {
                        Toast.makeText(context, "La contraseña no cumple los requisitos.", Toast.LENGTH_SHORT).show()
                        isLoading = false
                        return@Button
                    }
                    if (!esCorreoValido(correo)) {
                        Toast.makeText(context, "El correo institucional debe terminar en @ucatolica.edu.co", Toast.LENGTH_SHORT).show()
                        isLoading = false
                        return@Button
                    }


                    if (rolSeleccionado == "Administrador" && codigoAdmin != CodigoA) {
                        Toast.makeText(context, "Código de administrador incorrecto.", Toast.LENGTH_SHORT).show()
                        isLoading = false
                        return@Button
                    }

                    if (!aceptaTerminos) {
                        Toast.makeText(context, "Debe aceptar los términos y condiciones.", Toast.LENGTH_SHORT).show()
                        isLoading = false
                        return@Button
                    }


                    RegistrarUsuario(
                        nombre = nombre,
                        codigo = codigoParaRegistro,
                        correo = correo,
                        contrasena = contrasena,
                        rol = rolSeleccionado,
                        onSuccess = {
                            Toast.makeText(context, "Cuenta creada exitosamente. Por favor, revisa tu correo para verificar tu cuenta.", Toast.LENGTH_LONG).show()
                            nombre = ""
                            codigoEstudiante = ""
                            codigoAdmin = ""
                            correo = ""
                            contrasena = ""
                            repContrasena = ""
                            aceptaTerminos = false
                            isLoading = false
                            onIrALogin()
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
                        esCorreoValido(correo) &&
                        isCodigoAdminValido
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
    nombre: String,
    codigo: String,
    correo: String,
    contrasena: String,
    rol: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    auth.createUserWithEmailAndPassword(correo, contrasena)
        .addOnCompleteListener { task ->
            Log.d("Registro", "Resultado de createUserWithEmailAndPassword: ${task.isSuccessful}")
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.sendEmailVerification()
                    ?.addOnCompleteListener { verificationTask ->
                        Log.d("Registro", "Resultado de sendEmailVerification: ${verificationTask.isSuccessful}")
                        if (verificationTask.isSuccessful) {
                            val uid = user.uid
                            val userData = hashMapOf(
                                "nombre" to nombre,
                                "codigo" to codigo,
                                "rol" to rol,
                                "correo" to correo,
                                "isEmailVerified" to false
                            )
                            db.collection("usuarios").document(uid).set(userData)
                                .addOnSuccessListener {
                                    Log.d("Registro", "Datos guardados en Firestore exitosamente")
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Registro", "Error al guardar datos del usuario: ${e.message}")
                                    onError("Error al guardar datos del usuario: ${e.message}")
                                }
                        } else {
                            user.delete()
                                .addOnCompleteListener { Log.d("Registro", "Usuario eliminado tras fallo en verificación") }
                            Log.e("Registro", "Error al enviar el correo de verificación: ${verificationTask.exception?.message}")
                            onError("Error al enviar el correo de verificación: ${verificationTask.exception?.message}")
                        }
                    }
            } else {
                Log.e("Registro", "Error al crear la cuenta: ${task.exception?.message}")
                onError("Error al crear la cuenta: ${task.exception?.message}")
            }
        }
}