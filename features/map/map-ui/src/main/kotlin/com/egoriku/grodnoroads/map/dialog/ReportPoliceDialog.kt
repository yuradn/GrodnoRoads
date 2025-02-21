package com.egoriku.grodnoroads.map.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.egoriku.grodnoroads.foundation.theme.GrodnoRoadsPreview
import com.egoriku.grodnoroads.foundation.theme.GrodnoRoadsTheme
import com.egoriku.grodnoroads.map.dialog.common.CommonReportDialog
import com.egoriku.grodnoroads.map.domain.model.MapEventType
import com.egoriku.grodnoroads.map.domain.model.MapEventType.TrafficPolice
import com.egoriku.grodnoroads.resources.R
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ReportDialog(
    onClose: () -> Unit,
    onSend: (
        mapEvent: MapEventType,
        shortMessage: String,
        message: String
    ) -> Unit
) {
    // TODO: Add localization
    val actions by remember {
        mutableStateOf(
            mapOf(
                "ГАИ" to TrafficPolice,
                "Работают с радаром" to TrafficPolice,
                "Проверка документов" to TrafficPolice,
                "Остановили на ходу" to TrafficPolice,
                "Сидят в машине" to TrafficPolice,
                "Работают на ходу" to TrafficPolice,
                "Транспортная инспекция" to TrafficPolice,
                "Фильтр" to TrafficPolice,
                "Регулировщик" to TrafficPolice
            )
        )
    }

    CommonReportDialog(
        titleRes = R.string.dialog_report_police,
        actions = actions.keys.toImmutableList(),
        onClose = onClose,
        onSelected = { index, inputText ->
            val pair = actions.toList()[index]

            val eventType = pair.second
            val shortMessage = pair.first

            onSend(
                eventType,
                shortMessage,
                inputText.ifEmpty { shortMessage }
            )
        }
    )
}

@GrodnoRoadsPreview
@Composable
fun PreviewReportDialog() {
    GrodnoRoadsTheme {
        ReportDialog(onClose = {}, onSend = { _, _, _ -> })
    }
}