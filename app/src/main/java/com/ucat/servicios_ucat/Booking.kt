@file:OptIn(ExperimentalMaterial3Api::class)
package com.ucat.servicios_ucat

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
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
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Booking(
    modifier: Modifier = Modifier,
    onReservaExitosa: () -> Unit = {}
) {
    //Nanejo de variables.
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser

    val tipoList = listOf("Cancha", "Juego de mesa", "Instrumento", "Balón")
    val horas = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00")
    val juegos = listOf("UNO", "Monopoly", "Jenga", "Ajedrez", "Parqués 4 puestos", "Parqués 6 puestos", "Damas Chinas", "Rummi", "Clue", "Conecta 4", "Pictureka", "Astucia Naval", "Superbanco Colombia", "Invasión", "Scrabble", "Laberinto", "Risk", "Pasaporte al Mundo", "Domino", "Cranium", "Twister", "Cubeez", "Spot It", "Dos", "Código Secreto")
    val instrumentos = listOf("Guitarra", "Ukelele")
    val balones = listOf("Voleibol", "Fútbol", "Micro", "Baloncesto")
    val deportes = listOf("Fútbol", "Baloncesto", "Voleibol", "Pin Pon")

    val tipo = remember { mutableStateOf("") }
    val fecha = remember { mutableStateOf("") }
    val hora = remember { mutableStateOf("") }
    val juego = remember { mutableStateOf("") }
    val instrumento = remember { mutableStateOf("") }
    val balon = remember { mutableStateOf("") }
    val deporte = remember { mutableStateOf("") }
    val cancha = remember { mutableStateOf("") }
    val loading = remember { mutableStateOf(false) }


    val limpiarFormulario = {
        tipo.value = ""
        fecha.value = ""
        hora.value = ""
        juego.value = ""
        instrumento.value = ""
        balon.value = ""
        deporte.value = ""
        cancha.value = ""
    }

    fun attemptReservation() {
        if (currentUser == null) {
            Toast.makeText(context, "Debe iniciar sesión para reservar.", Toast.LENGTH_SHORT).show()
            return
        }

        if (tipo.value.isBlank() || fecha.value.isBlank() || hora.value.isBlank()) {
            Toast.makeText(context, "Por favor, complete todos los campos obligatorios.", Toast.LENGTH_SHORT).show()
            return
        }

        when (tipo.value) {
            "Juego de mesa" -> if (juego.value.isBlank()) { Toast.makeText(context, "Seleccione un juego.", Toast.LENGTH_SHORT).show(); return }
            "Instrumento" -> if (instrumento.value.isBlank()) { Toast.makeText(context, "Seleccione un instrumento.", Toast.LENGTH_SHORT).show(); return }
            "Balón" -> if (balon.value.isBlank()) { Toast.makeText(context, "Seleccione un balón.", Toast.LENGTH_SHORT).show(); return }
            "Cancha" -> {
                if (deporte.value.isBlank()) { Toast.makeText(context, "Seleccione un deporte.", Toast.LENGTH_SHORT).show(); return }
                if (cancha.value.isBlank()) { Toast.makeText(context, "Seleccione una cancha.", Toast.LENGTH_SHORT).show(); return }
            }
        }


        val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
        val ahora = Calendar.getInstance()

        try {
            val fechaSeleccionadaDate = formatoFecha.parse(fecha.value) ?: return
            val horaSeleccionadaStr = if (hora.value.length == 4) "0${hora.value}" else hora.value
            val horaSeleccionadaDate = formatoHora.parse(horaSeleccionadaStr) ?: return

            val calendarioSeleccionado = Calendar.getInstance().apply {
                time = fechaSeleccionadaDate
                set(Calendar.HOUR_OF_DAY, horaSeleccionadaDate.hours)
                set(Calendar.MINUTE, horaSeleccionadaDate.minutes)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            // Client-side date and time validation
            if (calendarioSeleccionado.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                Toast.makeText(context, "No se pueden hacer reservas los domingos", Toast.LENGTH_SHORT).show()
                return
            }
            if (calendarioSeleccionado.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                val horaSeleccionadaInt = calendarioSeleccionado.get(Calendar.HOUR_OF_DAY)
                if (horaSeleccionadaInt < 9 || horaSeleccionadaInt >= 15) {
                    Toast.makeText(
                        context,
                        "Los sábados solo se puede reservar de 9:00 a 14:00",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
            }

            val currentCalendar = Calendar.getInstance().apply {
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            currentCalendar.add(Calendar.MINUTE, 1)

            if (calendarioSeleccionado.before(currentCalendar)) {
                Toast.makeText(context, "Debes seleccionar una fecha y hora futura", Toast.LENGTH_SHORT).show()
                return
            }

            loading.value = true

            db.collection("reservas")
                .whereEqualTo("uid", currentUser.uid)
                .whereEqualTo("fecha", fecha.value)
                .get()
                .addOnSuccessListener { dailyBookings ->
                    val totalDailyBookings = dailyBookings.size()
                    Log.d("Booking", "Reservas del día para ${currentUser.uid}: $totalDailyBookings")

                    if (totalDailyBookings >= 5) {
                        Toast.makeText(context, "Ya tienes el límite de 5 reservas para hoy.", Toast.LENGTH_LONG).show()
                        loading.value = false
                        return@addOnSuccessListener
                    }

                    db.collection("reservas")
                        .whereEqualTo("uid", currentUser.uid)
                        .whereEqualTo("fecha", fecha.value)
                        .whereEqualTo("hora", hora.value)
                        .get()
                        .addOnSuccessListener { hourlyBookings ->
                            val totalHourlyBookings = hourlyBookings.size()
                            Log.d("Booking", "Reservas por hora para ${currentUser.uid} a las ${hora.value}: $totalHourlyBookings")

                            if (totalHourlyBookings >= 2) {
                                Toast.makeText(context, "Ya tienes el límite de 2 reservas activas para esta hora.", Toast.LENGTH_LONG).show()
                                loading.value = false
                                return@addOnSuccessListener
                            }

                            val onReservaFinalizada: (Boolean) -> Unit = { success ->
                                loading.value = false
                                if (success) {
                                    onReservaExitosa()
                                }
                            }

                            when (tipo.value) {
                                "Cancha" -> reservarCancha(
                                    db, context, fecha.value, hora.value, deporte.value, cancha.value, limpiarFormulario, { onReservaFinalizada(true) }, { loading.value = it }
                                )
                                "Juego de mesa" -> reservarObjeto(
                                    db, context, fecha.value, hora.value, juego.value, "Juego de mesa", limpiarFormulario, { onReservaFinalizada(true) }, { loading.value = it }
                                )
                                "Instrumento" -> reservarObjeto(
                                    db, context, fecha.value, hora.value, instrumento.value, "Instrumento", limpiarFormulario, { onReservaFinalizada(true) }, { loading.value = it }
                                )
                                "Balón" -> reservarObjeto(
                                    db, context, fecha.value, hora.value, balon.value, "Balón", limpiarFormulario, { onReservaFinalizada(true) }, { loading.value = it }
                                )
                            }

                        }.addOnFailureListener { e ->
                            Log.e("Booking", "Error al verificar reservas por hora: ${e.message}")
                            Toast.makeText(context, "Error al verificar reservas por hora: ${e.message}", Toast.LENGTH_SHORT).show()
                            loading.value = false
                        }
                }.addOnFailureListener { e ->
                    Log.e("Booking", "Error al verificar reservas diarias: ${e.message}")
                    Toast.makeText(context, "Error al verificar reservas diarias: ${e.message}", Toast.LENGTH_SHORT).show()
                    loading.value = false
                }

        } catch (e: Exception) {
            Log.e("Booking", "Error en la validación de fecha/hora o formato: ${e.message}")
            Toast.makeText(context, "Error en la validación de fecha/hora o formato.", Toast.LENGTH_SHORT).show()
            loading.value = false // Ensure loading is reset on error
        }
    }


    Box(modifier = modifier
        .fillMaxSize()
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF042137),
                    Color(0xFF2C80C1),
                    Color(0xFF4C9BE3),
                    Color(0xFF042137)
                )
            )
        )) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().alpha(0.8f),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier
                .padding(36.dp)
                .fillMaxSize()
                .background(Color.Transparent),
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
            //formulario reservas, lógica canchas
            when (tipo.value) {
                "Juego de mesa" -> DropdownField("Juego", juegos, juego.value) { juego.value = it }
                "Instrumento" -> DropdownField("Instrumento", instrumentos, instrumento.value) { instrumento.value = it }
                "Balón" -> DropdownField("Balón", balones, balon.value) { balon.value = it }
                "Cancha" -> {
                    DropdownField("Deporte", deportes, deporte.value) { deporte.value = it }

                    val canchasFiltradas = when (deporte.value) {
                        "Pin Pon" -> listOf("Mesa Pin Pon 1", "Mesa Pin Pon 2")
                        else -> listOf("Claustro", "Carrera 13")
                    }

                    if (deporte.value.isNotBlank()) {
                        DropdownField("Cancha", canchasFiltradas, cancha.value) { cancha.value = it }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    attemptReservation()
                },
                modifier = Modifier.width(190.dp).height(50.dp),
                shape = RectangleShape,
                enabled = !loading.value
            ) {
                if (loading.value) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                } else {
                    Text("Reservar")
                }
            }

        }
    }
}

