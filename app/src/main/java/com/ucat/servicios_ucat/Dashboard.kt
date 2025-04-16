package com.ucat.servicios_ucat

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucat.servicios_ucat.ui.theme.*

@Composable
fun Dashboard(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SERVICIOS UCAT",
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 24.dp),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        MenuButton("RESERVAR") { /* TODO: Navegar a reservar */ }
        Spacer(modifier = Modifier.height(24.dp))
        MenuButton("MIS RESERVAS") { /* TODO: Navegar a mis reservas */ }
        Spacer(modifier = Modifier.height(24.dp))
        MenuButton("HORARIOS") { /* TODO: Navegar a horarios */ }
        Spacer(modifier = Modifier.height(24.dp))
        MenuButton("AYUDA") { /* TODO: Navegar a ayuda */ }
        Spacer(modifier = Modifier.height(24.dp))
        MenuButton("AJUSTES") { /* TODO: Navegar a ajustes */ }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(250.dp)
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RectangleShape
    ) {
        Text(text = text, color = BlueButton)
    }
}
