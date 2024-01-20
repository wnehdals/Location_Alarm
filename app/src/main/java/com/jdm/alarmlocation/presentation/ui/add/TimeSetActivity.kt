package com.jdm.alarmlocation.presentation.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.ui.theme.AlarmlocationTheme
import com.jdm.core.base.BaseComposeActivity
import com.jdm.designsystem.kit.JAppbar
import com.jdm.designsystem.layout.BaseVerticalScrollLayout
import com.jdm.designsystem.theme.JdsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimeSetActivity : BaseComposeActivity() {
    private val viewModel: TimeSetViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlarmlocationTheme {

            }
        }
    }
}
@Composable
private fun TimeSetScreen(
    viewModel: TimeSetViewModel
) {
    val state by viewModel.viewState.collectAsStateWithLifecycle()
}
@Composable
private fun TimeSetScreen(
    state: TimeSetContract.TimeSetState,
    backButtonClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = JdsTheme.colors.white)
        ) {
        }
        BaseVerticalScrollLayout(
            scrollState = scrollState,
            backgroundColor = JdsTheme.colors.white
        ) {

        }
    }
}