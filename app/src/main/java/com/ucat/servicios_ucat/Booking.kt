package com.ucat.servicios_ucat



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@Composable
fun Booking(
    modifier: Modifier = Modifier,
    onReservaExitosa: () -> Unit
) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val (tipo, setTipo) = remember { mutableStateOf("") }
    val (fecha, setFecha) = remember { mutableStateOf("") }
    val (hora, setHora) = remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Reservar")

        OutlinedTextField(
            value = tipo,
            onValueChange = setTipo,
            label = { Text("Tipo de recurso") }
        )

        OutlinedTextField(
            value = fecha,
            onValueChange = setFecha,
            label = { Text("Fecha (ej: 2025-05-16)") }
        )

        OutlinedTextField(
            value = hora,
            onValueChange = setHora,
            label = { Text("Hora (ej: 14:00 - 15:00)") }
        )

        Button(onClick = {
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
        }) {
            Text("Confirmar Reserva")
        }
    }
}
