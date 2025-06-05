package com.ucat.servicios_ucat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.* // Importar composables como remember, mutableStateOf, LaunchedEffect
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
    // Estados para controlar qué pantalla se muestra
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

    // Estado para el nombre del usuario
    var nombreUsuario by remember { mutableStateOf<String?>(null) }

    // Función para cargar el nombre del usuario desde Firestore
    val cargarNombreUsuario: (String?) -> Unit = { uid ->
        if (uid != null) {
            firestore.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    nombreUsuario = document.getString("nombre")
                }
                .addOnFailureListener {
                    nombreUsuario = "Invitado" // En caso de error, muestra "Invitado"
                }
        } else {
            nombreUsuario = null // Si no hay UID, el nombre es nulo
        }
    }

    // LaunchedEffect para manejar el estado de autenticación de Firebase
    // Se ejecuta cada vez que el usuario logueado cambia
    LaunchedEffect(auth.currentUser?.uid) {
        // Observa el UID del usuario actual. Cuando cambia (login/logout), este efecto se re-ejecuta.
        val uid = auth.currentUser?.uid
        cargarNombreUsuario(uid)
    }

    // LaunchedEffect para la lógica inicial de la app (splash screen y redirección)
    LaunchedEffect(Unit) {
        delay(3000) // Duración del Splash Screen
        mostrarSplash = false

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Si hay un usuario logueado, obtenemos su rol para redirigir
            firestore.collection("usuarios").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    val rol = document.getString("rol")
                    when (rol) {
                        "Estudiante" -> mostrarDashboardEstudiante = true
                        "Administrador" -> mostrarDashboardAdministrador = true
                        else -> mostrarLogin = true // Rol no reconocido, ir al login
                    }
                }
                .addOnFailureListener {
                    mostrarLogin = true // Error al obtener rol, ir al login
                }
        } else {
            mostrarLogin = true // No hay usuario logueado, ir al login
        }
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
                    // Después del login, fuerza la carga del nombre del nuevo usuario
                    cargarNombreUsuario(auth.currentUser?.uid)
                    mostrarDashboardEstudiante = true
                },
                onLoginExitosoAdmin = {
                    mostrarLogin = false
                    // Después del login, fuerza la carga del nombre del nuevo usuario
                    cargarNombreUsuario(auth.currentUser?.uid)
                    mostrarDashboardAdministrador = true
                },
                onIrARegistro = { mostrarLogin = false }, // Asumiendo que RegistroScreen es el 'else'
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
                            auth.signOut() // Cierra sesión de Firebase
                            nombreUsuario = null // Limpia el nombre al cerrar sesión
                            mostrarReserva = false
                            mostrarLogin = true
                        },
                        onAjustesCuenta = {
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
                            auth.signOut()
                            nombreUsuario = null
                            mostrarGestionReservas = false
                            mostrarLogin = true
                        },
                        onAjustesCuenta = {
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
                            auth.signOut()
                            nombreUsuario = null
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
                            nombreUsuario = null // Limpia el nombre al cerrar sesión
                            mostrarDashboardEstudiante = false
                            mostrarLogin = true
                        },
                        onAjustesCuenta = { mostrarDashboardEstudiante = false; mostrarAjustesCuenta = true }
                    )
                },
                contenidoPrincipal = {
                    DashboardContent(
                        nombre = nombreUsuario, // ¡Aquí se pasa el nombre observado!
                        onIrAReservar = { mostrarDashboardEstudiante = false; mostrarReserva = true },
                        onIrAGestionarReservas = { mostrarDashboardEstudiante = false; mostrarGestionReservas = true },
                        onIrAAyuda = { mostrarDashboardEstudiante = false; mostrarAyuda = true },
                        onIrACerrar = {
                            auth.signOut()
                            nombreUsuario = null // Limpia el nombre al cerrar sesión
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
                    nombreUsuario = null // Limpia el nombre al cerrar sesión
                    mostrarDashboardAdministrador = false
                    mostrarLogin = true
                }
            )

            mostrarVerReservasAdmin -> VerReservas(
                onVolverMenu = {
                    mostrarVerReservasAdmin = false
                    mostrarDashboardAdministrador = true
                }
            )

            mostrarAjustesAdmin -> AdminSettingsScreen(
                onVolverAlDashboard = {
                    mostrarAjustesAdmin = false
                    mostrarDashboardAdministrador = true
                },
                onCerrarSesionAdmin = {
                    auth.signOut()
                    nombreUsuario = null
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
                        onCerrarSesion = {
                            auth.signOut()
                            nombreUsuario = null
                            mostrarAjustesCuenta = false
                            mostrarLogin = true
                        },
                        onAjustesCuenta = {}
                    )
                },
                contenidoPrincipal = {
                    AccountSettingsScreen(
                        onCerrarSesion = {
                            auth.signOut()
                            nombreUsuario = null
                            mostrarAjustesCuenta = false
                            mostrarLogin = true
                        }
                    )
                }
            )

            else -> RegistroScreen(
                modifier = Modifier
                    .padding(innerPadding),
                onIrALogin = {
                    mostrarLogin = true
                    nombreUsuario = null // Limpia el nombre al ir al login desde registro
                }
            )
        }
    }
}