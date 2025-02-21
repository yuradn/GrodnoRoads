package com.egoriku.grodnoroads.map.mode.drive.alerts.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.egoriku.grodnoroads.foundation.theme.GrodnoRoadsPreview
import com.egoriku.grodnoroads.foundation.theme.GrodnoRoadsTheme
import com.egoriku.grodnoroads.map.domain.model.MessageItem
import com.egoriku.grodnoroads.map.domain.model.Source
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun MessageComponent(messages: ImmutableList<MessageItem>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        messages.forEach {
            MessageRow(messageItem = it)
        }
    }
}

@GrodnoRoadsPreview
@Composable
fun PreviewMessageComponent() {
    GrodnoRoadsTheme {
        MessageComponent(
            messages = persistentListOf(
                MessageItem(message = "Test message 1", source = Source.App),
                MessageItem(message = "Test message 2", source = Source.Viber),
                MessageItem(message = "Test message 3", source = Source.Telegram)
            )
        )
    }
}