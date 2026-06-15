package com.jdm.alarmlocation.presentation.ui.compose.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Lifecycle-aware one-shot effect collector for MVI screens.
 * Collects [effect] only while the lifecycle is at least STARTED.
 */
@Composable
fun <E : UiEffect> CollectEffect(
    effect: Flow<E>,
    onEffect: suspend (E) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    androidx.compose.runtime.LaunchedEffect(effect, lifecycleOwner) {
        scope.launch {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                effect.collect { onEffect(it) }
            }
        }
    }
}
