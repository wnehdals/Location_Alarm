package com.jdm.designsystem.kit

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jdm.designsystem.R
import com.jdm.designsystem.theme.JdsTheme
import java.util.Calendar


@Composable
fun JPopup(
    title: String,
    message: String,
    leftText: String? = null,
    rightText: String,
    leftClick: (() -> Unit)? = null,
    rightClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Popup(
        title = title,
        message = message,
        buttonContents = {
            if (!leftText.isNullOrEmpty()) {
                if (leftClick != null) {
                    JButtonS(
                        modifier = Modifier.weight(1.0f),
                        onClick = leftClick,
                        text = leftText,
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
            JButtonF(
                modifier = Modifier.weight(1.0f),
                onClick = rightClick,
                text = rightText,
            )
        },
        onDismiss = onDismiss
    )
}

@Composable
fun PermissionDialog(
    title: String,
    permissionTitle: String,
    @DrawableRes permissionIcon: Int,
    message: String,
    rightText: String,
    rightClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = JdsTheme.colors.white
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                LabelL(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    text = title, textColor = JdsTheme.colors.black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Divider(
                    thickness = 1.dp,
                    color = JdsTheme.colors.gray50
                )
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = permissionIcon),
                            contentDescription = "permissionIcon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LabelL(text = permissionTitle, textColor = JdsTheme.colors.black)
                    }

                    ParagraphM(
                        modifier = Modifier.padding(start = 28.dp),
                        text = message, textColor = JdsTheme.colors.black
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    JButtonF(
                        modifier = Modifier.weight(1.0f),
                        onClick = rightClick,
                        text = rightText,
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

    }
}



@Composable
internal fun Popup(
    title: String,
    message: String,
    buttonContents: @Composable RowScope.() -> Unit,
    arrowContents: @Composable (RowScope.() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = JdsTheme.colors.white
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                LabelL(text = title, textColor = JdsTheme.colors.black)
                Spacer(modifier = Modifier.height(16.dp))
                Divider(
                    thickness = 1.dp,
                    color = JdsTheme.colors.gray50
                )
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    ParagraphM(text = message, textColor = JdsTheme.colors.black)
                }
                if (arrowContents != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        arrowContents()
                    }

                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    buttonContents()

                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

    }
}

