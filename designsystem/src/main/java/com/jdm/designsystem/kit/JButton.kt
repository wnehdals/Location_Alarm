package com.jdm.designsystem.kit

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jdm.designsystem.theme.JdsTheme

@Composable
fun JButtonF(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isEnable: Boolean = true,
    text: String?,
) {
    val pressedColor = JdsTheme.colors.gray600
    val unPressedColor = JdsTheme.colors.black
    val disabledContainerColor = JdsTheme.colors.gray50
    val disabledContentColor = JdsTheme.colors.gray300
    val textColor = JdsTheme.colors.white
    TextButton(
        modifier = modifier,
        onClick = onClick,
        pressedColor = pressedColor,
        unPressedColor = unPressedColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        contentHorizontalPadding = 0.dp,
        contentVerticalPadding = 16.dp,
        isEnable = isEnable,
    ) {
        if (!text.isNullOrEmpty()) {
            LabelM(
                text = text,
                textColor = if (isEnable) textColor else disabledContentColor
            )
        }
    }
}
@Composable
internal fun JButtonS(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isEnable: Boolean = true,
    text: String?,
) {
    val pressedColor = JdsTheme.colors.gray200
    val unPressedColor = JdsTheme.colors.gray100
    val disabledContainerColor = JdsTheme.colors.gray50
    val disabledContentColor = JdsTheme.colors.gray300
    val textColor = JdsTheme.colors.black
    TextButton(
        modifier = modifier,
        onClick = onClick,
        pressedColor = pressedColor,
        unPressedColor = unPressedColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
        contentHorizontalPadding = 0.dp,
        contentVerticalPadding = 16.dp,
        isEnable = isEnable,
    ) {
        if (!text.isNullOrEmpty()) {
            LabelM(
                text = text,
                textColor = if (isEnable) textColor else disabledContentColor
            )
        }
    }
}

@Composable
internal fun TextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    pressedColor: Color,
    unPressedColor: Color,
    disabledContainerColor: Color,
    disabledContentColor: Color,
    contentVerticalPadding: Dp,
    contentHorizontalPadding: Dp,
    isEnable: Boolean,
    label: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val selector = if (isPressed) pressedColor else unPressedColor
    val color = ButtonDefaults.buttonColors(
        containerColor = selector,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor
    )
    Button(
        modifier = modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = color,
        interactionSource = interactionSource,
        enabled = isEnable,
        contentPadding = PaddingValues(
            horizontal = contentHorizontalPadding,
            vertical = contentVerticalPadding
        )
    ) {
        label()

    }
}