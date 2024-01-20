package com.jdm.alarmlocation.presentation.ui.add

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.ui.theme.AlarmlocationTheme
import com.jdm.core.base.BaseComposeActivity
import com.jdm.designsystem.attr.SelectorItem
import com.jdm.designsystem.kit.JAppbar
import com.jdm.designsystem.kit.LabelL
import com.jdm.designsystem.kit.LabelM
import com.jdm.designsystem.kit.ParagraphL
import com.jdm.designsystem.kit.ParagraphM
import com.jdm.designsystem.kit.ParagraphS
import com.jdm.designsystem.kit.SelectorText
import com.jdm.designsystem.kit.SingleLineTextField
import com.jdm.designsystem.kit.Spinner
import com.jdm.designsystem.kit.TimePickerDialog
import com.jdm.designsystem.kit.UnderLineText
import com.jdm.designsystem.layout.BaseVerticalScrollLayout
import com.jdm.designsystem.theme.JdsTheme
import com.jdm.model.NameLocation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.StringFormat
import java.util.Calendar
import com.jdm.alarmlocation.presentation.ui.add.AddAlarmContract.AddAlarmEvent
import com.jdm.alarmlocation.presentation.ui.add.AddAlarmContract.AddAlarmState
import com.jdm.alarmlocation.presentation.ui.add.AddAlarmContract.AddAlarmSideEffect.GoToLocationSearch
import com.jdm.designsystem.kit.JButtonF

