package com.jdm.designsystem.kit

import android.widget.Spinner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jdm.designsystem.R
import com.jdm.designsystem.attr.SelectorItem
import com.jdm.designsystem.theme.JdsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Spinner(
    modifier: Modifier = Modifier,
    items: List<SelectorItem>,
    hint: String,
    selectedItem: SelectorItem? = null,
    fontSize: Int = 14,
    onItemSelected: (SelectorItem) -> Unit,
) {

    var expanded: Boolean by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        }
    ) {
        Row(
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = if (selectedItem == null) JdsTheme.colors.gray200 else JdsTheme.colors.black,
                    shape = RoundedCornerShape(8.dp)
                ).menuAnchor().clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = selectedItem?.text ?: hint,
                style = TextStyle(
                    color = if (selectedItem == null) JdsTheme.colors.gray200 else JdsTheme.colors.black,
                    fontWeight = FontWeight.Normal,
                    fontSize = fontSize.sp
                )
            )
            HImageToggleButton(
                checked = expanded,
                checkedRes = R.drawable.ic_arrow_up_black,
                unCheckedRes = R.drawable.ic_arrow_down_black,
                onCheckedChange = {
                    expanded = it
                }
            )

            Spacer(modifier = Modifier.width(16.dp))
        }
        ExposedDropdownMenu(
            modifier = Modifier.background(color = JdsTheme.colors.white),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEachIndexed { index, element ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(items[index])
                        expanded = false
                    }, text = {
                        ParagraphS(
                            text = element.text,
                            textColor = JdsTheme.colors.black
                        )
                    })
            }
        }
    }

}
