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
    var mostrarDashboardEstudiante by remember { mutableStateOf(false) }
    var mostrarDashboardAdministrador by remember { mutableStateOf(false) }
    var mostrarVerReservasAdmin by remember { mutableStateOf(false) }
    var mostrarRecuperar by remember { mutableStateOf(false) }
    var mostrarReserva by remember { mutableStateOf(false) }
    var mostrarGestionReservas by remember { mutableStateOf(false) }
    var mostrarAyuda by remember { mutableStateOf(false) }
    var mostrarAjustesCuenta by remember { mutableStateOf(false) }
    var mostrarAjustesAdmin by remember { mutableStateOf(false) }
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
                onLoginExitosoEstudiante = {
                    mostrarLogin = false
                    mostrarDashboardEstudiante = true
                },
                onLoginExitosoAdmin = {
                    mostrarLogin = false
                    mostrarDashboardAdministrador = true
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
                            mostrarDashboardEstudiante = false
                            mostrarDashboardAdministrador = false
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
                            mostrarDashboardEstudiante = true
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
                            mostrarDashboardEstudiante = false
                            mostrarDashboardAdministrador = false
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
                            mostrarDashboardEstudiante = true
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
                        onAjustesCuenta = {
                            mostrarAyuda = false
                            mostrarAjustesCuenta = true
                        }
                    )
                },
                contenidoPrincipal = {
                    Help(
                        onReservaExitosa = {
                            mostrarAyuda = false
                            mostrarDashboardEstudiante = true
                        }
                    )
                }
            )

            mostrarDashboardEstudiante -> PantallaConDrawer(
                drawerContent = {
                    DrawerContent(
                        onReservar = { mostrarDashboardEstudiante = false; mostrarReserva = true },
                        onMisReservas = { mostrarDashboardEstudiante = false; mostrarGestionReservas = true },
                        onAyuda = { mostrarDashboardEstudiante = false; mostrarAyuda = true },
                        onCerrarSesion = {
                            auth.signOut()
                            mostrarDashboardEstudiante = false
                            mostrarLogin = true
                        },
                        onAjustesCuenta = { mostrarDashboardEstudiante = false; mostrarAjustesCuenta = true }
                    )
                },
                contenidoPrincipal = {
                    DashboardContent(
                        nombre = nombreUsuario,
                        onIrAReservar = { mostrarDashboardEstudiante = false; mostrarReserva = true },
                        onIrAGestionarReservas = { mostrarDashboardEstudiante = false; mostrarGestionReservas = true },
                        onIrAAyuda = { mostrarDashboardEstudiante = false; mostrarAyuda = true },
                        onIrACerrar = {
                            auth.signOut()
                            mostrarDashboardEstudiante = false
                            mostrarLogin = true
                        },
                        onIrAAjustesCuenta = { mostrarDashboardEstudiante = false; mostrarAjustesCuenta = true }
                    )
                }
            )

            mostrarDashboardAdministrador -> AdminDashboard(
                onVerReservas = {
                    mostrarDashboardAdministrador = false;
                    mostrarVerReservasAdmin = true
                },
                onIrAjustesAdmin = {
                    mostrarDashboardAdministrador = false;
                    mostrarAjustesAdmin = true
                },
                onCerrarSesionAdmin = {
                    auth.signOut()
                    mostrarDashboardAdministrador = false
                    mostrarLogin = true
                }
            )

            mostrarVerReservasAdmin -> VerReservas(
                onVolverMenu = {
                    mostrarVerReservasAdmin = false
                    mostrarDashboardAdministrador = true
                }
            ) // Mostrar el panel de ver reservas

            mostrarAjustesAdmin -> AdminSettingsScreen(
                onVolverAlDashboard = {
                    mostrarAjustesAdmin = false
                    mostrarDashboardAdministrador = true
                },
                onCerrarSesionAdmin = {
                    auth.signOut()
                    mostrarAjustesAdmin = false
                    mostrarLogin = true
                }
            )

            mostrarAjustesCuenta -> PantallaConDrawer(
                drawerContent = {
                    DrawerContent(
                        onReservar = { mostrarAjustesCuenta = false; mostrarReserva = true },
                        onMisReservas = { mostrarAjustesCuenta = false; mostrarGestionReservas = true },
                        onAyuda = { mostrarAjustesCuenta = false; mostrarAyuda = true },
                        onCerrarSesion = { mostrarAjustesCuenta = false; mostrarLogin = true },
                        onAjustesCuenta = {}
                    )
                },
                contenidoPrincipal = {
                    AccountSettingsScreen(
                        onVolverAlDashboard = {
                            mostrarAjustesCuenta = false
                            mostrarDashboardEstudiante = true
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
