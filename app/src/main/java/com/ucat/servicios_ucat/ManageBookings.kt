package com.ucat.servicios_ucat

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucat.servicios_ucat.ui.theme.BlueInstitutional
import java.text.SimpleDateFormat
import java.util.*

data class Reserva(
    val id: String = "",
    val tipo: String = "",
    val recurso: String = "",
    val fecha: String = "",
    val hora: String = "",
    val carrera: String = ""  // Información adicional para canchas
)

@Composable
fun ManageBookings(onVolverAlMenu: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    var reservas by remember { mutableStateOf(listOf<Reserva>()) }
    var fechaEdit by remember { mutableStateOf("") }
    var horaEdit by remember { mutableStateOf("") }
    var tipoEdit by remember { mutableStateOf("") }
    var recursoEdit by remember { mutableStateOf("") }
    var carreraEdit by remember { mutableStateOf("") }
    var reservaEdit by remember { mutableStateOf<Reserva?>(null) }

    fun cargarReservas() {
        db.collection("reservas")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { result ->
                reservas = result.documents.map {
                    val tipo = it.getString("tipo") ?: ""
                    val nombreRecurso = when (tipo) {
                        "Juego de mesa" -> it.getString("juego") ?: ""
                        "Instrumento" -> it.getString("instrumento") ?: ""
                        "Balón" -> it.getString("balon") ?: ""
                        "Cancha" -> it.getString("cancha") ?: ""
                        else -> ""
                    }
                    Reserva(
                        id = it.id,
                        tipo = tipo,
                        recurso = nombreRecurso,
                        fecha = it.getString("fecha") ?: "",
                        hora = it.getString("hora") ?: "",
                        carrera = it.getString("carrera") ?: ""
                    )
                }
            }
    }

    LaunchedEffect(Unit) {
        cargarReservas()
    }

    Box(modifier = Modifier.fillMaxSize().background(BlueInstitutional)) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(0.8f),
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier.padding(36.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("TUS RESERVAS", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))

            if (reservaEdit == null) {
                LazyColumn(modifier = Modifier.fillMaxHeight(0.8f)) {
                    items(reservas) { reserva ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Tipo: ${reserva.tipo}")
                                Text("Recurso: ${reserva.recurso}")
                                Text("Fecha: ${reserva.fecha}")
                                Text("Hora: ${reserva.hora}")

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    IconButton(onClick = {
                                        reservaEdit = reserva
                                        fechaEdit = reserva.fecha
                                        horaEdit = reserva.hora
                                        tipoEdit = reserva.tipo
                                        recursoEdit = reserva.recurso
                                        carreraEdit = reserva.carrera
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = {
                                        db.collection("reservas").document(reserva.id).delete().addOnSuccessListener {
                                            Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show()
                                            cargarReservas()
                                        }
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Editar Reserva", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Fecha editable
                    FechaPickerField(fechaEdit) { fechaEdit = it }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Hora editable
                    TextField(
                        value = horaEdit,
                        onValueChange = { horaEdit = it },
                        label = { Text("Hora") },
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Campos adicionales si deseas permitir editarlos también
                    TextField(
                        value = recursoEdit,
                        onValueChange = { recursoEdit = it },
                        label = { Text("Recurso") },
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (tipoEdit == "Cancha") {
                        TextField(
                            value = carreraEdit,
                            onValueChange = { carreraEdit = it },
                            label = { Text("Carrera") },
                            modifier = Modifier.fillMaxWidth().height(60.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Botones
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(
                            onClick = {
                                if (fechaEdit.isBlank() || horaEdit.isBlank()) {
                                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val data = mutableMapOf(
                                    "fecha" to fechaEdit,
                                    "hora" to horaEdit
                                )
                                if (tipoEdit == "Cancha") {
                                    data["carrera"] = carreraEdit
                                }

                                db.collection("reservas").document(reservaEdit!!.id).update(data as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Reserva actualizada", Toast.LENGTH_SHORT).show()
                                        reservaEdit = null
                                        cargarReservas()
                                    }
                            },
                            modifier = Modifier.width(160.dp).height(50.dp),
                            shape = RectangleShape
                        ) {
                            Text("Actualizar")
                        }

                        Button(
                            onClick = { reservaEdit = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            modifier = Modifier.width(160.dp).height(50.dp),
                            shape = RectangleShape
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}