@AndroidEntryPoint
class AddAlarmActivity : BaseComposeActivity() {
    private val viewModel: AddAlarmViewModel by viewModels()
    private val locationSearchActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val nameLocation: NameLocation? = it.data?.getParcelableExtra("location")
                if (nameLocation != null) {
                    viewModel.setEvent(AddAlarmContract.AddAlarmEvent.SendNameLocation(nameLocation))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subscribe()
        setContent {
            AlarmlocationTheme {
                AddAlarmScreen(
                    viewModel = viewModel,
                    appbarLeftClick = {
                        finish()
                    },
                )
            }

        }

    }
    private fun subscribe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.effect.collectLatest {
                    when (it) {
                        is AddAlarmContract.AddAlarmSideEffect.GoToLocationSearch -> {
                            val intent = Intent(this@AddAlarmActivity, LocationSearchActivity::class.java)
                            locationSearchActivityLauncher.launch(intent)
                        }
                        is AddAlarmContract.AddAlarmSideEffect.ShowToast -> {
                            Toast.makeText(this@AddAlarmActivity, it.msg, Toast.LENGTH_SHORT).show()
                        }
                        is AddAlarmContract.AddAlarmSideEffect.GoToBack -> {
                            finish()
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun AddAlarmScreen(
    viewModel: AddAlarmViewModel,
    appbarLeftClick: () -> Unit,
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
    AddAlarmScreen(
        state = state,
        appbarLeftClick = appbarLeftClick,
        setEvent = viewModel::setEvent

    )
}

@Composable
private fun AddAlarmScreen(
    state: AddAlarmState,
    appbarLeftClick: () -> Unit,
    setEvent: (AddAlarmEvent) -> Unit,
) {
    val scrollState = rememberScrollState()
    var isShowLeftTimePickerDialog by remember {
        mutableStateOf(false)
    }
    var isShowRightTimePickerDialog by remember {
        mutableStateOf(false)
    }
    val rangeList = listOf(
        Range(1, "300m", 300),
        Range(2, "600m", 600),
        Range(3, "900m", 900),
        Range(4, "1.2km", 1200),
        Range(5, "1.5km", 1500),
        Range(6, "1.8km",  1800),
        Range(7, "2.1km",  2100),
        Range(8, "2.4km",  2400),
        Range(9, "2.7km", 2700),
        Range(10, "3km",  3000),
    )
    val wayList = listOf(
        Way(0, "진입하면"),
        Way(1, "벗어나면")
    )
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = JdsTheme.colors.white)
        ) {
            JAppbar(
                title = stringResource(id = R.string.str_add_location_alarm),
                color = JdsTheme.colors.white,
                leftIcon = R.drawable.ic_back_black,
                onClickLeftIcon = appbarLeftClick,
                rightIcon = null
            )
            BaseVerticalScrollLayout(
                scrollState = scrollState,
                backgroundColor = JdsTheme.colors.white
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    /* left time */
                    TimePickText(
                        modifier = Modifier,
                        text = state.leftTimeText,
                        onClick = {
                            isShowLeftTimePickerDialog = true
                        }
                    )
                    LabelL(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = R.string.chr_between),
                        textColor = JdsTheme.colors.black
                    )
                    /* right time */
                    TimePickText(
                        modifier = Modifier,
                        text = state.rightTimeText,
                        onClick = {
                            isShowRightTimePickerDialog = true
                        }
                    )
                    ParagraphL(
                        text = stringResource(id = R.string.str_time_between),
                        textColor = JdsTheme.colors.black
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 8.dp),
                ) {
                    /* Address */
                    TimePickText(
                        text = state.nameLocation.name,
                        onClick = {
                            setEvent(AddAlarmEvent.OnClickSearchLocation)
                        },
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = JdsTheme.colors.blue400
                        ),
                        hint = "                                                                                                      "
                    )




                }
                /* range */
                Row(
                    modifier = Modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ParagraphL(
                        text = stringResource(id = R.string.str_alarm_location_guide),
                        textColor = JdsTheme.colors.black
                    )
                    Spinner(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .height(36.dp),
                        items = rangeList,
                        hint = "0m",
                        selectedItem = state.range,
                        onItemSelected = {
                            setEvent(AddAlarmEvent.OnClickRangeSelect(it as Range))
                        }
                    )

                }
                /* way */
                Row(
                    modifier = Modifier.padding(top = 8.dp, start = 20.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ParagraphL(
                        text = stringResource(id = R.string.str_rl),
                        textColor = JdsTheme.colors.black
                    )
                    Spinner(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(200.dp)
                            .height(36.dp),
                        items = wayList,
                        hint = stringResource(id = R.string.str_way),
                        selectedItem = state.way,
                        onItemSelected = {
                            setEvent(AddAlarmEvent.OnClickWay(it as Way))
                        }
                    )

                }
                ParagraphL(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 20.dp, end = 20.dp),
                    text = stringResource(id = R.string.str_alarm_location_guide2),
                    textColor = JdsTheme.colors.black
                )
                Spacer(modifier = Modifier
                    .fillMaxSize()
                    .background(color = JdsTheme.colors.white)
                    .weight(1f))
                JButtonF(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    onClick = { setEvent(AddAlarmEvent.OnClickAddAlarmLocation) },
                    text = stringResource(id = R.string.str_add_location_alarm)
                )
                Spacer(modifier = Modifier
                    .height(16.dp)
                    .background(color = JdsTheme.colors.white))



                if (isShowLeftTimePickerDialog) {
                    TimePickerDialog(onCancel = {
                        isShowLeftTimePickerDialog = false
                    }, onConfirm = { calendar ->
                        setEvent(AddAlarmEvent.OnClickLeftTimePickerConfirm(calendar))
                        isShowLeftTimePickerDialog = false
                    })
                }
                if (isShowRightTimePickerDialog) {
                    TimePickerDialog(onCancel = {
                        isShowRightTimePickerDialog = false

                    }, onConfirm = { calendar ->
                        setEvent(AddAlarmEvent.OnClickRightTimePickerConfirm(calendar))
                        isShowRightTimePickerDialog = false
                    })
                }

            }
        }
    }
}

@Composable
private fun TimePickText(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    hint: String ="            ",
    textStyle: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = JdsTheme.colors.blue400
    )
) {
    if (text.isEmpty()) {
        UnderLineText(
            modifier = modifier
                .background(color = JdsTheme.colors.gray100)
                .clickable {
                    onClick()
                },
            text = hint,
            textStyle = textStyle
        )
    } else {
        UnderLineText(
            modifier = modifier
                .background(color = JdsTheme.colors.white)
                .clickable {
                    onClick()
                },
            text = text,
            textStyle = textStyle
        )
    }
}


data class Range(
    val id: Int,
    override var text: String,
    val value: Int
) : SelectorItem() {
}
data class Way(
    val id: Int,
    override var text: String
): SelectorItem() {

}