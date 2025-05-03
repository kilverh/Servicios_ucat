package com.ucat.servicios_ucat

import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ReservaConCodigo(
    val id: String = "",
    val tipo: String = "",
    val recurso: String = "",
    val hora: String = "",
    val codigoEstudiante: String? = null // Incluimos el código del estudiante
)

@Composable
fun VerReservasPorDia() {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var reservasDelDia by remember { mutableStateOf<List<ReservaConCodigo>>(emptyList()) }

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormatter.format(selectedDate.time)

    fun obtenerReservasPorFecha(fecha: String) {
        firestore.collection("reservas")
            .whereEqualTo("fecha", fecha)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val reservas = mutableListOf<ReservaConCodigo>()
                for (document in querySnapshot) {
                    reservas.add(
                        ReservaConCodigo(
                            id = document.id,
                            tipo = document.getString("tipo") ?: "",
                            recurso = when (document.getString("tipo")) {
                                "Juego de mesa" -> document.getString("juego") ?: ""
                                "Instrumento" -> document.getString("instrumento") ?: ""
                                "Balón" -> document.getString("balon") ?: ""
                                "Cancha" -> document.getString("cancha") ?: ""
                                else -> document.getString("recurso") ?: ""
                            },
                            hora = document.getString("hora") ?: "",
                            codigoEstudiante = document.getString("codigoUsuario") // Asumiendo que tienes este campo
                        )
                    )
                }
                reservasDelDia = reservas
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al cargar las reservas: ${e.message}", Toast.LENGTH_SHORT).show()
                reservasDelDia = emptyList()
            }
    }

    Column(
        modifier = Modifier
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
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Reservas por Día",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Date Picker
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, yearSelected, monthSelected, dayOfMonthSelected ->
                selectedDate.set(yearSelected, monthSelected, dayOfMonthSelected)
                obtenerReservasPorFecha(dateFormatter.format(selectedDate.time))
            },
            year,
            month,
            dayOfMonth
        )

        Button(onClick = { datePickerDialog.show() }) {
            Text("Seleccionar Fecha: $formattedDate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Reservas para el $formattedDate",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (reservasDelDia.isEmpty()) {
            Text("No hay reservas para este día.", color = Color.White)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(reservasDelDia) { reserva ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF37474F)) // Un gris más oscuro
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Tipo: ${reserva.tipo}", color = Color.White)
                            Text("Recurso: ${reserva.recurso}", color = Color.White)
                            Text("Hora: ${reserva.hora}", color = Color.White)
                            reserva.codigoEstudiante?.let {
                                Text("Código: $it", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        obtenerReservasPorFecha(formattedDate)
    }
}