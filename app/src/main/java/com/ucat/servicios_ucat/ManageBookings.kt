package com.ucat.servicios_ucat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Reserva(
    val id: String = "",
    val tipo: String = "",
    val fecha: String = "",
    val hora: String = ""
)

@Composable
fun ManageBookings(onVolverAlMenu: () -> Unit) {

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    var reservas by remember { mutableStateOf(listOf<Reserva>()) }
    var isEditing by remember { mutableStateOf(false) }
    var reservaActual by remember { mutableStateOf<Reserva?>(null) }
    var nuevoTipo by remember { mutableStateOf("") }
    var nuevaFecha by remember { mutableStateOf("") }
    var nuevaHora by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        firestore.collection("reservas")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                reservas = snapshot.documents.map {
                    Reserva(
                        id = it.id,
                        tipo = it.getString("tipo") ?: "",
                        fecha = it.getString("fecha") ?: "",
                        hora = it.getString("hora") ?: ""
                    )
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.FillWidth
        )
        Column(modifier = Modifier
            .padding(36.dp)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("TUS RESERVAS", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(reservas) { reserva ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Tipo: ${reserva.tipo}")
                            Text("Fecha: ${reserva.fecha}")
                            Text("Hora: ${reserva.hora}")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    isEditing = true
                                    reservaActual = reserva
                                    nuevoTipo = reserva.tipo
                                    nuevaFecha = reserva.fecha
                                    nuevaHora = reserva.hora
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }

                                IconButton(onClick = {
                                    firestore.collection("reservas").document(reserva.id).delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show()
                                            reservas = reservas.filterNot { it.id == reserva.id }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                                        }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }
                    }
                }
            }

            if (isEditing && reservaActual != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Editar reserva", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = nuevoTipo,
                    onValueChange = { nuevoTipo = it },
                    label = { Text("Tipo") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = nuevaFecha,
                    onValueChange = { nuevaFecha = it },
                    label = { Text("Fecha") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = nuevaHora,
                    onValueChange = { nuevaHora = it },
                    label = { Text("Hora") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    val id = reservaActual!!.id
                    firestore.collection("reservas").document(id)
                        .update(mapOf(
                            "tipo" to nuevoTipo,
                            "fecha" to nuevaFecha,
                            "hora" to nuevaHora
                        ))
                        .addOnSuccessListener {
                            Toast.makeText(context, "Reserva actualizada", Toast.LENGTH_SHORT).show()
                            isEditing = false
                            reservaActual = null
                            reservas = reservas.map {
                                if (it.id == id) it.copy(tipo = nuevoTipo, fecha = nuevaFecha, hora = nuevaHora) else it
                            }
                        }
                }) {
                    Text("Guardar cambios")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onVolverAlMenu,
                modifier = Modifier
                    .width(190.dp)
                    .height(50.dp),
                shape = RectangleShape){
                Text("Volver al menú")
            }
        }
    }
}
