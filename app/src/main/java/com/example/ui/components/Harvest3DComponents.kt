package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.*

/**
 * 3D Interactive Card Tilt Modifier
 * Tilts a card in 3D space with perspective and dynamic shadow based on touch/drag interaction.
 */
fun Modifier.perspective3D(
    maxTiltDegrees: Float = 14f
): Modifier = this.then(
    Modifier.graphicsLayer {
        cameraDistance = 16f * density
    }
)

/**
 * Interactive 3D Card Container that tilts in 3D based on touch gestures.
 */
@Composable
fun Interactive3DCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var rotX by remember { mutableFloatStateOf(0f) }
    var rotY by remember { mutableFloatStateOf(0f) }

    val animatedRotX by animateFloatAsState(
        targetValue = rotX,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "rotX"
    )
    val animatedRotY by animateFloatAsState(
        targetValue = rotY,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "rotY"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationX = animatedRotX
                rotationY = animatedRotY
                cameraDistance = 14f * density
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {},
                    onDragEnd = {
                        rotX = 0f
                        rotY = 0f
                    },
                    onDragCancel = {
                        rotX = 0f
                        rotY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        rotX = (rotX - dragAmount.y * 0.25f).coerceIn(-15f, 15f)
                        rotY = (rotY + dragAmount.x * 0.25f).coerceIn(-15f, 15f)
                    }
                )
            }
    ) {
        content()
    }
}

/**
 * Brand 3D: Seed-to-Sprout Growth Micro-Animation
 * Renders an animated agricultural seed that embeds into rich soil and sprouts 3D emerald leaves.
 */
@Composable
fun SeedToSprout3DVisual(
    modifier: Modifier = Modifier.size(110.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "seed_growth")

    // Rotation around Y axis for 3D depth
    val yRotation by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "yRotation"
    )

    // Growth progression
    val sproutScale by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sproutScale"
    )

    // Gentle sunbeam angle
    val beamOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "beamOffset"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = yRotation
                cameraDistance = 14f * density
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f

            // Soft aura glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color.Transparent),
                    center = center,
                    radius = radius * 0.95f
                )
            )

            // 3D Mound of Farm Soil
            val soilPath = Path().apply {
                moveTo(center.x - radius * 0.65f, center.y + radius * 0.35f)
                cubicTo(
                    center.x - radius * 0.3f, center.y + radius * 0.2f,
                    center.x + radius * 0.3f, center.y + radius * 0.2f,
                    center.x + radius * 0.65f, center.y + radius * 0.35f
                )
                cubicTo(
                    center.x + radius * 0.4f, center.y + radius * 0.55f,
                    center.x - radius * 0.4f, center.y + radius * 0.55f,
                    center.x - radius * 0.65f, center.y + radius * 0.35f
                )
                close()
            }
            drawPath(
                path = soilPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF6D4C41), Color(0xFF3E2723))
                )
            )

            // 3D Sprout Stem
            val stemPath = Path().apply {
                moveTo(center.x, center.y + radius * 0.25f)
                cubicTo(
                    center.x - 4f, center.y,
                    center.x + 4f, center.y - radius * 0.2f * sproutScale,
                    center.x, center.y - radius * 0.35f * sproutScale
                )
            }
            drawPath(
                path = stemPath,
                color = HarvestForestGreen,
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            // Left Emerald Leaf
            val leftLeaf = Path().apply {
                moveTo(center.x, center.y - radius * 0.2f * sproutScale)
                cubicTo(
                    center.x - radius * 0.4f * sproutScale, center.y - radius * 0.35f * sproutScale,
                    center.x - radius * 0.35f * sproutScale, center.y - radius * 0.05f * sproutScale,
                    center.x, center.y - radius * 0.1f * sproutScale
                )
                close()
            }
            drawPath(
                path = leftLeaf,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF81C784), Color(0xFF2E7D32)),
                    start = Offset(center.x, center.y),
                    end = Offset(center.x - radius * 0.4f, center.y - radius * 0.35f)
                )
            )

            // Right Emerald Leaf
            val rightLeaf = Path().apply {
                moveTo(center.x, center.y - radius * 0.25f * sproutScale)
                cubicTo(
                    center.x + radius * 0.42f * sproutScale, center.y - radius * 0.45f * sproutScale,
                    center.x + radius * 0.38f * sproutScale, center.y - radius * 0.1f * sproutScale,
                    center.x, center.y - radius * 0.15f * sproutScale
                )
                close()
            }
            drawPath(
                path = rightLeaf,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFA5D6A7), Color(0xFF1B5E20)),
                    start = Offset(center.x, center.y),
                    end = Offset(center.x + radius * 0.42f, center.y - radius * 0.45f)
                )
            )

            // Specular dew drop reflection on leaf
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = 2.5f,
                center = Offset(center.x - radius * 0.18f * sproutScale, center.y - radius * 0.22f * sproutScale)
            )
        }
    }
}

