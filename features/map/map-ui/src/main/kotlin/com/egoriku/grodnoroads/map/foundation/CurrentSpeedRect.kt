package com.egoriku.grodnoroads.map.foundation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.egoriku.grodnoroads.foundation.theme.GrodnoRoadsPreview
import com.egoriku.grodnoroads.foundation.theme.GrodnoRoadsTheme
import com.egoriku.grodnoroads.resources.R

@Composable
fun CurrentSpeedRect(
    modifier: Modifier = Modifier,
    speed: String
) {
    Card(
        modifier = modifier,
        elevation = 5.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(id = R.string.current_speed_template, speed),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .defaultMinSize(minWidth = 80.dp)
            )
        }
    }
}

@GrodnoRoadsPreview
@Composable
private fun CurrentSpeedRectPreview() {
    GrodnoRoadsTheme {
        CurrentSpeedRect(speed = 70.toString())
    }
}