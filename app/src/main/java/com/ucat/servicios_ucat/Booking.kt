@file:OptIn(ExperimentalMaterial3Api::class)

package com.ucat.servicios_ucat

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

@Composable
fun Booking(
    modifier: Modifier = Modifier,
    onReservaExitosa: () -> Unit,
    onVolverAlMenu: () -> Unit
) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val tipoRecursoList = listOf("Instrumentos", "Cancha para futbol","Cancha para baloncesto", "Cancha para voleyball", "Uno", "Monopoly", "Parques", "Ajedrez", "Jenga")
    val horaList = listOf("08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00","12:00 - 1:00", "1:00 - 2:00", "2:00 - 3:00", "3:00 - 4:00", "4:00 - 5:00")

    val (tipo, setTipo) = remember { mutableStateOf("") }
    val (fecha, setFecha) = remember { mutableStateOf("") }
    val (hora, setHora) = remember { mutableStateOf("") }

    val expandedTipo = remember { mutableStateOf(false) }
    val expandedHora = remember { mutableStateOf(false) }
    val showDatePicker = remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    if (showDatePicker.value) {
        DatePickerDialog(
            context,
            { _, y, m, d ->
                val fechaFormateada = "%04d-%02d-%02d".format(y, m + 1, d)
                setFecha(fechaFormateada)
                showDatePicker.value = false
            },
            year, month, day
        ).show()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .offset(y = (-65).dp)
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = modifier
                .padding(36.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("RESERVAR", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(40.dp))


            ExposedDropdownMenuBox(
                expanded = expandedTipo.value,
                onExpandedChange = { expandedTipo.value = !expandedTipo.value }
            ) {
                TextField(
                    value = tipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de recurso") },
                    modifier = Modifier.menuAnchor()
                        .width(380.dp)
                        .height(60.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedTipo.value,
                    onDismissRequest = { expandedTipo.value = false }
                ) {
                    tipoRecursoList.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                setTipo(it)
                                expandedTipo.value = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            TextField(
                value = fecha,
                onValueChange = { setFecha(it) },
                label = { Text("Fecha (ej: 2025-05-16)") },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker.value = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Hora
            ExposedDropdownMenuBox(
                expanded = expandedHora.value,
                onExpandedChange = { expandedHora.value = !expandedHora.value }
            ) {
                TextField(
                    value = hora,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora") },
                    modifier = Modifier.menuAnchor()
                        .width(380.dp)
                        .height(60.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedHora.value,
                    onDismissRequest = { expandedHora.value = false }
                ) {
                    horaList.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                setHora(it)
                                expandedHora.value = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Confirmar reserva
            Button(
                onClick = {
                    if (tipo.isBlank() || fecha.isBlank() || hora.isBlank()) {
                        Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val reserva = hashMapOf(
                        "tipo" to tipo,
                        "fecha" to fecha,
                        "hora" to hora,
                        "uid" to firebaseAuth.currentUser?.uid
                    )
                    firestore.collection("reservas")
                        .add(reserva)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Reserva guardada", Toast.LENGTH_SHORT).show()
                            onReservaExitosa()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al guardar reserva", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier
                    .width(190.dp)
                    .height(50.dp),
                shape = RectangleShape
            ) {
                Text("Confirmar Reserva")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Volver al menú
            Button(
                onClick = onVolverAlMenu,
                modifier = Modifier
                    .width(190.dp)
                    .height(50.dp),
                shape = RectangleShape
            ) {
                Text("Ir al menú")
            }
        }
    }
}