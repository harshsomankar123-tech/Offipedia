package com.plcoding.bookpedia.core.presentation

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.pulseAnimation(): Modifier {
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    return this.graphicsLayer {
        alpha = progress
        scaleX = progress
        scaleY = progress
    }
}
