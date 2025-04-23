package com.ucat.servicios_ucat

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucat.servicios_ucat.ui.theme.BlueButton
import com.ucat.servicios_ucat.ui.theme.BlueInstitutional
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Booking(
    modifier: Modifier = Modifier,
    onReservaExitosa: () -> Unit,
    onVolverAlMenu: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    val tipoList = listOf("Cancha", "Juego de mesa", "Instrumento", "Balón")
    val horas = listOf("08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 1:00", "2:00 - 3:00", "3:00 - 4:00", "4:00 - 5:00", "5:00 - 6:00", "6:00 - 7:00", "7:00 - 8:00")
    val juegos = listOf("UNO", "Monopoly", "Jenga", "Ajedrez", "Parqués 4 puestos", "Parqués 6 puestos", "Damas Chinas", "Rummi", "Clue", "Conecta 4", "Pictureka", "Astucia Naval", "Superbanco Colombia", "Invasión", "Scrabble", "Laberinto", "Risk", "Pasaporte al Mundo", "Domino", "Cranium", "Twister", "Cubeez", "Spot It", "Dos", "Código Secreto")
    val instrumentos = listOf("Guitarra", "Ukelele")
    val balones = listOf("Voleibol", "Fútbol", "Micro", "Baloncesto")
    val deportes = listOf("Fútbol", "Baloncesto", "Voleibol", "Pin Pon")
    val canchas = listOf("Claustro", "Carrera 13", "Mesa Pin Pon 1", "Mesa Pin Pon 2")

    val tipo = remember { mutableStateOf("") }
    val fecha = remember { mutableStateOf("") }
    val hora = remember { mutableStateOf("") }
    val juego = remember { mutableStateOf("") }
    val instrumento = remember { mutableStateOf("") }
    val balon = remember { mutableStateOf("") }
    val deporte = remember { mutableStateOf("") }
    val cancha = remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(BlueInstitutional)) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(0.8f),
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier.padding(36.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("RESERVA", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(30.dp))

            DropdownField("Tipo", tipoList, tipo.value) { tipo.value = it }
            Spacer(modifier = Modifier.height(10.dp))

            FechaPickerField(fecha.value) { fecha.value = it }
            Spacer(modifier = Modifier.height(10.dp))

            DropdownField("Hora", horas, hora.value) { hora.value = it }

            when (tipo.value) {
                "Juego de mesa" -> DropdownField("Juego", juegos, juego.value) { juego.value = it }
                "Instrumento" -> DropdownField("Instrumento", instrumentos, instrumento.value) { instrumento.value = it }
                "Balón" -> DropdownField("Balón", balones, balon.value) { balon.value = it }
                "Cancha" -> {
                    DropdownField("Deporte", deportes, deporte.value) { deporte.value = it }
                    DropdownField("Cancha", canchas, cancha.value) { cancha.value = it }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (tipo.value.isBlank() || fecha.value.isBlank() || hora.value.isBlank() ||
                        (tipo.value == "Cancha" && (cancha.value.isBlank() || deporte.value.isBlank())) ||
                        (tipo.value == "Juego de mesa" && juego.value.isBlank()) ||
                        (tipo.value == "Instrumento" && instrumento.value.isBlank()) ||
                        (tipo.value == "Balón" && balon.value.isBlank())
                    ) {
                        Toast.makeText(context, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val fechaActual = Calendar.getInstance().time
                    val fechaSeleccionada = formatoFecha.parse(fecha.value)
                    if (fechaSeleccionada.before(fechaActual)) {
                        Toast.makeText(context, "No puedes seleccionar una fecha pasada", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    when (tipo.value) {
                        "Cancha" -> reservarCancha(db, context, fecha.value, hora.value, deporte.value, cancha.value)
                        "Juego de mesa" -> validarYReservarObjeto(db, context, fecha.value, hora.value, juego.value, "Juego de mesa")
                        "Instrumento" -> validarYReservarObjeto(db, context, fecha.value, hora.value, instrumento.value, "Instrumento")
                        "Balón" -> validarYReservarObjeto(db, context, fecha.value, hora.value, balon.value, "Balón")
                    }
                },
                modifier = Modifier.width(190.dp).height(50.dp),
                shape = RectangleShape
            ) {
                Text("Reservar")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DropdownField(label: String, options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier
        .padding(vertical = 4.dp)
        .width(380.dp)
        .height(60.dp)) {
        TextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelected(option)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun FechaPickerField(fecha: String, onFechaSeleccionada: (String) -> Unit) {
    val context = LocalContext.current
    val calendario = Calendar.getInstance()
    val year = calendario.get(Calendar.YEAR)
    val month = calendario.get(Calendar.MONTH)
    val day = calendario.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember {
        DatePickerDialog(context, { _, y, m, d ->
            val fechaFormateada = String.format("%02d/%02d/%04d", d, m + 1, y)
            onFechaSeleccionada(fechaFormateada)
        }, year, month, day)
    }

    TextField(
        value = fecha,
        onValueChange = {},
        label = { Text("Fecha (dd/MM/yyyy)") },
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Image(
                    painter = painterResource( R.drawable.calendar),
                    contentDescription = null
                )
            }
        },
        modifier = Modifier.width(380.dp).height(60.dp)
    )
}

fun reservarCancha(db: FirebaseFirestore, context: android.content.Context, fecha: String, hora: String, deporte: String, cancha: String) {
    val docRef = db.collection("reservas")
        .whereEqualTo("tipo", "Cancha")
        .whereEqualTo("fecha", fecha)
        .whereEqualTo("hora", hora)
        .whereEqualTo("cancha", cancha)

    val firebaseAuth = FirebaseAuth.getInstance()
    docRef.get().addOnSuccessListener { result ->
        if (result.isEmpty) {
            val reserva = hashMapOf(
                "tipo" to "Cancha",
                "fecha" to fecha,
                "hora" to hora,
                "deporte" to deporte,
                "cancha" to cancha,
                "timestamp" to Timestamp.now(),
                "uid" to firebaseAuth.currentUser?.uid
            )

            db.collection("reservas")
                .add(reserva)
                .addOnSuccessListener {
                    Toast.makeText(context, "Reserva de cancha exitosa", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al reservar cancha", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Esta cancha ya está reservada en ese horario", Toast.LENGTH_SHORT).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Error al verificar disponibilidad de la cancha", Toast.LENGTH_SHORT).show()
    }
}

fun validarYReservarObjeto(db: FirebaseFirestore, context: android.content.Context, fecha: String, hora: String, nombre: String, tipo: String) {
    val inventarioRef = db.collection("inventario").document(nombre)
    val firebaseAuth = FirebaseAuth.getInstance()

    inventarioRef.get().addOnSuccessListener { document ->
        val cantidadDisponible = document.getLong("cantidadDisponible") ?: 0L

        if (cantidadDisponible > 0) {
            val reserva = hashMapOf(
                "tipo" to tipo,
                "fecha" to fecha,
                "hora" to hora,
                "uid" to firebaseAuth.currentUser?.uid,
                (if (tipo == "Instrumento") "instrumento" else if (tipo == "Balón") "balon" else "juego") to nombre,
                "timestamp" to Timestamp.now()
            )

            db.collection("reservas")
                .add(reserva)
                .addOnSuccessListener {
                    inventarioRef.update("cantidadDisponible", cantidadDisponible - 1)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Reserva de $tipo exitosa", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Reserva realizada, pero error al actualizar inventario", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al reservar $tipo", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "$tipo no disponible para reservar", Toast.LENGTH_SHORT).show()
        }
    }.addOnFailureListener {
        Toast.makeText(context, "Error al consultar inventario de $tipo", Toast.LENGTH_SHORT).show()
    }
}
