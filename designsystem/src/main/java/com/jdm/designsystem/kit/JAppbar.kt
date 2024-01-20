package com.jdm.designsystem.kit

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jdm.designsystem.theme.JdsTheme

@Composable
fun JAppbar(
    title: String,
    color: Color = JdsTheme.colors.gray50,
    @DrawableRes leftIcon: Int? = null,
    @DrawableRes rightIcon: Int? = null,
    onClickLeftIcon: () -> Unit = {},
    onClickRightIcon: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color = color),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        if (leftIcon != null) {
            Image(
                modifier = Modifier.clickable { onClickLeftIcon() },
                painter = painterResource(id = leftIcon), contentDescription = "left icon")
            Spacer(modifier = Modifier.width(8.dp))
        }
        LabelL(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = title, textColor = JdsTheme.colors.black)
        if (rightIcon != null) {
            Image(
                modifier = Modifier.clickable { onClickRightIcon() },
                painter = painterResource(id = rightIcon), contentDescription = "left icon")
            Spacer(modifier = Modifier.width(20.dp))
        }

    }
}
@Composable
fun JAppbar(
    title: String,
    color: Color = JdsTheme.colors.gray50,
    @DrawableRes leftIcon: Int? = null,
    rightText: String? = null,
    onClickLeftIcon: () -> Unit = {},
    onClickRightIcon: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color = color),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        if (leftIcon != null) {
            Image(
                modifier = Modifier.clickable { onClickLeftIcon() },
                painter = painterResource(id = leftIcon), contentDescription = "left icon")
            Spacer(modifier = Modifier.width(8.dp))
        }
        LabelL(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = title, textColor = JdsTheme.colors.black)
        if (rightText != null) {
            LabelL(text = rightText, textColor = JdsTheme.colors.purple400)
            Spacer(modifier = Modifier.width(20.dp))
        }

    }
}