package com.jdm.alarmlocation.presentation.ui.compose.base

/** Marker for an immutable screen state rendered by a Composable. */
interface UiState

/** Marker for a user/system intent sent into an [MviViewModel]. */
interface UiIntent

/** Marker for a one-shot side effect (navigation, toast, dialog) consumed once. */
interface UiEffect