/**
 * 3D Isometric Wooden Crate Celebration & Loading Graphic
 * Renders a 3D perspective harvest crate with hovering vegetables.
 */
@Composable
fun HarvestCrate3DVisual(
    modifier: Modifier = Modifier.size(130.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "crate_bounce")

    val cropBounce by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cropBounce"
    )

    val shadowScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shadowScale"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // 3D Soft cast shadow on ground
        drawOval(
            color = Color.Black.copy(alpha = 0.15f),
            topLeft = Offset(cx - (w * 0.35f * shadowScale), cy + h * 0.28f),
            size = Size(w * 0.7f * shadowScale, h * 0.16f)
        )

        // 3D Wooden Crate Back Wall
        val backWall = Path().apply {
            moveTo(cx - w * 0.3f, cy)
            lineTo(cx, cy - h * 0.16f)
            lineTo(cx + w * 0.3f, cy)
            lineTo(cx, cy + h * 0.14f)
            close()
        }
        drawPath(
            path = backWall,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF8D6E63), Color(0xFF5D4037))
            )
        )

        // Fresh Crops inside Crate (with dynamic 3D hover/bobbing)
        // Red Tomato
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFF5252), Color(0xFFC62828)),
                center = Offset(cx - w * 0.12f, cy - h * 0.08f + cropBounce),
                radius = w * 0.14f
            ),
            radius = w * 0.13f,
            center = Offset(cx - w * 0.12f, cy - h * 0.08f + cropBounce)
        )
        // Tomato green calyx
        drawCircle(
            color = HarvestForestGreen,
            radius = w * 0.035f,
            center = Offset(cx - w * 0.12f, cy - h * 0.19f + cropBounce)
        )

        // Golden Sweet Corn Cob
        drawOval(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFEE58), Color(0xFFF57F17))
            ),
            topLeft = Offset(cx + w * 0.02f, cy - h * 0.22f - cropBounce * 0.5f),
            size = Size(w * 0.14f, h * 0.28f)
        )

        // Crisp Farm Lettuce Leaves
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFF81C784), Color(0xFF2E7D32)),
                center = Offset(cx - w * 0.02f, cy - h * 0.16f + cropBounce * 0.8f),
                radius = w * 0.12f
            ),
            radius = w * 0.11f,
            center = Offset(cx - w * 0.02f, cy - h * 0.16f + cropBounce * 0.8f)
        )

        // 3D Wooden Crate Left Face
        val leftFace = Path().apply {
            moveTo(cx - w * 0.34f, cy)
            lineTo(cx, cy + h * 0.16f)
            lineTo(cx, cy + h * 0.34f)
            lineTo(cx - w * 0.34f, cy + h * 0.18f)
            close()
        }
        drawPath(
            path = leftFace,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFBCAAA4), Color(0xFF795548)),
                start = Offset(cx - w * 0.34f, cy),
                end = Offset(cx, cy + h * 0.34f)
            )
        )

        // 3D Wooden Crate Right Face
        val rightFace = Path().apply {
            moveTo(cx, cy + h * 0.16f)
            lineTo(cx + w * 0.34f, cy)
            lineTo(cx + w * 0.34f, cy + h * 0.18f)
            lineTo(cx, cy + h * 0.34f)
            close()
        }
        drawPath(
            path = rightFace,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFA1887F), Color(0xFF4E342E)),
                start = Offset(cx, cy + h * 0.16f),
                end = Offset(cx + w * 0.34f, cy + h * 0.34f)
            )
        )

        // Slat outline grooves for tactile farm crate realism
        drawLine(
            color = Color(0xFF3E2723).copy(alpha = 0.4f),
            start = Offset(cx - w * 0.34f, cy + h * 0.08f),
            end = Offset(cx, cy + h * 0.24f),
            strokeWidth = 2.5f
        )
        drawLine(
            color = Color(0xFF3E2723).copy(alpha = 0.4f),
            start = Offset(cx, cy + h * 0.24f),
            end = Offset(cx + w * 0.34f, cy + h * 0.08f),
            strokeWidth = 2.5f
        )
    }
}
