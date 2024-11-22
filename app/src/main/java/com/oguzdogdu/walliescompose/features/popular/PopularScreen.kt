package com.oguzdogdu.walliescompose.features.popular

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.SubcomposeAsyncImage
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.data.common.ImageLoadingState
import com.oguzdogdu.walliescompose.domain.model.popular.PopularImage
import com.oguzdogdu.walliescompose.features.home.SharedTransitionKey
import com.oguzdogdu.walliescompose.features.popular.components.PopularEditDetails
import com.oguzdogdu.walliescompose.ui.theme.bold
import com.oguzdogdu.walliescompose.ui.theme.medium
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin


@OptIn(ExperimentalSharedTransitionApi::class)
 val boundsTransform = BoundsTransform { _: Rect, _: Rect ->
    tween(durationMillis = boundsAnimationDurationMillis, easing = LinearEasing)
}
private const val boundsAnimationDurationMillis = 500

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PopularScreenRoute(
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    viewModel: PopularViewModel = hiltViewModel(),
    onPopularClick: (String?) -> Unit,
    onBackClick: () -> Unit
) {

    val popularListState: LazyPagingItems<PopularImage> =
        viewModel.getPopular.collectAsLazyPagingItems()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    var isRefreshList by remember { mutableStateOf(false) }
    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE, lifecycleOwner = lifecycleOwner) {
        viewModel.handleUIEvent(PopularScreenEvent.FetchPopularData)

    }
    Scaffold(modifier = modifier
        .fillMaxSize(), topBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onBackClick.invoke() },
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .wrapContentSize()
                )
            }

            Text(
                text = stringResource(id = R.string.popular_title),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp,
                fontFamily = medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
        }

    }) {
        PopularDetailListScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            animatedVisibilityScope = animatedVisibilityScope,
            popularLazyPagingItems = popularListState,
            onPopularClick = { id ->
                onPopularClick.invoke(id)
            },
            listRefresh = {
                scope.launch {
                    isRefreshList = true
                    viewModel.handleUIEvent(PopularScreenEvent.FetchPopularData)
                    delay(4000)
                    isRefreshList = false
                }

            }, listRefreshing = isRefreshList
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SharedTransitionScope.PopularDetailListScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    popularLazyPagingItems: LazyPagingItems<PopularImage>,
    onPopularClick: (String) -> Unit,
    listRefresh: () -> Unit,
    listRefreshing:Boolean,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyGridState()
    val showButton by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0
        }
    }
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    var isProcess by remember { mutableStateOf(false) }

    LaunchedEffect (listRefreshing){
        if (listRefreshing) {
            isProcess = true
            isRefreshing = true
        } else {
            isProcess = false
            delay(850)
            isRefreshing = false
        }
    }
    val pullToRefreshState = rememberPullToRefreshState()
    val onRefresh: () -> Unit = {
        scope.launch {
            listRefresh.invoke()
        }
    }
    var isClick by remember { mutableStateOf(false) }
    var transitionScreenItemState by remember { mutableStateOf<PopularImage?>(null) }
    val pullAnimate by animateFloatAsState(
        targetValue = (pullToRefreshState.distanceFraction * 200).coerceIn(
            0f,
            200f
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = 125f
        ), label = ""
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = if (transitionScreenItemState == null) Modifier
                    .fillMaxSize()
                    .pullToRefresh(
                        isRefreshing = isRefreshing,
                        state = pullToRefreshState,
                        onRefresh = onRefresh
                    ) else Modifier
                    .fillMaxSize()
                    .blur(
                        10.dp, edgeTreatment = BlurredEdgeTreatment(
                            RoundedCornerShape(16.dp)
                        )
                    )
            ) {
                CoopLoadingAnimation(
                    isProcess = isProcess,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .offset { IntOffset(x = 0, y = pullAnimate.toInt()) },
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = popularLazyPagingItems.itemCount,
                        key = popularLazyPagingItems.itemKey { item: PopularImage -> item.url.hashCode() },
                        contentType = popularLazyPagingItems.itemContentType { "Popular" }) { index: Int ->
                        val popular: PopularImage? = popularLazyPagingItems[index]
                        androidx.compose.animation.AnimatedVisibility(
                            visible = transitionScreenItemState != popular,
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            Box(
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "${popular?.id}-bounds"),
                                        animatedVisibilityScope = this,
                                        clipInOverlayDuringTransition = OverlayClip(
                                            RoundedCornerShape(16.dp)
                                        ),
                                        boundsTransform = boundsTransform,
                                    )
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                if (popular != null) {
                                    with(if (transitionScreenItemState != popular) this@PopularDetailListScreen else this@SharedTransitionLayout) {
                                        PopularListItem(
                                            animatedVisibilityScope = if (transitionScreenItemState != popular) animatedVisibilityScope else this@AnimatedVisibility,
                                            popularImage = popular,
                                            onPopularClick = { id ->
                                                if (transitionScreenItemState == null) {
                                                    isClick = isClick.not()
                                                    onPopularClick.invoke(id)
                                                }

                                            },
                                            onPreviewClick = {
                                                transitionScreenItemState = it
                                            })
                                    }
                                }
                            }
                        }
                    }
                }
                if (showButton && !isClick) {
                        FloatingActionButton(
                            modifier = Modifier
                                .renderInSharedTransitionScopeOverlay(
                                    zIndexInOverlay = 1f,
                                )
                                .zIndex(2f)
                                .animateContentSize()
                                .align(Alignment.BottomEnd)
                                .padding(20.dp),
                            containerColor = colorResource(id = R.color.lush_green),
                            onClick = {
                                scope.launch {
                                    listState.scrollToItem(0)
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_upward),
                                contentDescription = "Scroll the list"
                            )
                        }
                    }
            }
            PopularEditDetails(popularImage = transitionScreenItemState, onConfirmClick = {
                transitionScreenItemState = null
            })
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PopularListItem(
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    popularImage: PopularImage,
    onPopularClick: (String) -> Unit,
    onPreviewClick: (PopularImage) -> Unit
) {
    Box {
        SubcomposeAsyncImage(
            modifier = modifier
                .sharedElement(
                    state = rememberSharedContentState(key = "$SharedTransitionKey${popularImage.id}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(16.dp)),
                    boundsTransform = boundsTransform
                )
                .fillMaxWidth()
                .height(240.dp)
                .clickable {
                    popularImage.id?.let { onPopularClick.invoke(it) }
                }
                .clip(RoundedCornerShape(16.dp)),
            model = popularImage.url,
            contentDescription = popularImage.imageDesc,
            loading = {
                ImageLoadingState()
            },
            contentScale = ContentScale.FillBounds
        )
        IconButton(
            onClick = { onPreviewClick.invoke(popularImage) },
            modifier = Modifier
                .padding(8.dp)
                .clip(RoundedCornerShape(32.dp))
                .size(32.dp)
                .background(Color.LightGray.copy(alpha = 0.5f))
                .align(Alignment.TopEnd)

        ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_expand),
                contentDescription = "",
                modifier = Modifier
                    .wrapContentSize()
            )
        }
    }
}

