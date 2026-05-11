package com.mysuccu.app.ui.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysuccu.app.R
import com.mysuccu.app.ui.theme.*

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha = transition.animateFloat(
        initialValue = 0.2f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(animation = tween(1000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "alpha"
    )
    return this.background(DividerGray.copy(alpha = alpha.value))
}

@Composable
fun PlantCard(
    modifier: Modifier = Modifier,
    plantName: String = "",
    statusText: String = "",
    days: Int = 0,
    isHealthy: Boolean = true,
    statusColor: Color = ThemeOfficialContainer, // 默认调用健康色 #D2DC97
    isLoading: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(enabled = !isLoading) { },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, DividerGray)
    ) {
        if (isLoading) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(8.dp)).shimmerEffect())
                Box(modifier = Modifier.fillMaxWidth(0.8f).height(12.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Box(modifier = Modifier.fillMaxWidth(0.5f).height(10.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
            }
            return@Card
        }

        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(ThemeOfficialContainer.copy(alpha = 0.3f)) // #D2DC97
            ) {
                Text(
                    text = if (plantName.isNotEmpty()) plantName.take(1) else "?",
                    fontSize = 40.sp, fontWeight = FontWeight.Bold,
                    color = ThemeOfficialPrimary.copy(alpha = 0.4f), // #1A3D59
                    modifier = Modifier.align(Alignment.Center)
                )

                if (statusText.isNotEmpty()) {
                    Surface(
                        color = statusColor,
                        shape = RoundedCornerShape(percent = 50),
                        modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)
                    ) {
                        Text(
                            text = statusText, fontSize = 8.sp, fontWeight = FontWeight.Bold,
                            color = if (statusColor == ThemeOfficialContainer) ThemeOfficialPrimary else Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = plantName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold,
                    color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.DateRange, null, modifier = Modifier.size(12.dp), tint = ThemeOfficialAccent2)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(id = R.string.days_format, days), fontSize = 10.sp, color = ThemeOfficialAccent2)
                }
            }
        }
    }
}

@Composable
fun AddPlantCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth().aspectRatio(0.7f).clip(RoundedCornerShape(16.dp))
            .background(SurfaceLight)
            .clickable { }
            .padding(2.dp)
            .drawBehind {
                drawRoundRect(
                    color = ThemeOfficialPrimary.copy(alpha = 0.3f),
                    style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(percent = 50)).background(ThemeOfficialContainer),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Add, null, tint = ThemeOfficialPrimary, modifier = Modifier.size(20.dp)) }
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(id = R.string.add), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ThemeOfficialPrimary)
        }
    }
}