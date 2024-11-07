package com.oguzdogdu.walliescompose.features.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.ui.theme.bold
import com.oguzdogdu.walliescompose.ui.theme.regular
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OnboardingPageContent(
    pageNumber: Int,
    onboardingPages: List<OnboardingPage>
) {
    val (animateImage, setAnimateImage) = remember { mutableStateOf(false) }
    val (animateTitle, setAnimateTitle) = remember { mutableStateOf(false) }
    val (animateDescription, setAnimateDescription) = remember { mutableStateOf(false) }

    LaunchedEffect(pageNumber) {
        setAnimateImage(false)
        setAnimateTitle(false)
        setAnimateDescription(false)

        setAnimateImage(true)
        delay(500)
        setAnimateTitle(true)
        delay(500)
        setAnimateDescription(true)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = animateImage,
            enter = slideInVertically(
                animationSpec = spring(
                    stiffness = Spring.StiffnessVeryLow
                ),
                initialOffsetY = { -it }
            ) + fadeIn(tween(1000))
        ) {
            Image(
                painter = painterResource(id = onboardingPages[pageNumber].imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = animateTitle,
                enter = slideInHorizontally(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    ),
                    initialOffsetX = { -it }
                ) + fadeIn(tween(1000))
            ) {
                Text(
                    text = onboardingPages[pageNumber].title,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 24.sp,
                    fontFamily = bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = animateDescription,
                enter = slideInHorizontally(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    ),
                    initialOffsetX = { -it }
                ) + fadeIn(tween(1000))
            ) {
                Text(
                    text = onboardingPages[pageNumber].description,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 18.sp,
                    fontFamily = regular,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun OnboardingButton(
    pagerState: PagerState,
    onboardingPages: List<OnboardingPage>,
    onSkipClicked: () -> Unit,
    onGetStartedClicked: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 8.dp, vertical = 24.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            onClick = {
                onSkipClicked()
            },
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            Text(
                text = "Skip",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = regular
            )
        }

        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            onboardingPages.forEachIndexed { index, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index)
                                Color(0xFFFFA500).copy(alpha = 0.8f)
                            else Color.Gray.copy(alpha = 0.5f)
                        )

                )
            }
        }

        Button(
            onClick = {
                if (pagerState.currentPage == onboardingPages.lastIndex) {
                    onGetStartedClicked()
                } else {
                    coroutineScope.launch {
                        pagerState.scrollToPage(
                            pagerState.currentPage.plus(1)
                        )
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.lush_green)
            ),
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .animateContentSize()
        ) {
            Text(
                text = if (pagerState.currentPage == onboardingPages.lastIndex) {
                    "Get Started"
                } else {
                    "Next"
                },
                color = Color.White,
                textAlign = TextAlign.Start,
                maxLines = 1,
                fontFamily = regular
            )
        }
    }
}

@Composable
fun OnboardingScreen(
    onSkipClicked: () -> Unit,
    onGetStartedClicked: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val onboardingPages = listOf(
        OnboardingPage(
            title = stringResource(R.string.text_onboarding_first_title),
            description = stringResource(R.string.text_onboarding_first_desc),
            imageRes = R.drawable.ic_hq_res_image
        ),
        OnboardingPage(
            title = stringResource(R.string.text_onboarding_second_title),
            description = stringResource(R.string.text_onboarding_second_desc),
            imageRes = R.drawable.ic_add_image_to_favorites
        ),
        OnboardingPage(
            title = stringResource(R.string.text_onboarding_third_title),
            description = stringResource(R.string.text_onboarding_third_desc),
            imageRes = R.drawable.ic_set_and_download
        ),
    )
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState, modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                userScrollEnabled = false,
                pageSize = PageSize.Fill
            ) { page ->
                OnboardingPageContent(page, onboardingPages)
            }

            OnboardingButton(
                pagerState = pagerState,
                onboardingPages = onboardingPages,
                onSkipClicked = onSkipClicked,
                onGetStartedClicked = onGetStartedClicked
            )
        }
    }
}


@Immutable
data class OnboardingPage(
    val title: String, val description: String, val imageRes: Int
)