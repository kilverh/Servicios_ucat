@file:OptIn(ExperimentalMaterial3Api::class)
package com.ucat.servicios_ucat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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

data class Reserva(
    val id: String = "",
    val tipo: String = "",
    val recurso: String = "",
    val fecha: String = "",
    val hora: String = ""
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
    var reservaEdit by remember { mutableStateOf<Reserva?>(null) }

    var recursosDisponibles by remember { mutableStateOf(listOf<String>()) }
    val tipos = listOf("Juego de mesa", "Instrumento", "Balón", "Cancha")
    val horasDisponibles = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00")

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
                        hora = it.getString("hora") ?: ""
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
                                        cargarRecursosPorTipo(reserva.tipo)
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = {
                                        db.collection("reservas").document(reserva.id).delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Reserva eliminada",
                                                    Toast.LENGTH_SHORT
                                                ).show()
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
                var expandedTipo by remember { mutableStateOf(false) }
                var expandedHora by remember { mutableStateOf(false) }
                var expandedRecurso by remember { mutableStateOf(false) }
                var expandedDeporte by remember { mutableStateOf(false) }
                val deportes = listOf("Fútbol", "Baloncesto", "Voleibol", "Pin Pon")

                var deporteEdit by remember { mutableStateOf("") }

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

                    // TIPO
                    ExposedDropdownMenuBox(
                        expanded = expandedTipo,
                        onExpandedChange = { expandedTipo = !expandedTipo }) {
                        TextField(
                            value = tipoEdit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tipo") },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expandedTipo = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTipo,
                            onDismissRequest = { expandedTipo = false }
                        ) {
                            tipos.forEach { tipo ->
                                DropdownMenuItem(
                                    text = { Text(tipo) },
                                    onClick = {
                                        tipoEdit = tipo
                                        recursoEdit = ""
                                        deporteEdit = ""
                                        cargarRecursosPorTipo(tipo)
                                        expandedTipo = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // FECHA
                    FechaPickerField(fechaEdit) { fechaEdit = it }

                    Spacer(modifier = Modifier.height(8.dp))

                    // HORA
                    ExposedDropdownMenuBox(
                        expanded = expandedHora,
                        onExpandedChange = { expandedHora = !expandedHora }) {
                        TextField(
                            value = horaEdit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hora") },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { expandedHora = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = expandedHora,
                            onDismissRequest = { expandedHora = false }
                        ) {
                            horasDisponibles.forEach { hora ->
                                DropdownMenuItem(
                                    text = { Text(hora) },
                                    onClick = {
                                        horaEdit = hora
                                        expandedHora = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // RECURSO
                    if (recursosDisponibles.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = expandedRecurso,
                            onExpandedChange = { expandedRecurso = !expandedRecurso }) {
                            TextField(
                                value = recursoEdit,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Recurso") },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { expandedRecurso = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = expandedRecurso,
                                onDismissRequest = { expandedRecurso = false }
                            ) {
                                recursosDisponibles.forEach { recurso ->
                                    DropdownMenuItem(
                                        text = { Text(recurso) },
                                        onClick = {
                                            recursoEdit = recurso
                                            expandedRecurso = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // DEPORTE (solo si tipo es Cancha)
                    if (tipoEdit == "Cancha") {
                        Spacer(modifier = Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = expandedDeporte,
                            onExpandedChange = { expandedDeporte = !expandedDeporte }) {
                            TextField(
                                value = deporteEdit,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Deporte") },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { expandedDeporte = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDeporte,
                                onDismissRequest = { expandedDeporte = false }
                            ) {
                                deportes.forEach { deporte ->
                                    DropdownMenuItem(
                                        text = { Text(deporte) },
                                        onClick = {
                                            deporteEdit = deporte
                                            expandedDeporte = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // BOTONES
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = {
                                if (fechaEdit.isBlank() || horaEdit.isBlank() || recursoEdit.isBlank() || (tipoEdit == "Cancha" && deporteEdit.isBlank())) {
                                    Toast.makeText(
                                        context,
                                        "Por favor completa todos los campos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                val data = mutableMapOf(
                                    "fecha" to fechaEdit,
                                    "hora" to horaEdit,
                                    "tipo" to tipoEdit
                                )

                                when (tipoEdit) {
                                    "Juego de mesa" -> data["juego"] = recursoEdit
                                    "Instrumento" -> data["instrumento"] = recursoEdit
                                    "Balón" -> data["balon"] = recursoEdit
                                    "Cancha" -> {
                                        data["cancha"] = recursoEdit
                                        data["deporte"] = deporteEdit
                                    }
                                }

                                db.collection("reservas").document(reservaEdit!!.id)
                                    .update(data as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Reserva actualizada",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
