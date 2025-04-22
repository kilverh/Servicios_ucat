package com.ucat.servicios_ucat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.FirebaseApp
import com.ucat.servicios_ucat.ui.theme.BlueInstitutional
import com.ucat.servicios_ucat.ui.theme.Servicios_ucatTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            Servicios_ucatTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    var mostrarSplash by remember { mutableStateOf(true) }
    var mostrarLogin by remember { mutableStateOf(false) }
    var mostrarDashboard by remember { mutableStateOf(false) }
    var mostrarRecuperar by remember { mutableStateOf(false) }
    var mostrarReserva by remember { mutableStateOf(false) }
    var mostrarGestionReservas by remember { mutableStateOf(false) }
    var mostrarAyuda by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        delay(3000)
        mostrarSplash = false
        mostrarLogin = true
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BlueInstitutional
    ) { innerPadding ->
    when {
        mostrarSplash -> SplashScreen()

        mostrarLogin -> Login(
            onLoginExitoso = {
                mostrarLogin = false
                mostrarDashboard = true
            },
            onIrARegistro = { mostrarLogin = false },
            onError = {},
            onRecuperar = {
                mostrarLogin = false
                mostrarRecuperar = true
            }
        )

        mostrarRecuperar -> Recover(
            onVolverAlLogin = {
                mostrarRecuperar = false
                mostrarLogin = true
            }
        )

        mostrarReserva -> PantallaConDrawer(
            drawerContent = {
                DrawerContent(
                    onReservar = {},
                    onMisReservas = {
                        mostrarReserva = false
                        mostrarGestionReservas = true
                    },
                    onAyuda = {
                        mostrarReserva = false
                        mostrarGestionReservas = false
                        mostrarDashboard = false
                        mostrarAyuda = true},
                    onCerrarSesion = {
                        mostrarReserva = false
                        mostrarLogin = true
                    }
                )
            },
            contenidoPrincipal = {
                Booking(
                    onReservaExitosa = {
                        mostrarReserva = false
                        mostrarDashboard = true
                    },
                    onVolverAlMenu = {
                        mostrarReserva = false
                        mostrarDashboard = true
                    }
                )
            }
        )

        mostrarGestionReservas -> PantallaConDrawer(
            drawerContent = {
                DrawerContent(
                    onReservar = {
                        mostrarGestionReservas = false
                        mostrarReserva = true
                    },
                    onMisReservas = {},
                    onAyuda = {
                        mostrarReserva = false
                        mostrarGestionReservas = false
                        mostrarDashboard = false
                        mostrarAyuda = true},
                    onCerrarSesion = {
                        mostrarGestionReservas = false
                        mostrarLogin = true
                    }
                )
            },
            contenidoPrincipal = {
                ManageBookings(
                    onVolverAlMenu = {
                        mostrarGestionReservas = false
                        mostrarDashboard = true
                    }
                )
            }
        )
        mostrarAyuda -> PantallaConDrawer(
            drawerContent = {
                DrawerContent(
                    onReservar = {
                        mostrarAyuda = false
                        mostrarReserva = true
                    },
                    onMisReservas = {
                        mostrarAyuda = false
                        mostrarGestionReservas = true
                    },
                    onAyuda = {
                        mostrarReserva = false
                        mostrarGestionReservas = false
                        mostrarDashboard = false
                        mostrarAyuda = true
                              },
                    onCerrarSesion = {
                        mostrarAyuda = false
                        mostrarLogin = true
                    }
                )
            },
            contenidoPrincipal = {
                Help(
                    onReservaExitosa = {
                        mostrarAyuda = false
                        mostrarDashboard = true
                    }
                )
            }
        )
        mostrarDashboard -> PantallaConDrawer(
            drawerContent = {
                DrawerContent(
                    onReservar = {
                        mostrarDashboard = false
                        mostrarReserva = true
                    },
                    onMisReservas = {
                        mostrarDashboard = false
                        mostrarGestionReservas = true
                    },
                    onAyuda = {
                        mostrarReserva = false
                        mostrarGestionReservas = false
                        mostrarDashboard = false
                        mostrarAyuda = true
                    },
                    onCerrarSesion = {
                        mostrarDashboard = false
                        mostrarLogin = true
                    }
                )
            },
            contenidoPrincipal = {
                DashboardContent(
                    onIrAReservar = {
                        mostrarDashboard = false
                        mostrarReserva = true
                    },
                    onIrAGestionarReservas = {
                        mostrarDashboard = false
                        mostrarGestionReservas = true
                    },
                    onIrAAyuda = {
                        mostrarReserva = false
                        mostrarGestionReservas = false
                        mostrarDashboard = false
                        mostrarAyuda = true
                    },
                    onIrACerrar = {
                        mostrarDashboard = false
                        mostrarLogin = true
                    }
                )
            }
        )

        else -> RegistroScreen(
            modifier = Modifier.padding(innerPadding),
            onIrALogin = { mostrarLogin = true },
            onError = {}
        )
    }
    }
}
