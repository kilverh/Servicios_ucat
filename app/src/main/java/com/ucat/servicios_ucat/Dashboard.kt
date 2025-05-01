package com.ucat.servicios_ucat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ucat.servicios_ucat.ui.theme.*

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    onIrAReservar: () -> Unit,
    onIrAGestionarReservas: () -> Unit,
    onIrAAyuda: () -> Unit,
    onIrACerrar: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(
        brush = Brush.linearGradient(
            colors = listOf(
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
            //Botones para ir a los diferentes modulos o salir de la cuenta
            Button(
                onClick = onIrAReservar,
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                shape = RectangleShape
            ) {
                Text("RESERVAR")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onIrAGestionarReservas,
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                shape = RectangleShape
            ) {
                Text("MIS RESERVAS")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onIrAAyuda,
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                shape = RectangleShape
            ) {
                Text("AYUDA")
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onIrACerrar,
                modifier = Modifier
                    .width(380.dp)
                    .height(60.dp),
                shape = RectangleShape
            ) {
                Text("CERRAR SESIÓN")
            }
        }
    }
}