package com.ucat.servicios_ucat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucat.servicios_ucat.ui.theme.DarkGrey
import com.ucat.servicios_ucat.ui.theme.White

@Composable
fun AdminSettingsScreen(
    onVolverAlDashboard: () -> Unit,
    onCerrarSesionAdmin: () -> Unit
) {
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
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 0.dp, y = -65.dp)
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onVolverAlDashboard) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Volver al Dashboard", tint = White)
                }
                Text(
                    "AJUSTES DE ADMINISTRADOR",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = White,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))

            // Opciones de ajustes de administrador
            Button(
                onClick = { /* TODO: Implementar funcionalidad para gestionar usuarios */ },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGrey),
                shape = RectangleShape
            ) {
                Text("Gestionar Usuarios", fontSize = 18.sp, color = White)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO: Implementar funcionalidad para gestionar inventario */ },
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGrey),
                shape = RectangleShape
            ) {
                Text("Gestionar Inventario", fontSize = 18.sp, color = White)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Divider(color = White.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCerrarSesionAdmin,
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.7f)),
                shape = RectangleShape
            ) {
                Text("Cerrar Sesión", fontSize = 18.sp, color = White)
            }
        }
    }
}