package com.egoriku.grodnoroads.foundation.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.egoriku.grodnoroads.foundation.theme.GrodnoRoadsPreview
import com.egoriku.grodnoroads.foundation.theme.GrodnoRoadsTheme
import com.egoriku.grodnoroads.resources.R

@Composable
fun DialogTitle(
    @StringRes titleRes: Int,
    center: Boolean = false
) {
    val titleText = stringResource(titleRes)

    var modifier = Modifier
        .fillMaxWidth()
        .padding(start = 24.dp, end = 24.dp)
        .height(64.dp)
        .wrapContentHeight(Alignment.CenterVertically)

    modifier = modifier.then(
        Modifier.wrapContentWidth(
            if (center) {
                Alignment.CenterHorizontally
            } else {
                Alignment.Start
            }
        )
    )

    Text(
        modifier = modifier,
        text = titleText,
        color = MaterialTheme.colors.onSurface,
        style = MaterialTheme.typography.h6
    )
}

@GrodnoRoadsPreview
@Composable
private fun PreviewDialogTitle() = GrodnoRoadsTheme {
    Column(modifier = Modifier.fillMaxWidth()) {
        DialogTitle(titleRes = R.string.app_name)
        Divider()
        DialogTitle(titleRes = R.string.app_name, center = true)
    }
}