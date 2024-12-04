package com.oguzdogdu.walliescompose.util

import com.oguzdogdu.walliescompose.R
import com.oguzdogdu.walliescompose.navigation.utils.WalliesIcons

object ReusableMenuRowLists {
    val newList = listOf(
        ListItem.Header(R.string.settings_general_header),
        ListItem.Content(
            0, title = R.string.theme_text, icon = WalliesIcons.DarkMode, arrow = true
        ),
        ListItem.Content(
            1, title = R.string.language_title_text, icon = WalliesIcons.Language, arrow = true

        ),
        ListItem.Header(R.string.settings_storage_header),
        ListItem.Content(
            2, title =  R.string.clear_cache_title,icon = WalliesIcons.Cache, arrow = false
        ),
        ListItem.Header(R.string.text_shortcuts),
        ListItem.Content(
            3,
            title = R.string.change_theme,
            description = R.string.change_theme_desc,
            arrow = false,
           customIcon = true
        )
    )
}