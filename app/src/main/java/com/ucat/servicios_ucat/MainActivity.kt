package com.ucat.servicios_ucat

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.FirebaseApp
import com.ucat.servicios_ucat.ui.theme.BlueInstitutional
import com.ucat.servicios_ucat.ui.theme.Servicios_ucatTheme

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

    // Temporizador de 3 segundos (puedes cambiarlo)
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        mostrarSplash = false
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BlueInstitutional
    ) { innerPadding ->
        if (mostrarSplash) {
            SplashScreen(modifier = Modifier.padding(innerPadding))
        } else {
            RegistroScreen(
                modifier = Modifier.padding(innerPadding),
                onRegistroExitoso = {
                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}