@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .width(380.dp)
            .height(60.dp)
            .clickable { expanded = true }
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            enabled = false,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Image(
                        painterResource(if (expanded) R.drawable.arriba else R.drawable.abajo),
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
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

    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d ->
            val tempCalendar = Calendar.getInstance().apply {
                set(y, m, d)
            }
            if (tempCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                Toast.makeText(context, "No se pueden seleccionar domingos", Toast.LENGTH_SHORT).show()
            } else {
                val fechaFormateada = String.format("%02d/%02d/%04d", d, m + 1, y) // dd/MM/yyyy
                onFechaSeleccionada(fechaFormateada)
            }
        },
        year,
        month,
        day
    )

    Box(
        modifier = Modifier
            .width(380.dp)
            .height(60.dp)
            .clickable { datePickerDialog.show() }
    ) {
        TextField(
            value = fecha,
            onValueChange = {},
            label = { Text("Fecha (dd/mm/yyyy)") },
            readOnly = true,
            enabled = false,
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Image(
                        painter = painterResource(R.drawable.calendar),
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun reservarCancha(
    db: FirebaseFirestore,
    context: android.content.Context,
    fecha: String,
    hora: String,
    deporte: String,
    cancha: String,
    limpiarFormulario: () -> Unit,
    onFinish: () -> Unit,
    onLoadingChange: (Boolean) -> Unit
) {
    val docRef = db.collection("reservas")
        .whereEqualTo("tipo", "Cancha")
        .whereEqualTo("fecha", fecha)
        .whereEqualTo("hora", hora)
        .whereEqualTo("cancha", cancha)

    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser

    currentUser?.let { user ->
        db.collection("usuarios").document(user.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                val codigoEstudiante = documentSnapshot.getString("codigo")

                if (codigoEstudiante != null) {
                    docRef.get().addOnSuccessListener { result ->
                        if (result.isEmpty) {
                            val reserva = hashMapOf(
                                "tipo" to "Cancha",
                                "fecha" to fecha,
                                "hora" to hora,
                                "deporte" to deporte,
                                "cancha" to cancha,
                                "timestamp" to Timestamp.now(),
                                "uid" to user.uid,
                                "codigo" to codigoEstudiante
                            )

                            db.collection("reservas")
                                .add(reserva)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Reserva de cancha exitosa", Toast.LENGTH_SHORT).show()
                                    limpiarFormulario()
                                    onFinish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al guardar la reserva", Toast.LENGTH_SHORT).show()
                                    onLoadingChange(false)
                                }
                        } else {
                            Toast.makeText(context, "La cancha ya está reservada para esa hora", Toast.LENGTH_SHORT).show()
                            onLoadingChange(false)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "Error al verificar disponibilidad", Toast.LENGTH_SHORT).show()
                        onLoadingChange(false)
                    }
                } else {
                    Toast.makeText(context, "No se pudo obtener el código del usuario", Toast.LENGTH_SHORT).show()
                    onLoadingChange(false)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show()
                onLoadingChange(false)
            }
    } ?: run {
        Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        onLoadingChange(false)
    }
}

fun reservarObjeto(
    db: FirebaseFirestore,
    context: android.content.Context,
    fecha: String,
    hora: String,
    nombreObjeto: String,
    tipo: String,
    limpiarFormulario: () -> Unit,
    onFinish: () -> Unit,
    onLoadingChange: (Boolean) -> Unit,
    esAuxiliar: Boolean = false,
    codigoEstudiante: String? = null
) {
    val reservasRef = db.collection("reservas")
    val inventarioRef = db.collection("inventario").document(nombreObjeto)
    val firebaseAuth = FirebaseAuth.getInstance()
    val currentUser = firebaseAuth.currentUser

    currentUser?.let { user ->
        val obtenerCodigoEstudiante: (String) -> Unit = { codigo ->
            inventarioRef.get().addOnSuccessListener { document ->
                val cantidadDisponible = document.getLong("cantidadDisponible") ?: 0L

                val campoObjeto = when (tipo) {
                    "Instrumento" -> "instrumento"
                    "Balón" -> "balon"
                    else -> "juego"
                }

                reservasRef
                    .whereEqualTo("tipo", tipo)
                    .whereEqualTo("fecha", fecha)
                    .whereEqualTo("hora", hora)
                    .whereEqualTo(campoObjeto, nombreObjeto)
                    .get()
                    .addOnSuccessListener { resultadoReservas: QuerySnapshot ->
                        val cantidadReservadaEnHorario = resultadoReservas.size()

                        if (cantidadReservadaEnHorario < cantidadDisponible) {
                            val reserva = hashMapOf(
                                "tipo" to tipo,
                                "fecha" to fecha,
                                "hora" to hora,
                                "uid" to user.uid,
                                campoObjeto to nombreObjeto,
                                "timestamp" to Timestamp.now(),
                                "codigo" to codigo
                            )

                            reservasRef.add(reserva)
                                .addOnSuccessListener {
                                    if (!esAuxiliar) {
                                        Toast.makeText(context, "Reserva de $tipo ($nombreObjeto) exitosa", Toast.LENGTH_SHORT).show()
                                        limpiarFormulario()
                                        onFinish()
                                    }
                                    onLoadingChange(false)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Error al reservar $tipo", Toast.LENGTH_SHORT).show()
                                    onLoadingChange(false)
                                }
                        } else {
                            Toast.makeText(context, "$tipo ($nombreObjeto) no disponible en este horario", Toast.LENGTH_SHORT).show()
                            onLoadingChange(false)
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error al verificar disponibilidad de $tipo", Toast.LENGTH_SHORT).show()
                        onLoadingChange(false)
                    }
            }.addOnFailureListener {
                Toast.makeText(context, "Error al consultar inventario de $tipo", Toast.LENGTH_SHORT).show()
                onLoadingChange(false)
            }
        }

        if (codigoEstudiante != null) {
            obtenerCodigoEstudiante(codigoEstudiante)
        } else {
            db.collection("usuarios").document(user.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val codigo = documentSnapshot.getString("codigo")
                    if (codigo != null) {
                        obtenerCodigoEstudiante(codigo)
                    } else {
                        Toast.makeText(context, "No se pudo obtener el código del usuario", Toast.LENGTH_SHORT).show()
                        onLoadingChange(false)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Error al obtener información del usuario", Toast.LENGTH_SHORT).show()
                    onLoadingChange(false)
                }
        }
    } ?: run {
        Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        onLoadingChange(false)
    }
}
