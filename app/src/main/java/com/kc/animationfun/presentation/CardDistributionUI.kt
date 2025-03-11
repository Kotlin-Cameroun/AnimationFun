package com.kc.animationfun.presentation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

data class CardData(
    val id: Int,
    val color: Color
)

@Composable
fun CardDistributionUI() {
    val cards = remember { mutableStateListOf<CardData>() }
    var counter by remember { mutableStateOf(1) }
    var draggedCardId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            cards.add(
                CardData(
                    id = counter++,
                    color = Color(
                        red = Random.nextFloat(),
                        green = Random.nextFloat(),
                        blue = Random.nextFloat()
                    )
                )
            )
        }) {
            Text("Distribution")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            cards.forEachIndexed { index, card ->
                key(card.id) {
                    AnimatedSlidingCard(
                        index = index,
                        card = card,
                        isDragged = (draggedCardId == card.id),
                        onDragStart = { draggedCardId = it.id },
                        onDragEnd = { draggedCardId = null },
                        onRemove = { cards.remove(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedSlidingCard(
    index: Int,
    card: CardData,
    isDragged: Boolean,
    onDragStart: (CardData) -> Unit,
    onDragEnd: () -> Unit,
    onRemove: (CardData) -> Unit
) {
    val targetX = remember { Animatable(-300f) }
    val targetY = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val animatedAlpha = remember { Animatable(0f) }

    // Drag offset
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    var isBeingRemoved by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Animation d'entrée de la carte
        launch {
            targetX.animateTo(
                targetValue = index * 40f,
                animationSpec = tween(600, delayMillis = index * 100, easing = FastOutSlowInEasing)
            )
        }
        launch {
            targetY.animateTo(
                targetValue = index * 10f,
                animationSpec = tween(600, delayMillis = index * 100, easing = FastOutSlowInEasing)
            )
        }
        launch {
            animatedAlpha.animateTo(1f, tween(600, delayMillis = index * 100))
        }
    }

    if (isBeingRemoved) {
        LaunchedEffect("remove-${card.id}") {
            launch {
                targetX.animateTo(-300f, tween(600, easing = FastOutSlowInEasing))
            }
            launch {
                animatedAlpha.animateTo(0f, tween(600))
            }
            delay(600)
            onRemove(card)
        }
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (targetX.value + dragOffset.x).roundToInt(),
                    (targetY.value + dragOffset.y).roundToInt()
                )
            }
            .zIndex(if (isDragged) 1f else 0f)
            .pointerInput(card.id) {
                detectDragGestures(
                    onDragStart = {
                        onDragStart(card)
                    },
                    onDragEnd = {
                        onDragEnd()
                    },
                    onDragCancel = {
                        onDragEnd()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                    }
                )
            }
            .graphicsLayer {
                rotationZ = rotation.value
                alpha = animatedAlpha.value
            }
            .size(120.dp, 180.dp)
            .background(color = card.color, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
            .clickable {
                if (!isBeingRemoved) isBeingRemoved = true
            }
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("${card.id}", color = Color.White, style = MaterialTheme.typography.titleMedium)
        }
    }
}





@Preview
@Composable
fun CardDistributionUIPreview() {
    MaterialTheme {
        CardDistributionUI()
    }
}

