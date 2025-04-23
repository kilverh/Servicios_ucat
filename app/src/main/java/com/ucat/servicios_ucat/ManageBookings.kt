@file:OptIn(ExperimentalMaterial3Api::class)
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
import java.util.*

data class Reserva(
    val id: String = "",
    val tipo: String = "",
    val recurso: String = "",
    val fecha: String = "",
    val hora: String = "",
    val carrera: String = ""
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

    var recursosDisponibles by remember { mutableStateOf(listOf<String>()) }

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

    fun cargarRecursosPorTipo(tipo: String) {
        db.collection("inventario").document(tipo).get()
            .addOnSuccessListener { doc ->
                recursosDisponibles = when (tipo) {
                    "Juego de mesa" -> (doc.get("juegos") as? List<String>) ?: emptyList()
                    "Instrumento" -> (doc.get("instrumentos") as? List<String>) ?: emptyList()
                    "Balón" -> (doc.get("balones") as? List<String>) ?: emptyList()
                    "Cancha" -> (doc.get("canchas") as? List<String>) ?: emptyList()
                    else -> emptyList()
                }
            }
    }

    LaunchedEffect(Unit) {
        cargarReservas()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueInstitutional)
    ) {
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
                .fillMaxSize()
                .padding(36.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TUS RESERVAS",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (reservaEdit == null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reservas) { reserva ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Tipo: ${reserva.tipo}")
                                Text("Recurso: ${reserva.recurso}")
                                Text("Fecha: ${reserva.fecha}")
                                Text("Hora: ${reserva.hora}")

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {
                                        reservaEdit = reserva
                                        fechaEdit = reserva.fecha
                                        horaEdit = reserva.hora
                                        tipoEdit = reserva.tipo
                                        recursoEdit = reserva.recurso
                                        carreraEdit = reserva.carrera
                                        cargarRecursosPorTipo(reserva.tipo)
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = {
                                        db.collection("reservas").document(reserva.id).delete()
                                            .addOnSuccessListener {
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Editar Reserva",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FechaPickerField(fechaEdit) { fechaEdit = it }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = horaEdit,
                        onValueChange = { horaEdit = it },
                        label = { Text("Hora") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (recursosDisponibles.isNotEmpty()) {
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            TextField(
                                value = recursoEdit,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Recurso") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                recursosDisponibles.forEach { recurso ->
                                    DropdownMenuItem(
                                        text = { Text(recurso) },
                                        onClick = {
                                            recursoEdit = recurso
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (tipoEdit == "Cancha") {
                        TextField(
                            value = carreraEdit,
                            onValueChange = { carreraEdit = it },
                            label = { Text("Carrera") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                if (fechaEdit.isBlank() || horaEdit.isBlank() || recursoEdit.isBlank()) {
                                    Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }

                                val data = mutableMapOf(
                                    "fecha" to fechaEdit,
                                    "hora" to horaEdit
                                )

                                when (tipoEdit) {
                                    "Juego de mesa" -> data["juego"] = recursoEdit
                                    "Instrumento" -> data["instrumento"] = recursoEdit
                                    "Balón" -> data["balon"] = recursoEdit
                                    "Cancha" -> {
                                        data["cancha"] = recursoEdit
                                        data["carrera"] = carreraEdit
                                    }
                                }

                                db.collection("reservas").document(reservaEdit!!.id)
                                    .update(data as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Reserva actualizada", Toast.LENGTH_SHORT).show()
                                        reservaEdit = null
                                        cargarReservas()
                                    }
                            },
                            modifier = Modifier
                                .width(160.dp)
                                .height(50.dp),
                            shape = RectangleShape
                        ) {
                            Text("Actualizar")
                        }

                        Button(
                            onClick = { reservaEdit = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                            modifier = Modifier
                                .width(160.dp)
                                .height(50.dp),
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
