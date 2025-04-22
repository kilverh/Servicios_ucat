package com.ucat.servicios_ucat

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucat.servicios_ucat.ui.theme.BlueButton
import com.ucat.servicios_ucat.ui.theme.BlueInstitutional

data class Reserva(
    val id: String = "",
    val tipo: String = "",
    val fecha: String = "",
    val hora: String = "",
    val elemento: String = "",
    val cantidad: Int = 0,
    val cancha: String = "",
    val deporte: String = ""
)

fun cargarReservas(userId: String?, onLoaded: (List<Reserva>) -> Unit, onError: () -> Unit) {
    if (userId == null) {
        onError()
        return
    }

    FirebaseFirestore.getInstance().collection("reservas")
        .whereEqualTo("uid", userId)
        .get()
        .addOnSuccessListener { snapshot ->
            Log.d("FirestoreDebug", "Reservas encontradas: ${snapshot.size()} documentos")
            val lista = snapshot.documents.map {
                Log.d("FirestoreDebug", "Documento: ${it.data}")
                Reserva(
                    id = it.id,
                    tipo = it.getString("tipo") ?: "",
                    fecha = it.getString("fecha") ?: "",
                    hora = it.getString("hora") ?: "",
                    elemento = it.getString("elemento") ?: "",
                    cantidad = it.getLong("cantidad")?.toInt() ?: 0,
                    cancha = it.getString("cancha") ?: "",
                    deporte = it.getString("deporte") ?: ""
                )
            }
            onLoaded(lista)
        }
        .addOnFailureListener {
            Log.e("FirestoreDebug", "Error al cargar reservas", it)
            onError()
        }
}

@Composable
fun ManageBookings(onVolverAlMenu: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    Log.d("FirestoreDebug", "UID actual: $userId")

    var reservas by remember { mutableStateOf(listOf<Reserva>()) }
    var isEditing by remember { mutableStateOf(false) }
    var reservaActual by remember { mutableStateOf<Reserva?>(null) }

    var nuevoTipo by remember { mutableStateOf("") }
    var nuevaFecha by remember { mutableStateOf("") }
    var nuevaHora by remember { mutableStateOf("") }
    var nuevoElemento by remember { mutableStateOf("") }
    var nuevaCantidad by remember { mutableStateOf("") }
    var nuevaCancha by remember { mutableStateOf("") }
    var nuevoDeporte by remember { mutableStateOf("") }

    fun recargar() {
        cargarReservas(userId,
            onLoaded = { reservas = it },
            onError = {
                Toast.makeText(context, "Error al cargar reservas o usuario no autenticado", Toast.LENGTH_SHORT).show()
            }
        )
    }

    LaunchedEffect(userId) {
        recargar()
    }

    Box(modifier = Modifier.fillMaxSize().background(BlueInstitutional)) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "TUS RESERVAS",
                style = MaterialTheme.typography.headlineSmall,
                color = BlueButton,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { recargar() }) {
                Text("Refrescar reservas")
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (reservas.isEmpty()) {
                Text("No tienes reservas registradas.", color = Color.White)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reservas) { reserva ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Tipo: ${reserva.tipo}")
                            Text("Fecha: ${reserva.fecha}")
                            Text("Hora: ${reserva.hora}")
                            if (reserva.elemento.isNotBlank()) Text("Elemento: ${reserva.elemento}")
                            if (reserva.cantidad > 0) Text("Cantidad: ${reserva.cantidad}")
                            if (reserva.cancha.isNotBlank()) Text("Cancha: ${reserva.cancha}")
                            if (reserva.deporte.isNotBlank()) Text("Deporte: ${reserva.deporte}")

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
                                    nuevoElemento = reserva.elemento
                                    nuevaCantidad = reserva.cantidad.toString()
                                    nuevaCancha = reserva.cancha
                                    nuevoDeporte = reserva.deporte
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }

                                IconButton(onClick = {
                                    firestore.collection("reservas").document(reserva.id).delete()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show()
                                            recargar()
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
                Spacer(modifier = Modifier.height(16.dp))
                Text("Editar reserva", style = MaterialTheme.typography.titleMedium, color = BlueButton)
                Spacer(modifier = Modifier.height(8.dp))

                TextField(value = nuevoTipo, onValueChange = { nuevoTipo = it }, label = { Text("Tipo") }, modifier = Modifier.fillMaxWidth())
                TextField(value = nuevaFecha, onValueChange = { nuevaFecha = it }, label = { Text("Fecha") }, modifier = Modifier.fillMaxWidth())
                TextField(value = nuevaHora, onValueChange = { nuevaHora = it }, label = { Text("Hora") }, modifier = Modifier.fillMaxWidth())
                TextField(value = nuevoElemento, onValueChange = { nuevoElemento = it }, label = { Text("Elemento") }, modifier = Modifier.fillMaxWidth())
                TextField(value = nuevaCantidad, onValueChange = { nuevaCantidad = it }, label = { Text("Cantidad") }, modifier = Modifier.fillMaxWidth())
                TextField(value = nuevaCancha, onValueChange = { nuevaCancha = it }, label = { Text("Cancha") }, modifier = Modifier.fillMaxWidth())
                TextField(value = nuevoDeporte, onValueChange = { nuevoDeporte = it }, label = { Text("Deporte") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        reservaActual?.let {
                            firestore.collection("reservas").document(it.id)
                                .update(
                                    "tipo", nuevoTipo,
                                    "fecha", nuevaFecha,
                                    "hora", nuevaHora,
                                    "elemento", nuevoElemento,
                                    "cantidad", nuevaCantidad.toIntOrNull() ?: 0,
                                    "cancha", nuevaCancha,
                                    "deporte", nuevoDeporte
                                )
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Reserva actualizada", Toast.LENGTH_SHORT).show()
                                    recargar()
                                    isEditing = false
                                    reservaActual = null
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al actualizar reserva", Toast.LENGTH_SHORT).show()
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar cambios")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
