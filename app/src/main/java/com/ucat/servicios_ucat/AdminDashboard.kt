package com.ucat.servicios_ucat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucat.servicios_ucat.ui.theme.BlueButton
import com.ucat.servicios_ucat.ui.theme.White

@Composable
fun AdminDashboard(
    onVerReservas: () -> Unit,
    onCerrarSesionAdmin: () -> Unit,
    onIrAjustesAdmin: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .offset(x = 0.dp, y = 60.dp)
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.FillWidth,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //dashboard administrador
            Text(
                text = "BIENVENIDO ADMINISTRADOR",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = White,
                modifier = Modifier.padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(66.dp))

            Button(
                onClick = onVerReservas,
                modifier = Modifier
                    .width(300.dp)
                    .height(70.dp)
                    .shadow(elevation = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueButton),
                shape = RectangleShape
            ) {
                Text(
                    "Ver Reservas",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = onIrAjustesAdmin,
                modifier = Modifier
                    .width(300.dp)
                    .height(70.dp)
                    .shadow(elevation = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueButton),
                shape = RectangleShape
            ) {
                Text(
                    "Ajustes",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = onCerrarSesionAdmin,
                modifier = Modifier
                    .width(300.dp)
                    .height(70.dp)
                    .shadow(elevation = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueButton),
                shape = RectangleShape
            ) {
                Text(
                    "Cerrar Sesión",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}