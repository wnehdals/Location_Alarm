package com.jdm.alarmlocation.presentation.ui.compose.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jdm.alarmlocation.presentation.ui.compose.components.AppTopBar
import com.jdm.alarmlocation.presentation.ui.compose.components.SecondaryButton
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme

/** 다음 단계에서 구현될 화면(티켓 충전 05 / 상세 04-4 / 풀스크린 알람 06) 자리표시자. */
@Composable
fun ComingSoonScreen(title: String, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(AppTheme.colors.background)) {
        AppTopBar(title = title, onBack = onBack)
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("준비 중인 화면이에요", style = AppTheme.typography.title, color = AppTheme.colors.text)
            Spacer(Modifier.height(8.dp))
            Text(
                "이 화면(${title})은 다음 구현 단계에서 제공됩니다.",
                style = AppTheme.typography.body,
                color = AppTheme.colors.textSub,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(24.dp))
            SecondaryButton(text = "돌아가기", onClick = onBack, modifier = Modifier.fillMaxWidth(0.6f))
        }
    }
}
