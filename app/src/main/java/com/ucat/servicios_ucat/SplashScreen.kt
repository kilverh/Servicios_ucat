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
import com.ucat.servicios_ucat.ui.theme.BlueInstitutional
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (logo, versionText) = createRefs()


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
                bottom.linkTo(parent.bottom, margin = 84.dp)
                centerHorizontallyTo(parent)
            }
        )
    }
}

@Preview(
    showSystemUi = true,
    heightDp = 800,
    widthDp = 400,
)
@Composable
private fun Prev()  {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxSize(),
        color = BlueInstitutional
    ) {
        SplashScreen()
    }
}
