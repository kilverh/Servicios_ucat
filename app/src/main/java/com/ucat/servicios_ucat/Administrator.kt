package com.ucat.servicios_ucat

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.ucat.servicios_ucat.ui.theme.DarkGrey
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class ReservaConCodigo(
    val id: String = "",
    val tipo: String = "",
    val recurso: String = "",
    val hora: String = "",
    val codigo: String? = null
)

@Composable
//función para ver todas las reservas por día
fun VerReservas( onVolverMenu: () -> Unit) {
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
                            codigo = document.getString("codigo")
                        )
                    )
                }
                reservasDelDia = reservas
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Error al cargar las reservas: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                reservasDelDia = emptyList()
            }
    }

    fun eliminarReserva(reservaId: String) {
        firestore.collection("reservas").document(reservaId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show()
                obtenerReservasPorFecha(formattedDate) // Recargar las reservas después de eliminar
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al eliminar la reserva: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    Box(
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
    )
    {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 0.dp, y = 0.dp)
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.FillWidth,
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 36.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onVolverMenu) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver al menú", tint = Color.White)
                }
                Text(
                    "RESERVAS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Date Picker
            val year = selectedDate.get(Calendar.YEAR)
            val month = selectedDate.get(Calendar.MONTH)
            val dayOfMonth = selectedDate.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
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
            Spacer(modifier = Modifier.height(16.dp))
            if (reservasDelDia.isEmpty()) {
                Text("No hay reservas para este día.", color = Color.White)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reservasDelDia) { reserva ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = cardColors(DarkGrey)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Tipo: ${reserva.tipo}", color = Color.White)
                                        Text("Recurso: ${reserva.recurso}", color = Color.White)
                                        Text("Hora: ${reserva.hora}", color = Color.White)
                                        reserva.codigo?.let {
                                            Text("Código: ${it}", color = Color.White)
                                        }
                                    }
                                    IconButton(onClick = { eliminarReserva(reserva.id) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                                    }
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
}