@Composable
fun CoopLoadingAnimation(
    modifier: Modifier = Modifier,
    isProcess: Boolean
) {
    val rotation = remember { Animatable(0f) }
    val radius = remember { Animatable(20f) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isProcess) {
        if (isProcess) {
            launch {
                isVisible = true
                radius.snapTo(20f)
                rotation.animateTo(
                    targetValue = rotation.value + 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }

        } else {
            launch {
                rotation.snapTo(0f)
                radius.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 400, easing = LinearEasing)
                )
                delay(450)
                isVisible = false
            }
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp),
            contentAlignment = Alignment.Center
        ) {
            val letters = stringResource(R.string.app_name).uppercase().toList()
            val angleStep = 360f / letters.size
            val totalWidth = letters.size * 16
            val startX = -totalWidth / 2

            letters.forEachIndexed { index, letter ->
                val angle = rotation.value + (index * angleStep)
                val circleX = cos(angle * Math.PI / 180) * radius.value
                val circleY = sin(angle * Math.PI / 180) * radius.value

                val lineX = startX + (index * 16) + 16f

                val transitionProgress = (20f - radius.value) / 20f
                val x = lerp(circleX.toFloat(), lineX, transitionProgress)
                val y = lerp(circleY.toFloat(), 0f, transitionProgress)

                Text(
                    text = letter.toString(),
                    color = Color(0xFFFF9800),
                    fontSize = 16.sp,
                    fontFamily = bold,
                    modifier = Modifier.offset(x.dp, y.dp)
                )
            }
        }
    }
}