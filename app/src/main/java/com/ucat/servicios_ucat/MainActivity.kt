package com.ucat.servicios_ucat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    var mostrarSplash by remember { mutableStateOf(true) }
    var mostrarLogin by remember { mutableStateOf(false) }
    var mostrarDashboard by remember { mutableStateOf(false) }
    var mostrarRecuperar by remember { mutableStateOf(false) }
    var mostrarReserva by remember { mutableStateOf(false) }
    var mostrarGestionReservas by remember { mutableStateOf(false) }
    var mostrarAyuda by remember { mutableStateOf(false) }
    var mostrarAjustesCuenta by remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var nombreUsuario by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    nombreUsuario = document.getString("nombre")
                }
                .addOnFailureListener {
                    nombreUsuario = "Invitado"
                }
        }
    }

    LaunchedEffect(Unit) {
        delay(3000)
        mostrarSplash = false
        mostrarLogin = true
    }

    Scaffold(
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
            ),
        containerColor = Color.Transparent
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
                            mostrarAyuda = true
                        },
                        onCerrarSesion = {
                            mostrarReserva = false
                            mostrarLogin = true
                        },
                        onAjustesCuenta = { // Nuevo lambda
                            mostrarReserva = false
                            mostrarAjustesCuenta = true
                        }
                    )
                },
                contenidoPrincipal = {
                    Booking(
                        onReservaExitosa = {
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
                            mostrarAyuda = true
                        },
                        onCerrarSesion = {
                            mostrarGestionReservas = false
                            mostrarLogin = true
                        },
                        onAjustesCuenta = { // Nuevo lambda
                            mostrarGestionReservas = false
                            mostrarAjustesCuenta = true
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
                        onAyuda = {},
                        onCerrarSesion = {
                            mostrarAyuda = false
                            mostrarLogin = true
                        },
                        onAjustesCuenta = { // Nuevo lambda
                            mostrarAyuda = false
                            mostrarAjustesCuenta = true
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
                        },
                        onAjustesCuenta = { // Nuevo lambda
                            mostrarDashboard = false
                            mostrarAjustesCuenta = true
                        }
                    )
                },
                contenidoPrincipal = {
                    DashboardContent(
                        nombreUsuario = nombreUsuario,
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
                        },
                        onIrAAjustesCuenta = {
                            mostrarDashboard = false
                            mostrarAjustesCuenta = true
                        }
                    )
                }
            )

            mostrarAjustesCuenta -> PantallaConDrawer( // Envuelve AccountSettingsScreen
                drawerContent = {
                    DrawerContent(
                        onReservar = { mostrarAjustesCuenta = false; mostrarReserva = true },
                        onMisReservas = { mostrarAjustesCuenta = false; mostrarGestionReservas = true },
                        onAyuda = { mostrarAjustesCuenta = false; mostrarAyuda = true },
                        onCerrarSesion = { mostrarAjustesCuenta = false; mostrarLogin = true },
                        onAjustesCuenta = {} // Ya estamos aquí, no hacemos nada al clickear
                    )
                },
                contenidoPrincipal = {
                    AccountSettingsScreen(
                        onVolverAlDashboard = {
                            mostrarAjustesCuenta = false
                            mostrarDashboard = true
                        },
                        onCerrarSesion = {
                            mostrarAjustesCuenta = false
                            mostrarLogin = true
                        }
                    )
                }
            )

            else -> RegistroScreen(
                modifier = Modifier
                    .padding(innerPadding),
                onIrALogin = { mostrarLogin = true }
            )
        }
    }
}
