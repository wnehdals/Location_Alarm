package com.jdm.designsystem.kit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.jdm.designsystem.R
import com.jdm.designsystem.theme.JdsTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onCancel: () -> Unit,
    onConfirm: (Calendar) -> Unit,
    modifier: Modifier = Modifier
) {

    val time = Calendar.getInstance()
    time.timeInMillis = System.currentTimeMillis()

    val timeState: TimePickerState = rememberTimePickerState(
        initialHour = time[Calendar.HOUR_OF_DAY],
        initialMinute = time[Calendar.MINUTE],
    )

    fun onConfirmClicked() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, timeState.hour)
        cal.set(Calendar.MINUTE, timeState.minute)
        cal.isLenient = false

        onConfirm(cal)
    }

    PickerDialog(
        modifier = modifier,
        onDismissRequest = onCancel,
        title = { LabelL(
            text = stringResource(id = R.string.str_time_picker_title),
            textColor = JdsTheme.colors.black
        )
                },
        buttons = {
            JButtonS(
                modifier = modifier.weight(1f),
                onClick = onCancel,
                text = "취소"
            )
            Spacer(modifier = Modifier.width(20.dp))
            JButtonF(
                modifier = modifier.weight(1f),
                onClick = ::onConfirmClicked,
                text = "확인"
            )
        },
    ) {
        val contentModifier = Modifier.padding(horizontal = 24.dp)
        TimeInput(
            modifier = contentModifier,
            state = timeState,
            colors = TimePickerDefaults.colors(
                timeSelectorSelectedContainerColor = JdsTheme.colors.white,
                timeSelectorSelectedContentColor = JdsTheme.colors.black,
                timeSelectorUnselectedContainerColor = JdsTheme.colors.gray50,
                timeSelectorUnselectedContentColor = JdsTheme.colors.gray200,
                periodSelectorBorderColor = JdsTheme.colors.blue100,
                periodSelectorSelectedContainerColor = JdsTheme.colors.blue50,
                periodSelectorSelectedContentColor = JdsTheme.colors.blue400,
                periodSelectorUnselectedContainerColor = JdsTheme.colors.white,
                periodSelectorUnselectedContentColor = JdsTheme.colors.black
            )
        )

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerDialog(
    onDismissRequest: () -> Unit,
    title: @Composable () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    AlertDialog(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .height(IntrinsicSize.Min),
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = JdsTheme.colors.white
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(16.dp))
                title()
                Spacer(modifier = Modifier.height(16.dp))
                Divider(
                    thickness = 1.dp,
                    color = JdsTheme.colors.gray50
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Content
                content()
                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    buttons()
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}