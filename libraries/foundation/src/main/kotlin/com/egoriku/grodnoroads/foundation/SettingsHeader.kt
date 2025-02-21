package com.egoriku.grodnoroads.foundation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SettingsHeader(
    title: String,
    start: Dp = 16.dp,
    top: Dp = 48.dp,
    bottom: Dp = 4.dp
) {
    Text(
        modifier = Modifier.padding(start = start, top = top, bottom = bottom),
        style = MaterialTheme.typography.subtitle2,
        text = title,
    )
}