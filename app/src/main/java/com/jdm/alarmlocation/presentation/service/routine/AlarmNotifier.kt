package com.jdm.alarmlocation.presentation.service.routine

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jdm.alarmlocation.AlarmApp
import com.jdm.alarmlocation.R
import com.jdm.alarmlocation.domain.model.AlarmDirection
import com.jdm.alarmlocation.domain.model.AlarmMethod
import com.jdm.alarmlocation.domain.model.LocationRoutine
import com.jdm.alarmlocation.presentation.ui.compose.alarm.AlarmFullScreenActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 알람 발생 시 방식에 따라 분기 (SPEC §4-3, §5).
 * - ALARM(AlarmManager 강조) → 풀스크린 인텐트 알림 → [AlarmFullScreenActivity].
 * - PUSH(NotificationManager) → 일반 알림.
 */
@Singleton
class AlarmNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun fire(routine: LocationRoutine, remainingTickets: Int) {
        when (routine.method) {
            AlarmMethod.ALARM -> fireFullScreen(routine, remainingTickets)
            AlarmMethod.PUSH -> firePush(routine, remainingTickets)
        }
    }

    @SuppressLint("MissingPermission")
    private fun fireFullScreen(routine: LocationRoutine, remaining: Int) {
        val fullScreenIntent = AlarmFullScreenActivity.getIntent(context, routine, remaining)
        val pending = PendingIntent.getActivity(
            context,
            routine.id.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, AlarmApp.ALARM_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_noti)
            .setContentTitle(title(routine))
            .setContentText(body(routine))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setFullScreenIntent(pending, true)
            .setContentIntent(pending)
            .build()
        notifySafely(routine.id.toInt(), notification)
    }

    @SuppressLint("MissingPermission")
    private fun firePush(routine: LocationRoutine, remaining: Int) {
        val notification = NotificationCompat.Builder(context, AlarmApp.BASIC_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo_noti)
            .setContentTitle(title(routine))
            .setContentText("${body(routine)} · 티켓 1장 차감(남은 ${remaining}장)")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notifySafely(routine.id.toInt(), notification)
    }

    private fun title(routine: LocationRoutine): String =
        "${routine.title.ifBlank { "도착지" }} ${if (routine.direction == AlarmDirection.ENTER) "도착!" else "이탈!"}"

    private fun body(routine: LocationRoutine): String {
        val verb = if (routine.direction == AlarmDirection.ENTER) "진입했어요" else "벗어났어요"
        return "설정한 ${routine.radiusMeters}m 반경에 $verb"
    }

    private fun notifySafely(id: Int, notification: android.app.Notification) {
        runCatching { NotificationManagerCompat.from(context).notify(id, notification) }
    }
}
