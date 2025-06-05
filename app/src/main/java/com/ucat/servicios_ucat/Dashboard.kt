package com.ucat.servicios_ucat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardContent(
    nombre: String?,
    modifier: Modifier = Modifier,
    onIrAReservar: () -> Unit,
    onIrAGestionarReservas: () -> Unit,
    onIrAAyuda: () -> Unit,
    onIrACerrar: () -> Unit,
    onIrAAjustesCuenta: () -> Unit
){
    Box(modifier = Modifier.fillMaxSize().background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF042137),
                Color(0xFF2C80C1),
                Color(0xFF4C9BE3),
                Color(0xFF042137)
            )
        )
    )){
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            val displayNombre = if (!nombre.isNullOrEmpty()) " ${nombre.uppercase()}" else ""
            Text(
                text = "BIENVENID@$displayNombre",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = onIrAReservar,
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp)
                    .shadow(elevation = 16.dp),
                shape = RectangleShape
            ) {
                Text("RESERVAR")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onIrAGestionarReservas,
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp)
                    .shadow(elevation = 16.dp),
                shape = RectangleShape
            ) {
                Text("MIS RESERVAS")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onIrAAjustesCuenta,
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp)
                    .shadow(elevation = 16.dp),
                shape = RectangleShape
            ) {
                Text("AJUSTES DE CUENTA")
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onIrAAyuda,
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp)
                    .shadow(elevation = 16.dp),
                shape = RectangleShape
            ) {
                Text("AYUDA")
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onIrACerrar,
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp)
                    .shadow(elevation = 16.dp),
                shape = RectangleShape
            ) {
                Text("CERRAR SESIÓN")
            }
        }
    }
}
