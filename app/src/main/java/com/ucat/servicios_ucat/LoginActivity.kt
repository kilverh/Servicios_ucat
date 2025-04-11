package com.ucat.servicios_ucat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout


@Composable
fun SConstraint(modifier: Modifier = Modifier){

    ConstraintLayout (modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 10.dp, horizontal = 24.dp)){
        val (box1) = createRefs()


        Box (modifier= Modifier
            .width(200.dp)
            .height(200.dp)
            .background(Color.Magenta)
            .constrainAs(box1) {
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
            ,contentAlignment = Alignment.Center){
            SText1(texto=1.toString())}
    }



}

@Composable
fun SText1(modifier: Modifier = Modifier, texto: String) {
    Box(modifier = Modifier.background(Color.LightGray)) {
        Text(text = texto, fontSize = 48.sp, fontWeight = FontWeight.Bold)
    }}


@Preview
@Composable
private fun TextoPrev() {
    SText1(texto="Texto")
}



@Preview(showSystemUi = true, heightDp = 800,widthDp = 400)
@Composable
private fun ConstraintPrev() {
    SConstraint()
}






@Composable
fun SBox(modifier: Modifier= Modifier, texto:String) {
    Box (modifier = Modifier
        .width(200.dp)
        .height(200.dp)
        .background(Color.Blue), contentAlignment = Alignment.Center){
        Text(text = texto,
            fontSize = 20.sp,
            color = Color.Red,
            textAlign = Center,
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .background(Color.Cyan))
    }

}

@Composable
fun SRow(modifier: Modifier = Modifier) {
    Row() {
        SBox(modifier = Modifier, texto = "1")
        Spacer(modifier = Modifier.width(20.dp))
        SBox(modifier = Modifier, texto = "3")
        Spacer(modifier = Modifier.width(20.dp))
        SBox(modifier = Modifier, texto = "5")
        Spacer(modifier = Modifier.width(20.dp))
        SBox(modifier = Modifier, texto = "7")
        Spacer(modifier = Modifier.width(20.dp))
        SBox(modifier = Modifier, texto = "9")
        Spacer(modifier = Modifier.width(20.dp))
        SBox(modifier = Modifier, texto = "11")
    }
}



@Composable
fun SColumn(modifier: Modifier = Modifier) {
    Column() {
        SBox(modifier = Modifier, texto = "2")
        Spacer(modifier = Modifier.height(20.dp))
        SBox(modifier = Modifier, texto = "4")
        Spacer(modifier = Modifier.height(20.dp))
        SBox(modifier = Modifier, texto = "6")
        Spacer(modifier = Modifier.height(20.dp))
        SBox(modifier = Modifier, texto = "8")
        Spacer(modifier = Modifier.height(20.dp))
        SBox(modifier = Modifier, texto = "10")
    }
}

@Composable
fun SMix(modifier: Modifier = Modifier) {
    Column() {
        Row() {
            SBox(modifier = Modifier, texto = "1,1")
            Spacer(modifier = Modifier.width(20.dp))
            SBox(modifier = Modifier, texto = "1,2")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row() {
            SBox(modifier = Modifier, texto = "2,1")
            Spacer(modifier = Modifier.width(20.dp))
            SBox(modifier = Modifier, texto = "2,2")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row() {
            SBox(modifier = Modifier, texto = "3,1")
            Spacer(modifier = Modifier.width(20.dp))
            SBox(modifier = Modifier, texto = "3,2")
        }
    }
}



@Preview(showSystemUi = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PrevSBoxTablet() {
    SColumn()
    SRow()
}

@Preview(device = "id:wearos_small_round", showSystemUi = true)
@Composable
private fun PrevSBoxWatch() {
    SBox(texto = "10")
}

@Preview(device = "spec:width=411dp,height=891dp", showSystemUi = true)
@Composable
private fun PrevSBoxPhone() {
    SBox(texto = "20")
}







