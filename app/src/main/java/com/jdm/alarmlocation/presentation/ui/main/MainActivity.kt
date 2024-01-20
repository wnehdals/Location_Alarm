package com.jdm.alarmlocation.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.presentation.ui.add.AddAlarmActivity
import com.jdm.alarmlocation.presentation.ui.add.LocationSearchActivity
import com.jdm.alarmlocation.ui.theme.AlarmlocationTheme
import com.jdm.designsystem.kit.JAppbar
import com.jdm.designsystem.theme.JdsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlarmlocationTheme {
                // A surface container using the 'background' color from the theme
                AlarmLocationApp(
                    onClickSettingIcon = {},
                    onClickFloatingButton = {
                        val intent = Intent(this, AddAlarmActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlarmLocationApp(
    onClickSettingIcon: () -> Unit,
    onClickFloatingButton: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(color = JdsTheme.colors.gray50),
        snackbarHost = { snackbarHostState },
        floatingActionButton = {
            FloatingActionButton(
                contentColor = JdsTheme.colors.white,
                containerColor = JdsTheme.colors.purple400,
                onClick = onClickFloatingButton) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "add")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .background(color = JdsTheme.colors.gray50)
        ) {
            JAppbar(
                title = stringResource(id = R.string.str_app_name),
                rightIcon = R.drawable.ic_setting_black,
                onClickRightIcon = onClickSettingIcon
            )
        }
    }
}
