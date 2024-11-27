@file:OptIn(ExperimentalMaterial3Api::class)
package com.oguzdogdu.walliescompose.features.collections.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.features.collections.CollectionScreenEvent
import com.oguzdogdu.walliescompose.features.collections.CollectionState
import com.oguzdogdu.walliescompose.features.collections.onCollectionsScreenEvent
import com.oguzdogdu.walliescompose.ui.theme.medium
import com.oguzdogdu.walliescompose.ui.theme.regular
import com.oguzdogdu.walliescompose.util.measureTextWidth
import com.oguzdogdu.walliescompose.util.scrollProgress
import kotlinx.coroutines.launch


@Composable
fun ShowFilterOfCollections(
    collectionState: CollectionState,
    onCollectionsScreenEvent: onCollectionsScreenEvent,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val typeName = stringResource(id = R.string.text_sort)
    val sortTypeList = listOf(
        stringResource(R.string.text_recommended_ranking),
        stringResource(id = R.string.text_alphabetic_sort),
        stringResource(id = R.string.text_likes_sort),
        stringResource(R.string.text_updated_date)
    )

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

    AssistChip(
        onClick = {
        onCollectionsScreenEvent.invoke(
            CollectionScreenEvent.OpenFilterBottomSheet(
                true
            )
        )
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
                    painter = painterResource(id = R.drawable.ic_sort),
                    modifier = Modifier.size(20.dp),
                    contentDescription = null
                )

                AnimatedVisibility(
                    isVisible,
                    enter = fadeIn(animationSpec = tween(150)),
                    exit = fadeOut(animationSpec = tween(150))
                ) {
                    Text(
                        text = typeName,
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
        colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surface)
    )


        FilterDialog(
            typeOfFilters = sortTypeList,
            isOpen = collectionState.sheetState,
            onItemClick = { id ->
                when (id) {
                    0 -> onCollectionsScreenEvent.invoke(CollectionScreenEvent.FetchLatestData)

                    1 -> onCollectionsScreenEvent.invoke(CollectionScreenEvent.SortByTitles)

                    2 -> onCollectionsScreenEvent.invoke(CollectionScreenEvent.SortByLikes)

                    3 -> onCollectionsScreenEvent.invoke(CollectionScreenEvent.SortByUpdatedDate)
                }

                onCollectionsScreenEvent.invoke(CollectionScreenEvent.OpenFilterBottomSheet(false))
            }, onDismiss = {
                onCollectionsScreenEvent.invoke(CollectionScreenEvent.OpenFilterBottomSheet(false))
            }, choisedFilter = collectionState.choisedFilter
        )
    }

@Composable
fun FilterDialog(
    typeOfFilters: List<String>,
    isOpen: Boolean,
    choisedFilter:Int,
    onItemClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()

    if (isOpen) {
        ModalBottomSheet(
            modifier = modifier,
            sheetState = bottomSheetState,
            onDismissRequest = {
                scope.launch {
                    onDismiss.invoke()
                    bottomSheetState.hide()
                }
            },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BottomSheetContent(typeOfFilters = typeOfFilters, clickedItem = {
                    scope.launch {
                        onItemClick.invoke(it)
                        bottomSheetState.hide()
                    }
                }, onDismiss = {
                    onDismiss.invoke()
                }, choisedFilter = choisedFilter)
            }
        }
    }
}

@Composable
fun BottomSheetContent(
    typeOfFilters: List<String>,
    clickedItem: (Int) -> Unit,
    onDismiss: () -> Unit,
    choisedFilter: Int,
    modifier: Modifier = Modifier
) {
    var selectedOption by rememberSaveable {
        mutableStateOf(typeOfFilters[choisedFilter])
    }
    var indexOfFilter by remember {
        mutableIntStateOf(0)
    }

    var showButton by remember {
        mutableStateOf(false)
    }

        Column(
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 8.dp),
            ) {
                itemsIndexed(typeOfFilters) { index, item ->
                    FilterRow(
                        title = item,
                        selected = item == selectedOption,
                        clickButton = {
                            selectedOption = it
                            indexOfFilter = index
                            showButton = true
                        }
                    )
                }
            }
            if (showButton) {
                Button(
                    onClick = {
                        clickedItem.invoke(indexOfFilter)
                        onDismiss.invoke()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.text_apply),
                        fontSize = 14.sp,
                        fontFamily = medium,
                        color = Color.Black
                    )
                }
            }
        }
    }

@Composable
fun FilterRow(title: String, selected: Boolean, clickButton: (String) -> Unit, modifier: Modifier = Modifier) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    clickButton.invoke(title)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            RadioButton(
                selected = selected,
                onClick = { clickButton.invoke(title) },
                colors = RadioButtonColors(
                    selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                    unselectedColor = Color.Gray,
                    disabledSelectedColor = Color.Transparent,
                    disabledUnselectedColor = Color.Transparent
                )
            )
            Text(title,style = MaterialTheme.typography.titleSmall)
        }
    }