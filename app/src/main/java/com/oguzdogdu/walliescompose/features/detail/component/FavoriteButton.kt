package com.oguzdogdu.walliescompose.features.detail.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oguzdogdu.walliescompose.domain.model.favorites.FavoriteImages
import com.oguzdogdu.walliescompose.ui.theme.regular
import kotlinx.coroutines.launch

@Composable
fun WalliesFavoriteButton(
    modifier: Modifier = Modifier,
    favoriteImages: FavoriteImages?,
    addPhotoToFavorites: () -> Unit,
    removePhotoFromFavorites: () -> Unit
) {
    var isClicked by remember { mutableStateOf(false) }
    val heartScaleAlpha = remember { Animatable(1f) }

    LaunchedEffect(favoriteImages?.isChecked, isClicked) {
        if (favoriteImages?.isChecked == true) {
            launch {
                repeat(2) {
                    heartScaleAlpha.animateTo(
                        targetValue = 1f,
                        animationSpec = keyframes {
                            durationMillis = 1000
                            1.2f at 300 using CubicBezierEasing(0.68f, 0.55f, 0.265f, 1f)
                            1f at 1000 using CubicBezierEasing(0.68f, 0.55f, 0.265f, 1f)
                        }
                    )
                }
            }
        } else {
            launch {
                heartScaleAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        }
    }

    Button(
        onClick = {
            isClicked = !isClicked
            if (!(isClicked and (favoriteImages?.isChecked == true))) {
                addPhotoToFavorites.invoke()
            } else {
                removePhotoFromFavorites.invoke()
            }

        },
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .animateContentSize(
                spring(
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
        , contentPadding = PaddingValues(8.dp)
    ) {
        Row(
            modifier = Modifier
                .animateContentSize(
                  tween(500, easing = LinearEasing)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val tintColor =
                if (favoriteImages?.isChecked?.or(isClicked) == true) Red else MaterialTheme.colorScheme.onPrimaryContainer
            Icon(
                painter = rememberVectorPainter(Icons.TwoTone.Favorite),
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier
                    .size(28.dp)
                    .scale(heartScaleAlpha.value)
            )

            Spacer(modifier = Modifier.width(8.dp))
                AnimatedContent(
                    targetState = favoriteImages?.isChecked == true,
                    transitionSpec = {
                        (slideInVertically(
                            animationSpec = spring(
                                dampingRatio = 0.35f,
                                stiffness = Spring.StiffnessVeryLow
                            )
                        ) { height -> height }).togetherWith(
                            slideOutVertically(
                                animationSpec = spring(
                                    dampingRatio = 0.35f,
                                    stiffness = Spring.StiffnessVeryLow
                                )
                            ) { height -> -height })
                    },
                    label = "favorite text transition",
                    contentKey = {
                        it
                    }
                ) { favorite ->
                    Text(
                        text = if (favorite or (favoriteImages?.isChecked == true))
                            "Added to Favorites" else
                                "Add to Favorites",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontFamily = regular,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .graphicsLayer {
                                cameraDistance = 12f
                            }
                    )
                }
            }
        }
    }
