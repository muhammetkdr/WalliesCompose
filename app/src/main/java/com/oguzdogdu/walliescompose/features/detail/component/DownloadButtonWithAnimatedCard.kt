package com.oguzdogdu.walliescompose.features.detail.component

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.features.detail.DownloadState
import com.oguzdogdu.walliescompose.ui.theme.medium
import com.oguzdogdu.walliescompose.util.toReadableSize

@OptIn(ExperimentalSharedTransitionApi::class)
val buttonBoundTransform = BoundsTransform { _: Rect, _: Rect ->
    spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessVeryLow)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DownloadButton(
    animatedContentScope: AnimatedContentScope,
    buttonName: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = { onClick(buttonName) },
        modifier = modifier
            .sharedElement(
                state = rememberSharedContentState(key = buttonName),
                animatedVisibilityScope = animatedContentScope,
                boundsTransform = buttonBoundTransform
            )
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Text(
            text = "Download by $buttonName",
            fontFamily = medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DownloadCard(
    animatedContentScope: AnimatedContentScope,
    buttonName: String,
    downloadState: DownloadState,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .sharedElement(
                state = rememberSharedContentState(key = buttonName),
                animatedVisibilityScope = animatedContentScope,
                boundsTransform = buttonBoundTransform
            )
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            GreenDownloadIcon(downloadState = downloadState)
            Spacer(modifier = Modifier.size(8.dp))
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = if (!downloadState.isCompleted) "Downloading..." else "Completed",
                    fontFamily = medium, fontSize = 16.sp,color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "${downloadState.downloadedBytes.toReadableSize()} / ${downloadState.totalBytes.toReadableSize()}",
                    fontFamily = medium, fontSize = 12.sp,color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.size(8.dp))
                AnimatedLinearProgressBar(progress =
                downloadState.downloadedBytes.toFloat() / downloadState.totalBytes.toFloat())
            }
        }
    }
}

@Composable
fun AnimatedLinearProgressBar(progress: Float) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress, label = "", animationSpec = tween(1000, easing = EaseOutCubic)
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp),
        strokeCap = StrokeCap.Round,
        color = colorResource(id = R.color.lush_green),
        trackColor = Color.DarkGray
    )
}

@Composable
fun GreenDownloadIcon(
    downloadState: DownloadState,
    size: Dp = 40.dp,
    backgroundColor: Color = Color(0xFF008800),
    arrowColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val checkmarkProgress = remember { Animatable(0f) }
    val verticalOffset = remember { Animatable(0f) }
    LaunchedEffect(downloadState.isCompleted) {
        if (!downloadState.isCompleted) {
            verticalOffset.animateTo(
                targetValue = 0.5f * size.value, animationSpec = infiniteRepeatable(
                    animation = tween(500, easing = EaseInCubic), repeatMode = RepeatMode.Reverse
                )
            )
            checkmarkProgress.snapTo(0f)
        } else {
            verticalOffset.snapTo(0f)
            checkmarkProgress.animateTo(
                targetValue = 1f, animationSpec = tween(
                    durationMillis = 700, easing = EaseOutCubic
                )
            )
        }

    }
    Box(
        modifier = modifier
            .size(size)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val width = size.toPx()
            val height = size.toPx()
            val center = Offset(width / 2f, height / 2f)
            val radius = width / 2f

            drawCircle(
                color = backgroundColor, radius = radius, center = center
            )
            if (!downloadState.isCompleted) {
                val arrowPath = Path().apply {
                    val offsetY = verticalOffset.value
                    moveTo(center.x, center.y - height * 0.2f + offsetY)
                    lineTo(center.x, center.y + height * 0.3f + offsetY)


                    moveTo(center.x - width * 0.2f, center.y + height * 0.1f + offsetY)
                    lineTo(center.x, center.y + height * 0.3f + offsetY)

                    moveTo(center.x + width * 0.2f, center.y + height * 0.1f + offsetY)
                    lineTo(center.x, center.y + height * 0.3f + offsetY)
                }
                drawPath(
                    path = arrowPath,
                    color = arrowColor,
                    style = Stroke(width = width / 18f, cap = StrokeCap.Round)
                )
            }


            if (downloadState.isCompleted) {
                val checkmarkPath = Path().apply {
                    moveTo(center.x - radius * 0.5f, center.y)
                    lineTo(center.x - radius * 0.1f, center.y + radius * 0.4f)
                    lineTo(center.x + radius * 0.5f, center.y - radius * 0.4f)
                }
                val pathMeasure = PathMeasure()
                pathMeasure.setPath(checkmarkPath, false)

                val animatedPath = Path()
                pathMeasure.getSegment(
                    startDistance = 0f,
                    stopDistance = pathMeasure.length * checkmarkProgress.value,
                    destination = animatedPath
                )
                drawPath(
                    path = animatedPath, color = arrowColor, style = Stroke(
                        width = width / 18f,
                        cap = StrokeCap.Round,
                    )
                )
            }
        }
    }
}