@file:OptIn(ExperimentalMaterial3Api::class)
package com.ucat.servicios_ucat

import android.util.Log
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
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ucat.servicios_ucat.ui.theme.DarkGrey
import java.text.SimpleDateFormat
import java.util.*

data class Reserva(
    val id: String = "",
    val tipo: String = "",
    val recurso: String = "",
    val fecha: String = "",
    val hora: String = "",
    val deporte: String? = null
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
    val horasDisponibles = listOf(
        "08:00",
        "09:00",
        "10:00",
        "11:00",
        "12:00",
        "14:00",
        "15:00",
        "16:00",
        "17:00",
        "18:00",
        "19:00"
    )
    var isLoadingReservas by remember { mutableStateOf(true) }
    fun cargarReservas() {
        isLoadingReservas = true
        //Conexion con la tabla en la base de datos de las reservas
        db.collection("reservas")
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { result ->
                println("Consulta de reservas exitosa, número de documentos: ${result.size()}") // Agregado para debugging
                val fetchedReservas = result.documents.map { document ->
                    println("Documento encontrado: ${document.id} - ${document.data}") // Agregado para debugging
                    val tipo = document.getString("tipo") ?: ""
                    val nombreRecurso = when (tipo) {
                        "Juego de mesa" -> document.getString("juego") ?: ""
                        "Instrumento" -> document.getString("instrumento") ?: ""
                        "Balón" -> document.getString("balon") ?: ""
                        "Cancha" -> document.getString("cancha") ?: ""
                        else -> ""
                    }
                    Reserva(
                        id = document.id,
                        tipo = tipo,
                        recurso = nombreRecurso,
                        fecha = document.getString("fecha") ?: "",
                        hora = document.getString("hora") ?: ""
                    )
                }

                // Formateador para comparar fechas y horas
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val now = Calendar.getInstance()

                // Filtrar y eliminar reservas pasadas
                val futurasReservas = fetchedReservas.filter { reserva ->
                    try {
                        val reservaDateTime =
                            dateFormatter.parse("${reserva.fecha} ${reserva.hora}")
                        reservaDateTime?.after(now.time)
                            ?: true // Mantener si la fecha no se puede parsear
                    } catch (e: Exception) {
                        true
                    }
                }.toMutableList()
                fetchedReservas.forEach { reserva ->
                    try {
                        val reservaDateTime =
                            dateFormatter.parse("${reserva.fecha} ${reserva.hora}")
                        if (reservaDateTime?.before(now.time) == true) {
                            db.collection("reservas").document(reserva.id).delete()
                                .addOnSuccessListener {
                                    println("Reserva pasada eliminada: ${reserva.id}")
                                }
                                .addOnFailureListener { e ->
                                    println("Error al eliminar reserva pasada ${reserva.id}: $e")
                                }
                        }
                    } catch (e: Exception) {
                        println("Error al parsear fecha para eliminación: ${reserva.id} - ${e.message}")
                    }
                }

                // Ordenar las reservas futuras: primero la fecha actual, luego las futuras
                val hoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                futurasReservas.sortWith(compareBy<Reserva> {
                    try {
                        dateFormatter.parse("${it.fecha} ${it.hora}")
                    } catch (e: Exception) {
                        Date(Long.MAX_VALUE) // Si no se puede parsear, poner al final
                    }
                }.thenBy { it.hora })

                val reservasHoy = futurasReservas.filter { it.fecha == hoy }
                val reservasFuturas = futurasReservas.filter { it.fecha != hoy }

                reservas = reservasHoy + reservasFuturas
                println("Lista de reservas cargada: $reservas")
                isLoadingReservas = false
            }
            .addOnFailureListener { e ->
                println("Error al cargar las reservas: ${e.message}")
                isLoadingReservas = false
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

            if (isLoadingReservas) {
                CircularProgressIndicator(color = Color.White)
            } else if (reservas.isEmpty() && reservaEdit == null) {
                Text("No hay reservas actualmente.", color = Color.White, fontSize = 18.sp)
            } else if (reservaEdit == null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(reservas) { reserva ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = cardColors(DarkGrey)
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
                                    //Boton de reservas y eliminar
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

                // Estados separados para el recurso y el deporte en la edición
                var recursoEditado by remember { mutableStateOf(reservaEdit?.recurso ?: "") }
                var deporteEditado by remember {
                    mutableStateOf(reservaEdit?.let {
                        if (it.tipo == "Cancha") it.deporte ?: "" else ""
                    } ?: "")
                }

                // Estado para la lista de recursos filtrada por el tipo seleccionado
                var recursosDisponiblesEdit by remember { mutableStateOf(listOf<String>()) }
                var canchasDisponiblesEdit by remember { mutableStateOf(listOf<String>()) }
                val recursosMostrar = when (tipoEdit) {
                    "Cancha" -> canchasDisponiblesEdit
                    else -> recursosDisponiblesEdit
                }

                // Efecto para inicializar los campos de edición cuando se selecciona una reserva
                LaunchedEffect(reservaEdit) {
                    reservaEdit?.let {
                        tipoEdit = it.tipo
                        fechaEdit = it.fecha
                        horaEdit = it.hora
                        recursoEditado = it.recurso
                        deporteEditado = it.deporte ?: ""

                        // Este llamado ya es suficiente, evita que se dispare el otro LaunchedEffect
                        cargarRecursos(it.tipo) { recursos, canchas ->
                            println("Recursos disponibles para edición: $recursosDisponiblesEdit")
                            recursosDisponiblesEdit = recursos
                            canchasDisponiblesEdit = canchas
                        }
                    }
                }

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
                                        recursoEditado = "" // Reiniciar el recurso editado
                                        expandedTipo = false
                                        // Actualizar los recursos disponibles para el nuevo tipo
                                        cargarRecursos(tipo) { recursos, canchas ->
                                            Log.d(
                                                "ManageBookings",
                                                "Callback - Recursos recibidos para $tipo: recursos=$recursos, canchas=$canchas"
                                            )
                                            recursosDisponiblesEdit = recursos
                                            canchasDisponiblesEdit = canchas
                                        }
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

                    // RECURSO (Juego, Instrumento, Balón) - Mostrar solo si no es Cancha
                    if (tipoEdit != "Cancha") {
                        println("Recursos a mostrar: $recursosMostrar")
                        ExposedDropdownMenuBox(
                            expanded = expandedRecurso,
                            onExpandedChange = { expandedRecurso = !expandedRecurso }) {
                            TextField(
                                value = recursoEditado,
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text(
                                        when (tipoEdit) {
                                            "Juego de mesa" -> "Juego"
                                            "Instrumento" -> "Instrumento"
                                            "Balón" -> "Balón"
                                            else -> "Recurso"
                                        }
                                    )
                                },
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
                                recursosMostrar.forEach { recurso ->
                                    DropdownMenuItem(
                                        text = { Text(recurso) },
                                        onClick = {
                                            recursoEditado = recurso
                                            expandedRecurso = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Campos específicos para Cancha
                    if (tipoEdit == "Cancha") {
                        var canchasMostrarEdit by remember { mutableStateOf(listOf<String>()) }
                        var expandedDeporteEdit by remember { mutableStateOf(false) }
                        val deportes = listOf("Fútbol", "Baloncesto", "Voleibol", "Pin Pon")
                        var deporteEditadoLocal by remember { mutableStateOf(deporteEditado) }

                        // Actualizar canchasMostrarEdit cuando cambia deporteEditadoLocal
                        LaunchedEffect(deporteEditadoLocal) {
                            canchasMostrarEdit = when (deporteEditadoLocal) {
                                "Pin Pon" -> listOf("Mesa Pin Pon 1", "Mesa Pin Pon 2")
                                else -> listOf("Claustro", "Carrera 13")
                            }
                            // Si el deporte cambia, y el recurso editado actual no está en la nueva lista, resetearlo
                            if (recursoEditado !in canchasMostrarEdit) {
                                recursoEditado = ""
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = expandedDeporteEdit,
                            onExpandedChange = { expandedDeporteEdit = !expandedDeporteEdit }) {
                            TextField(
                                value = deporteEditadoLocal,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Deporte") },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(onClick = { expandedDeporteEdit = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = expandedDeporteEdit,
                                onDismissRequest = { expandedDeporteEdit = false }
                            ) {
                                deportes.forEach { deporte ->
                                    DropdownMenuItem(
                                        text = { Text(deporte) },
                                        onClick = {
                                            deporteEditadoLocal = deporte
                                            deporteEditado =
                                                deporte // Actualizar el estado original
                                            expandedDeporteEdit = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedRecurso,
                            onExpandedChange = { expandedRecurso = !expandedRecurso }) {
                            TextField(
                                value = recursoEditado,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Cancha") },
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
                                canchasMostrarEdit.forEach { cancha ->
                                    DropdownMenuItem(
                                        text = { Text(cancha) },
                                        onClick = {
                                            recursoEditado = cancha
                                            expandedRecurso = false
                                        }
                                    )
                                }
                            }
                        }
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
                                // Validación de campos vacíos
                                if (fechaEdit.isBlank() || horaEdit.isBlank() || recursoEditado.isBlank() || (tipoEdit == "Cancha" && deporteEditado.isBlank() && recursoEditado.isBlank())) {
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
                                    "Juego de mesa" -> data["juego"] = recursoEditado
                                    "Instrumento" -> data["instrumento"] = recursoEditado
                                    "Balón" -> data["balon"] = recursoEditado
                                    "Cancha" -> {
                                        data["cancha"] = recursoEditado
                                        data["deporte"] = deporteEditado
                                    }

                                    else -> data["recurso"] = recursoEditado
                                }

                                reservaEdit?.let { reserva ->
                                    db.collection("reservas").document(reserva.id)
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
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                context,
                                                "Error al actualizar: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            },
                            shape = RectangleShape
                        ) {
                            Text("Guardar cambios")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                reservaEdit = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray
                            ),
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
fun cargarRecursos(tipo: String, onRecursosCargados: (List<String>, List<String>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    Log.d("ManageBookings", "Cargando recursos para el tipo: $tipo")
    db.collection("inventario")
        .whereEqualTo("tipo", tipo)
        .get()
        .addOnSuccessListener { result ->
            val recursos = mutableListOf<String>()
            val canchas = mutableListOf<String>()
            for (document in result) {
                Log.d("ManageBookings", "Documento encontrado: ${document.id} con tipo: ${document.getString("tipo")}")
                when (tipo) {
                    "Juego de mesa", "Instrumento", "Balón" -> recursos.add(document.id)
                    "Cancha" -> canchas.add(document.id)
                }
            }
            Log.d("ManageBookings", "Recursos cargados para $tipo: recursos=$recursos, canchas=$canchas")
            onRecursosCargados(recursos, canchas)
        }
        .addOnFailureListener { e ->
            Log.e("ManageBookings", "Error al cargar recursos para $tipo: ${e.message}")
            onRecursosCargados(emptyList(), emptyList())
        }
}
