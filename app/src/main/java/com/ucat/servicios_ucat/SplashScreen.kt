package com.ucat.servicios_ucat


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (titleText, logo, versionText) = createRefs()

        Text(
            text = "SERVICIOS UCAT",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.constrainAs(titleText) {
                bottom.linkTo(logo.top, margin = 16.dp)
                centerHorizontallyTo(parent)
            }
        )

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.constrainAs(logo) {
                centerTo(parent)
            }
        )

        Text(
            text = "VERSIÓN 1.0",
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = Color.White,
            modifier = Modifier.constrainAs(versionText) {
                top.linkTo(logo.bottom, margin = 96.dp)
                centerHorizontallyTo(parent)
            }
        )
    }
}
