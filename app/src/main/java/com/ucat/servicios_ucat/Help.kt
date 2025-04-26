@file:OptIn(ExperimentalMaterial3Api::class)

package com.ucat.servicios_ucat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun Help(
    modifier: Modifier = Modifier,
    onReservaExitosa: ()-> Unit
) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var selectedProblem by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val problemOptions = listOf("Acceso", "Reservas", "Horarios", "Otro")
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(BlueInstitutional)) {
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
                .padding(36.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AYUDA",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedProblem,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de problema") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        problemOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedProblem = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            TextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Describe tu problema") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(bottom = 24.dp)
            )

            Button(
                onClick = {
                    if (selectedProblem.isBlank() || description.isBlank()) {
                        Toast.makeText(
                            context,
                            "Todos los campos son obligatorios",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    val problema = hashMapOf(
                        "tipo" to selectedProblem,
                        "descripcion" to description,
                        "uid" to firebaseAuth.currentUser?.uid,
                        "timestamp" to System.currentTimeMillis()
                    )

                    firestore.collection("problemas")
                        .add(problema)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Tu problema será revisado", Toast.LENGTH_SHORT)
                                .show()
                            selectedProblem = ""
                            description = ""
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Error al guardar el problema",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                modifier = Modifier
                    .width(190.dp)
                    .height(50.dp)
            ) {
                Text("ENVIAR", color = Color.White)
            }
        }
    }
}
