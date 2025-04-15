package com.ucat.servicios_ucat

import android.R.attr.enabled
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucat.servicios_ucat.ui.theme.BlueButton
import com.ucat.servicios_ucat.ui.theme.BlueInstitutional
import com.ucat.servicios_ucat.ui.theme.DarkGrey
import kotlinx.coroutines.NonDisposableHandle.parent

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    onRegistroExitoso: () -> Unit,
    onError: (String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var repContrasena by remember { mutableStateOf("") }
    val lock = remember{mutableStateOf(false)}
    var aceptaTerminos by remember { mutableStateOf(false) }
    val contrasenasCoinciden = contrasena == repContrasena && contrasena.isNotBlank()

    Box (modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = modifier
                .padding(36.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("REGÍSTRATE", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(54.dp))
            TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
            Spacer(modifier = Modifier.height(14.dp))
            TextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") })
            Spacer(modifier = Modifier.height(14.dp))
            TextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo") })
            Spacer(modifier = Modifier.height(14.dp))
            TextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                trailingIcon = { Image(painterResource(if (lock.value)R.drawable.eye_show else R.drawable.hidden_eye), contentDescription="REGISTRATE", modifier= Modifier.clickable{lock.value=!lock.value})},
                visualTransformation =
                    if (lock.value) VisualTransformation.None
                    else PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(14.dp))
            TextField(
                value = repContrasena,
                onValueChange = { repContrasena = it },
                trailingIcon = { Image(painterResource(if (lock.value)R.drawable.eye_show else R.drawable.hidden_eye), contentDescription="REGISTRATE", modifier= Modifier.clickable{lock.value=!lock.value})},
                label = { Text("Repite tu Contraseña") },
                visualTransformation =
                    if (lock.value) VisualTransformation.None
                    else PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(14.dp))

            if (!contrasenasCoinciden && repContrasena.isNotBlank()) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = BlueButton,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = aceptaTerminos,
                    onCheckedChange = { aceptaTerminos = it }
                )
                Text("Acepto los términos y condiciones", color = Color.White)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                RegistrarUsuario(
                    nombre = nombre,
                    apellido = apellido,
                    correo = correo,
                    contrasena = contrasena,
                    onSuccess = onRegistroExitoso,
                    onError = onError
                )
            },
                modifier = Modifier
                .width(250.dp)
                .height(60.dp),
            colors = ButtonColors(
                containerColor = BlueButton,
                contentColor = BlueButton,
                disabledContainerColor = DarkGrey,
                disabledContentColor = DarkGrey
            ),
                shape = RectangleShape,
                enabled = contrasenasCoinciden and aceptaTerminos

            ) {
                Text(
                    text = "Registrar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Ya tienes una cuenta?", color = Color.White)
            Text(text = "Inicia sesión", color = BlueButton)
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

