package com.oguzdogdu.walliescompose.features.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.util.noRippleClickable

@Composable
fun DayNightSwitch(
    isNightMode: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val transition = updateTransition(targetState = isNightMode, label = "DayNightTransition")

    val circleOffset by transition.animateDp(
        label = "CircleOffset",
        transitionSpec = { tween(500, easing = LinearEasing) }
    ) { if (it) 8.dp else 32.dp }

    val circleColor by transition.animateColor(
        label = "CircleColor"
    ) { if (it) Color(0xFFFFC107) else Color(0xFFFF9800) }

    Box(
        modifier = Modifier
            .size(56.dp, 32.dp)
            .border(width = 2.dp, shape = RoundedCornerShape(16.dp), color = Color.Gray)
            .noRippleClickable { onToggle(!isNightMode) },
        contentAlignment = Alignment.CenterStart
    ) {
        AnimatedVisibility(
            visible = isNightMode,
            modifier = Modifier
                .align(Alignment.CenterEnd),
            enter = slideInVertically(tween(500)),
            exit = slideOutVertically(tween(500))

        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sun),
                contentDescription = "Night Icon",
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 6.dp)
                    .align(Alignment.Center)
            )
        }

        AnimatedVisibility(
            visible = !isNightMode, modifier = Modifier
                .align(Alignment.CenterStart),
            enter = slideInVertically(tween(500)),
            exit = slideOutVertically(tween(500))
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_moon),
                contentDescription = "Day Icon",
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 6.dp)
                    .align(Alignment.Center)

            )
        }

        Box(
            modifier = Modifier
                .size(16.dp)
                .offset {
                    IntOffset(
                        circleOffset
                            .toPx()
                            .toInt(), 0
                    )
                }
                .background(circleColor, shape = CircleShape)
        )
    }
}
