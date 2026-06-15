package com.jdm.alarmlocation.presentation.ui.compose.alarm

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jdm.alarmlocation.domain.model.LocationRoutine
import com.jdm.alarmlocation.presentation.ui.compose.ComposeMainActivity
import com.jdm.alarmlocation.presentation.ui.compose.theme.AlarmLocationTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 알람 발생 풀스크린 (06). 잠금화면 위에서도 표시되도록 turnScreenOn/showWhenLocked 설정.
 * 지오펜스 트리거 → full-screen intent 로 본 액티비티 호출 연결은 후속 단계.
 */
@AndroidEntryPoint
class AlarmFullScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        setContent {
            AlarmLocationTheme {
                AlarmFullScreen(
                    onClose = { finish() },
                    onOpenCharge = {
                        startActivity(Intent(this, ComposeMainActivity::class.java))
                        finish()
                    },
                )
            }
        }
    }

    companion object {
        /** 발생 시점에 이미 차감된 [remaining] 잔량과 자동 OFF 여부를 함께 전달한다. */
        fun getIntent(context: Context, routine: LocationRoutine, remaining: Int): Intent =
            Intent(context, AlarmFullScreenActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(AlarmFullScreenViewModel.KEY_ROUTINE_ID, routine.id)
                putExtra(AlarmFullScreenViewModel.KEY_TITLE, routine.title)
                putExtra(AlarmFullScreenViewModel.KEY_DIRECTION, routine.direction.ordinal)
                putExtra(AlarmFullScreenViewModel.KEY_RADIUS, routine.radiusMeters)
                putExtra(AlarmFullScreenViewModel.KEY_REMAINING, remaining)
                putExtra(AlarmFullScreenViewModel.KEY_AUTO_OFF, remaining <= 0)
            }
    }
}
