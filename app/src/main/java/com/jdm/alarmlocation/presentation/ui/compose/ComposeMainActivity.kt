package com.jdm.alarmlocation.presentation.ui.compose

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.jdm.alarmlocation.data.draft.RoutineDraftStore
import com.jdm.alarmlocation.domain.model.AlarmDirection
import com.jdm.alarmlocation.domain.model.LocationRoutine
import com.jdm.alarmlocation.domain.model.Place
import com.jdm.alarmlocation.domain.repository.RoutineRepository
import com.jdm.alarmlocation.domain.toLatLng
import com.jdm.alarmlocation.presentation.ui.compose.navigation.AppNavHost
import com.jdm.alarmlocation.presentation.ui.compose.navigation.Routes
import com.jdm.alarmlocation.presentation.ui.compose.theme.AlarmLocationTheme
import com.jdm.alarmlocation.presentation.ui.location.SearchLocationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 재설계된 Compose 앱 호스트. 스플래시(버전 점검) → 로그인 → 권한 온보딩 → 알람 목록 → 생성/수정 플로우.
 * 위치 선택 단계만 XML+MVVM [SearchLocationActivity] 를 ActivityResult로 호출하고,
 * 결과를 [RoutineDraftStore] 초안에 반영한 뒤 Compose 생성 화면으로 진입한다.
 */
@AndroidEntryPoint
class ComposeMainActivity : ComponentActivity() {

    @Inject
    lateinit var draftStore: RoutineDraftStore

    @Inject
    lateinit var routineRepository: RoutineRepository

    /** 위치 선택(지도) 결과를 받아 초안 반영 후 생성 화면으로 이동. */
    private fun launchLocationPicker(
        launcher: ActivityResultLauncher<android.content.Intent>,
        prefill: LocationRoutine,
    ) {
        val place = if (prefill.title.isNotBlank() || prefill.id != 0L) {
            Place(
                title = prefill.title,
                roadAddress = prefill.address,
                mapx = (prefill.longitude * 10_000_000).toLong().toString(),
                mapy = (prefill.latitude * 10_000_000).toLong().toString(),
            )
        } else {
            null
        }
        val isIn = prefill.direction == AlarmDirection.ENTER
        launcher.launch(SearchLocationActivity.getIntent(this, place, prefill.radiusMeters, isIn))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlarmLocationTheme {
                val navController = rememberNavController()

                val mapLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.StartActivityForResult(),
                ) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        @Suppress("DEPRECATION")
                        val place = result.data?.getParcelableExtra<Place>(SearchLocationActivity.PARAM_PLACE)
                        val range = result.data?.getIntExtra(SearchLocationActivity.PARAM_RANGE, 50) ?: 50
                        val isIn = result.data?.getBooleanExtra(SearchLocationActivity.PARAM_DIRECTION, true) ?: true
                        if (place != null) {
                            val latLng = place.toLatLng()
                            draftStore.applyLocation(
                                title = place.title,
                                address = place.roadAddress,
                                latitude = latLng.latitude,
                                longitude = latLng.longitude,
                                radiusMeters = range,
                                direction = if (isIn) AlarmDirection.ENTER else AlarmDirection.EXIT,
                            )
                            navController.navigate(Routes.CREATE_TIME)
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    AppNavHost(
                        navController = navController,
                        onStartCreate = {
                            draftStore.start(null)
                            launchLocationPicker(mapLauncher, draftStore.draft)
                        },
                        onStartEdit = { id ->
                            lifecycleScope.launch {
                                val routine = routineRepository.get(id) ?: return@launch
                                draftStore.start(routine)
                                launchLocationPicker(mapLauncher, routine)
                            }
                        },
                        onExit = { finish() },
                    )
                }
            }
        }
    }
}
