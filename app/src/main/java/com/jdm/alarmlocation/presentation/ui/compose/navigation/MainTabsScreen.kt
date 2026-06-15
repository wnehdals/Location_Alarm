package com.jdm.alarmlocation.presentation.ui.compose.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.jdm.alarmlocation.presentation.ui.compose.charge.ChargeScreen
import com.jdm.alarmlocation.presentation.ui.compose.components.AppBottomNav
import com.jdm.alarmlocation.presentation.ui.compose.components.BottomTab
import com.jdm.alarmlocation.presentation.ui.compose.list.RoutineListScreen
import com.jdm.alarmlocation.presentation.ui.compose.profile.ProfileScreen
import com.jdm.alarmlocation.presentation.ui.compose.theme.AppTheme

/**
 * 목록/충전/마이를 하나의 공용 하단 네비게이션으로 묶는 탭 호스트.
 * 생성/상세는 이 화면 위로 push 되는 별도 라우트로 유지된다.
 */
@Composable
fun MainTabsScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onLoggedOut: () -> Unit,
) {
    var tab by rememberSaveable { mutableStateOf(BottomTab.LIST) }

    Scaffold(
        containerColor = AppTheme.colors.background,
        bottomBar = { AppBottomNav(selected = tab, onSelect = { tab = it }) },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (tab) {
                BottomTab.LIST -> RoutineListScreen(
                    onNavigateToCreate = onNavigateToCreate,
                    onNavigateToDetail = onNavigateToDetail,
                    onNavigateToCharge = { tab = BottomTab.CHARGE },
                )

                BottomTab.CHARGE -> ChargeScreen(onBack = null)

                BottomTab.PROFILE -> ProfileScreen(onLoggedOut = onLoggedOut)
            }
        }
    }
}
