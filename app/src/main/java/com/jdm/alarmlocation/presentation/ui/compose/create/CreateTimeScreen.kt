package com.jdm.alarmlocation.presentation.ui.compose.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdm.alarmlocation.domain.model.AlarmMethod
import com.jdm.alarmlocation.presentation.ui.compose.base.CollectEffect
import com.jdm.alarmlocation.presentation.ui.compose.components.AppTopBar
import com.jdm.alarmlocation.presentation.ui.compose.components.PrimaryButton
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme
import com.jdm.alarmlocation.presentation.ui.compose.util.DAY_LABELS
import com.jdm.alarmlocation.presentation.ui.compose.util.toHourMinuteLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTimeScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CreateTimeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            CreateTimeEffect.Saved -> onSaved()
            is CreateTimeEffect.ShowMessage -> snackbar.showSnackbar(effect.message)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(AppTheme.colors.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(
                title = "시간 · 알림 설정",
                onBack = onBack,
                trailing = {
                    Text("2 / 2", style = AppTheme.typography.caption, color = AppTheme.colors.textSub)
                },
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                LocationSummary(title = state.title, address = state.address)
                Spacer(Modifier.height(24.dp))

                SectionLabel("요일")
                Spacer(Modifier.height(12.dp))
                DaySelector(
                    selected = state.days,
                    onToggle = { viewModel.onIntent(CreateTimeIntent.ToggleDay(it)) },
                )

                Spacer(Modifier.height(28.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = state.timeRangeEnabled,
                        onCheckedChange = { viewModel.onIntent(CreateTimeIntent.SetTimeRangeEnabled(it)) },
                        colors = CheckboxDefaults.colors(checkedColor = AppTheme.colors.primary),
                    )
                    Text("시간 범위 설정 (선택)", style = AppTheme.typography.title, color = AppTheme.colors.text)
                }
                if (state.timeRangeEnabled) {
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TimeField(
                            label = "시작",
                            value = state.startMinute.toHourMinuteLabel(),
                            onClick = { viewModel.onIntent(CreateTimeIntent.OpenTimePicker(TimeField.START)) },
                            modifier = Modifier.weight(1f),
                        )
                        TimeField(
                            label = "종료",
                            value = state.endMinute.toHourMinuteLabel(),
                            onClick = { viewModel.onIntent(CreateTimeIntent.OpenTimePicker(TimeField.END)) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "시작과 종료가 같으면 알람이 울리지 않아요.",
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.warningText,
                    )
                }

                Spacer(Modifier.height(28.dp))
                SectionLabel("알림 방식")
                Spacer(Modifier.height(12.dp))
                MethodSegment(
                    selected = state.method,
                    onSelect = { viewModel.onIntent(CreateTimeIntent.SetMethod(it)) },
                )
                Spacer(Modifier.height(24.dp))
            }

            Box(modifier = Modifier.padding(20.dp)) {
                PrimaryButton(
                    text = if (state.isEdit) "수정 완료" else "저장하기",
                    onClick = { viewModel.onIntent(CreateTimeIntent.Save) },
                    enabled = state.canSave,
                    loading = state.isSaving,
                )
            }
        }

        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }

    // 04-3 Time Picker
    state.editingTimeField?.let { field ->
        val initial = if (field == TimeField.START) state.startMinute else state.endMinute
        val pickerState = rememberTimePickerState(
            initialHour = initial / 60,
            initialMinute = initial % 60,
            is24Hour = false,
        )
        ModalBottomSheet(
            onDismissRequest = { viewModel.onIntent(CreateTimeIntent.DismissTimePicker) },
            containerColor = AppTheme.colors.surface,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TimePicker(state = pickerState)
                Spacer(Modifier.height(12.dp))
                PrimaryButton(
                    text = "확인",
                    onClick = {
                        viewModel.onIntent(
                            CreateTimeIntent.SetTime(field, pickerState.hour, pickerState.minute),
                        )
                    },
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // D8 시간 검증 (E-10)
    if (state.showTimeError) {
        AlertDialog(
            onDismissRequest = { viewModel.onIntent(CreateTimeIntent.DismissTimeError) },
            title = { Text("시간을 확인해주세요", style = AppTheme.typography.title) },
            text = {
                Text(
                    "시작과 종료가 같으면 알람이 울리지 않아요. 종료 시간을 시작보다 뒤로 조정하거나 시간 범위를 해제해주세요.",
                    style = AppTheme.typography.body,
                    color = AppTheme.colors.textSub,
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onIntent(CreateTimeIntent.DismissTimeError) }) {
                    Text("확인", color = AppTheme.colors.primary)
                }
            },
            shape = AppTheme.shapes.dialog,
            containerColor = AppTheme.colors.surface,
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = AppTheme.typography.title, color = AppTheme.colors.text)
}

@Composable
private fun LocationSummary(title: String, address: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.surface, AppTheme.shapes.card)
            .border(1.dp, AppTheme.colors.border, AppTheme.shapes.card)
            .padding(16.dp),
    ) {
        Text(
            title.ifBlank { "선택한 위치" },
            style = AppTheme.typography.title,
            color = AppTheme.colors.text,
        )
        if (address.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(address, style = AppTheme.typography.bodySmall, color = AppTheme.colors.textSub)
        }
    }
}

@Composable
private fun DaySelector(selected: Set<Int>, onToggle: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        DAY_LABELS.forEachIndexed { index, label ->
            val isSelected = selected.contains(index)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .size(44.dp)
                    .background(
                        if (isSelected) AppTheme.colors.primary else AppTheme.colors.surface,
                        CircleShape,
                    )
                    .border(
                        1.dp,
                        if (isSelected) AppTheme.colors.primary else AppTheme.colors.border,
                        CircleShape,
                    )
                    .clickable { onToggle(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    label,
                    style = AppTheme.typography.body,
                    color = if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.textSub,
                )
            }
        }
    }
}

@Composable
private fun TimeField(label: String, value: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(AppTheme.colors.surface, AppTheme.shapes.input)
            .border(1.dp, AppTheme.colors.border, AppTheme.shapes.input)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Text(label, style = AppTheme.typography.caption, color = AppTheme.colors.textSub)
        Spacer(Modifier.height(2.dp))
        Text(value, style = AppTheme.typography.titleLarge, color = AppTheme.colors.text)
    }
}

@Composable
private fun MethodSegment(selected: AlarmMethod, onSelect: (AlarmMethod) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.background, AppTheme.shapes.segment)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SegmentItem("알람", selected == AlarmMethod.ALARM, { onSelect(AlarmMethod.ALARM) }, Modifier.weight(1f))
        SegmentItem("앱 푸시", selected == AlarmMethod.PUSH, { onSelect(AlarmMethod.PUSH) }, Modifier.weight(1f))
    }
}

@Composable
private fun SegmentItem(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(if (selected) AppTheme.colors.surface else Color.Transparent, AppTheme.shapes.input)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = AppTheme.typography.button,
            color = if (selected) AppTheme.colors.primary else AppTheme.colors.textSub,
            textAlign = TextAlign.Center,
        )
    }
}
