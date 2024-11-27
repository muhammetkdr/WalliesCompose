package com.oguzdogdu.walliescompose.features.collections

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.domain.model.collections.WallpaperCollections
import com.oguzdogdu.walliescompose.features.collections.components.CollectionItem
import com.oguzdogdu.walliescompose.features.collections.components.ShowFilterOfCollections
import com.oguzdogdu.walliescompose.ui.theme.medium
import com.oguzdogdu.walliescompose.ui.theme.regular
import com.oguzdogdu.walliescompose.util.measureTextWidth
import com.oguzdogdu.walliescompose.util.scrollProgress

typealias onCollectionsScreenEvent = (CollectionScreenEvent) -> Unit

@Composable
fun CollectionsScreenRoute(
    modifier: Modifier = Modifier,
    viewModel: CollectionsViewModel = hiltViewModel(),
    onCollectionClick: (String,String) -> Unit,
) {
    val collectionPaginationState: LazyPagingItems<WallpaperCollections> =
        viewModel.collectionPhotosState.collectAsLazyPagingItems()
    val collectionScreenState by viewModel.collectionScreenState.collectAsStateWithLifecycle()
    val sortAndFilterItems = listOf(
        ListSortAndFilterType(R.string.text_last_collected),
        ListSortAndFilterType(R.string.text_total_photos),
        ListSortAndFilterType(R.string.text_private_photos),
    )
    val context = LocalContext.current
    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {

        if (collectionPaginationState.itemCount == 0) {
            viewModel.handleUIEvent(CollectionScreenEvent.FetchLatestData)
        }
    }
    LifecycleEventEffect(event = Lifecycle.Event.ON_STOP) {
        viewModel.onListTypeChanged(collectionScreenState.collectionsListType.name)
    }

    LaunchedEffect(true) { viewModel.handleUIEvent(CollectionScreenEvent.CheckListType) }

    Scaffold(modifier = modifier
        .fillMaxSize(), topBar = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 12.dp, top = 16.dp, bottom = 8.dp, end = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.collections_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 24.sp,
                        fontFamily = medium,
                    )
                }
            Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.collection_desc),
                    fontSize = 16.sp,
                    fontFamily = medium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
            }
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            CollectionScreen(
                collectionState = collectionScreenState,
                filterTypes = sortAndFilterItems,
                collectionLazyPagingItems = collectionPaginationState,
                onCollectionClick = { id, title ->
                    onCollectionClick.invoke(id, title)
                },
                onCollectionsScreenEvent = viewModel::handleUIEvent,
            )
        }
    }
}

@Composable
fun CollectionScreen(
    collectionState: CollectionState,
    filterTypes: List<ListSortAndFilterType>,
    collectionLazyPagingItems: LazyPagingItems<WallpaperCollections>,
    onCollectionClick: (String, String) -> Unit,
    onCollectionsScreenEvent: onCollectionsScreenEvent,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberLazyListState()

    var selectedFilterType by remember { mutableStateOf<ListSortAndFilterType?>(null) }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            ShowFilterOfCollections(
                collectionState = collectionState,
                onCollectionsScreenEvent = onCollectionsScreenEvent,
                scrollState = scrollState
            )
            Spacer(modifier = Modifier.width(8.dp))
            ChangeListType(
                collectionState = collectionState,
                scrollState = scrollState,
                onCollectionsScreenEvent = onCollectionsScreenEvent
            )
            Spacer(modifier = Modifier.width(4.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                state = scrollState
            ) {
                items(filterTypes) { item ->
                    FilterAndSortListItems(
                        item = item,
                        isSelected = item == selectedFilterType,
                        onFilterTypeClicked = { selectedItem ->
                            selectedFilterType = selectedItem
                            when (selectedItem) {
                                ListSortAndFilterType(R.string.text_private_photos) -> {
                                    onCollectionsScreenEvent.invoke(CollectionScreenEvent.FetchPrivatePhotos)
                                }
                                ListSortAndFilterType(R.string.text_total_photos) -> {
                                    onCollectionsScreenEvent.invoke(CollectionScreenEvent.FetchTotalPhotos)
                                }
                            }
                        },
                        onDisableClicked = { selectedItem ->
                            if (selectedItem == selectedFilterType) {
                                selectedFilterType = null
                            }
                        }
                    )
                }
            }
        }
        CollectionPagingList(collectionLazyPagingItems, collectionState, onCollectionClick)
    }
}

