package com.jdm.designsystem.kit

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
internal fun HImageToggleButton(
    checked: Boolean,
    modifier: Modifier = Modifier,
    @DrawableRes checkedRes: Int,
    @DrawableRes unCheckedRes: Int,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = modifier.clickable {
            onCheckedChange(!checked)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (checked) {
            Image(painter = painterResource(id = checkedRes), contentDescription = "check checkbox")
        } else {
            Image(painter = painterResource(id = unCheckedRes), contentDescription = "uncheck checkbox")
        }
        if (content != null) {
            Spacer(modifier = Modifier.width(8.dp))
            content()
        }
    }
}