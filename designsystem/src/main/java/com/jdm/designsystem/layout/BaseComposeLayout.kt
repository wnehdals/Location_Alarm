package com.jdm.designsystem.layout

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.jdm.designsystem.theme.JdsTheme


@Composable
fun BaseVerticalScrollLayout(
    scrollState: ScrollState,
    backgroundColor: Color = Color(0xFFF6F6F6),
    contents: @Composable ColumnScope.() -> Unit
) {
    //val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(color = backgroundColor)
    ) {
        contents()
    }
}