@Composable
fun CollectionPagingList(
    collectionLazyPagingItems: LazyPagingItems<WallpaperCollections>,
    collectionState: CollectionState,
    onCollectionClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagingListState = rememberLazyGridState()
    val pagingListColumnSize by remember(collectionState.collectionsListType) {
        derivedStateOf {
            when (collectionState.collectionsListType) {
                ListType.VERTICAL -> GridCells.Fixed(1)
                ListType.GRID -> GridCells.Fixed(2)
            }
        }
    }
    LazyVerticalGrid(
        columns = pagingListColumnSize,
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        state = pagingListState,
        verticalArrangement = Arrangement.Center
    ) {
        items(
            count = collectionLazyPagingItems.itemCount,
            key = collectionLazyPagingItems.itemKey { item: WallpaperCollections -> item.id.hashCode() },
            contentType = collectionLazyPagingItems.itemContentType { "Collections" }) { index: Int ->
            val collections: WallpaperCollections? = collectionLazyPagingItems[index]
            if (collections != null) {
                CollectionItem(
                    collectionState = collectionState,
                    collections = collections,
                    onCollectionItemClick = { id, title ->
                        onCollectionClick.invoke(id, title)
                    }
                )
            }
        }
        collectionLazyPagingItems.apply {
            when {
                loadState.source.refresh is LoadState.Loading -> {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                loadState.refresh is LoadState.Error || loadState.append is LoadState.Error -> {
                    val errorMessage =
                        (loadState.refresh as? LoadState.Error)?.error?.localizedMessage.orEmpty()
                    item(span = { GridItemSpan(2) }) {
                        Text(text = errorMessage)
                    }
                }

                loadState.source.append is LoadState.Loading -> {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChangeListType(
    collectionState: CollectionState,
    scrollState: LazyListState,
    onCollectionsScreenEvent: onCollectionsScreenEvent,
) {
    val typeName = stringResource(id = R.string.text_over_view)
    var changeListPresentation by remember { mutableStateOf(false) }
    val textWidth = measureTextWidth(
        text = typeName,
        TextStyle(fontFamily = regular, fontSize = 16.sp)
    )
    val scrollProgress = scrollState.scrollProgress()

    val animatedWidth by animateDpAsState(
        targetValue = (textWidth + 2.dp) * (1 - scrollProgress),
        animationSpec = tween(30, easing = LinearEasing),
        label = "Width Animation"
    )
    val isVisible by remember {
        derivedStateOf {
            scrollProgress > 0.9f && animatedWidth > 2.dp
        }
    }
    LaunchedEffect(changeListPresentation) {
        if (changeListPresentation) {
            onCollectionsScreenEvent.invoke(
                CollectionScreenEvent.ChangeListType(
                    ListType.VERTICAL
                )
            )

        } else {
            onCollectionsScreenEvent.invoke(
                CollectionScreenEvent.ChangeListType(
                    ListType.GRID
                )
            )
        }
    }
    AssistChip(
        onClick = {
            changeListPresentation = !changeListPresentation
        },
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    space = 4.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                modifier = Modifier.animateContentSize()
            ) {
                Icon(
                    painter = when (collectionState.collectionsListType) {
                        ListType.VERTICAL -> painterResource(id = R.drawable.grid_4_svgrepo_com)
                        ListType.GRID -> painterResource(
                            id = R.drawable.grid_2_horizontal_svgrepo_com
                        )
                    }, contentDescription = "", modifier = Modifier.size(20.dp)
                )
                AnimatedVisibility(
                    isVisible,
                    enter = fadeIn(animationSpec = tween(150)),
                    exit = fadeOut(animationSpec = tween(150))
                ) {
                    Text(
                        text = typeName,
                        textAlign = TextAlign.Center,
                        fontFamily = regular,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .requiredHeightIn(min = 12.dp, max = 20.dp)
                            .width(animatedWidth)
                    )
                }
            }
        },
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.onBackground),
        shape = RoundedCornerShape(CornerSize(16.dp)),
        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface),
    )
}

@Composable
fun FilterAndSortListItems(
    onFilterTypeClicked: (ListSortAndFilterType) -> Unit,
    onDisableClicked: (ListSortAndFilterType) -> Unit,
    item: ListSortAndFilterType,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {

    FilterChip(
        onClick = {
            onFilterTypeClicked.invoke(item)
        },
        selected = isSelected,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.animateContentSize()
            ) {
                Text(
                    text = stringResource(item.type),
                    fontFamily = regular,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 16.sp
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = {
                            onDisableClicked.invoke(item)
                        },modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
        },
        border = FilterChipDefaults.filterChipBorder(
            borderWidth = 1.dp,
            selectedBorderWidth = 1.dp,
            enabled = isSelected,
            selected = isSelected,
            disabledBorderColor = MaterialTheme.colorScheme.onBackground,
            selectedBorderColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(CornerSize(16.dp)),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            selectedContainerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .padding(horizontal = 4.dp)

    )
}