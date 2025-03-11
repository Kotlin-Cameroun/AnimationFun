package com.kc.animationfun.presentation

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun EscapeFormButtonDemoUI() {
    var name by remember { mutableStateOf("") }
    val minLength = 5
    val isValid = name.trim().length >= minLength

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Interactive Form \uD83D\uDE01",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your name (min $minLength characters)") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
                }
        ) {
            Button(
                onClick = {
                    if (isValid) {
                        // Toast.makeText(LocalContext.current, "Formulaire envoyé !", Toast.LENGTH_SHORT).show()
                    } else {
                        scope.launch {
                            val randomX = listOf(-200f, 200f).random()
                            val randomY = listOf(-100f, 100f).random()

                            // les deux animations en parallèle
                            launch {
                                offsetX.animateTo(randomX, animationSpec = tween(600, easing = LinearOutSlowInEasing))
                            }
                            launch {
                                offsetY.animateTo(randomY, animationSpec = tween(600, easing = LinearOutSlowInEasing))
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isValid) Color(0xFF4CAF50) else Color.Red
                )
            ) {
                Text("Submit")
            }
        }
    }

    // Observation de la valeur de `name` pour ajuster la position du bouton
    LaunchedEffect(name) {
        val progress = (name.length.toFloat() / minLength).coerceIn(0f, 1f)
        offsetX.animateTo(0f * progress, animationSpec = tween(300))
        offsetY.animateTo(0f * progress, animationSpec = tween(300))
    }
}

@Preview
@Composable
fun EscapeFormButtonDemoUIPreview() {
    MaterialTheme {
        EscapeFormButtonDemoUI()
    }
}
