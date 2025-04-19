@file:OptIn(ExperimentalMaterial3Api::class)

package com.ucat.servicios_ucat

import android.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ucat.servicios_ucat.ui.theme.BlueButton
import com.ucat.servicios_ucat.ui.theme.BlueInstitutional
import kotlinx.coroutines.launch

@Composable
fun PantallaConDrawer(
    drawerContent: @Composable () -> Unit,
    contenidoPrincipal: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                drawerContent()
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Servicios UCAT") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF2C80C1),
                        titleContentColor = BlueButton
                    )
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                contenidoPrincipal()
            }
        }
    }
}
@Composable
fun DrawerContent(
    onReservar: () -> Unit,
    onMisReservas: () -> Unit,
    onHorarios: () -> Unit,
    onAyuda: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("INICIO", modifier = Modifier.padding(8.dp))
        Divider()
        DrawerItem("Reservar", onReservar)
        DrawerItem("Mis Reservas", onMisReservas)
        DrawerItem("Horarios", onHorarios)
        DrawerItem("Ayuda", onAyuda)
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        DrawerItem("Cerrar Sesión", onCerrarSesion)
    }
}

@Composable
fun DrawerItem(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text, fontSize = 18.sp)
    }
}
