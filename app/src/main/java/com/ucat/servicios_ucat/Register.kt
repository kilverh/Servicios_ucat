package com.ucat.servicios_ucat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    onRegistroExitoso: () -> Unit,
    onError: (String) -> Unit
) {
    Column(modifier = modifier.padding(16.dp)) {

        Button(onClick = {
            RegistrarUsuario(
                modifier = Modifier,
                nombre = "Juan",
                apellido = "Pérez",
                correo = "correo@ejemplo.com",
                contraseña = "123456",
                onSuccess = onRegistroExitoso,
                onError = onError
            )
        }) {
            Text("Registrar")
        }
    }
}

fun RegistrarUsuario(
    modifier: Modifier = Modifier,
    nombre: String,
    apellido: String,
    correo: String,
    contraseña: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    auth.createUserWithEmailAndPassword(correo, contraseña)
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

