package com.oguzdogdu.walliescompose.features.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.ui.theme.medium
import com.oguzdogdu.walliescompose.util.ListItem
import com.oguzdogdu.walliescompose.util.MenuRow

@Composable
fun MenuRowItems(modifier: Modifier, menuRow: MenuRow,arrow:Boolean) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = modifier.fillMaxWidth()) {
            Row(
                modifier = modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                menuRow.icon?.let {
                    Image(painter = painterResource(id = it), contentDescription = "")
                }
                Spacer(modifier = modifier.size(8.dp))
                menuRow.titleRes?.let {
                    Text(
                        modifier = modifier.padding(start = 8.dp),
                        text = stringResource(id = it),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

            }
            if (arrow) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_small),
                    contentDescription = "",
                    modifier = modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}
@Composable
fun RowOfSettingOptions(
    onClickToItem: (ListItem.Content) -> Unit,
    customIcon: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    listItem: ListItem.Content,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(8.dp)
            .clickable {
                onClickToItem(listItem)
            }
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row (modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(4f).padding(vertical = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = modifier.wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    listItem.icon?.let {
                        Image(painter = painterResource(id = it), contentDescription = "")
                        Spacer(modifier = modifier.size(8.dp))
                    }
                    listItem.title?.let {
                        Text(
                            modifier = modifier.padding(start = 8.dp),
                            text = stringResource(id = it),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
                listItem.description?.let {
                    Spacer(modifier = modifier.size(8.dp))
                    Text(
                        modifier = modifier.padding(horizontal = 8.dp),
                        text = stringResource(id = it),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = medium,
                        fontSize = 14.sp
                    )
                }
            }
            if (listItem.arrow) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_small),
                    contentDescription = "",
                    modifier = Modifier.wrapContentSize().padding(vertical = 8.dp)
                )
            }
            if (listItem.customIcon) {
                customIcon.invoke(Modifier.wrapContentSize().padding(vertical = 8.dp))
            }
        }
    }
}
