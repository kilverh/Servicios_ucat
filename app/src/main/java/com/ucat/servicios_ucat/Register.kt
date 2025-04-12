package com.ucat.servicios_ucat

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RegistroScreen(
    onRegisterClick: (String, String, String, String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo institucional") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = contraseña, onValueChange = { contraseña = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onRegisterClick(nombre, apellido, correo, contraseña)
        }) {
            Text("REGISTRARSE")
        }
    }
}
