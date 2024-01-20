package com.jdm.designsystem.kit

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.jdm.designsystem.theme.JdsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleLineTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value ,
        onValueChange =  onValueChange,
        singleLine = true,
        maxLines = 1,
        enabled = enabled,
        textStyle = JdsTheme.typography.P_S,
        placeholder = {
            ParagraphS(text = hint, textColor = JdsTheme.colors.gray400)
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = JdsTheme.colors.black,
            unfocusedBorderColor = JdsTheme.colors.gray200
        ),
        shape = RoundedCornerShape(8.dp)


    )

}