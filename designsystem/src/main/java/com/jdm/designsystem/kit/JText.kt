package com.jdm.designsystem.kit

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jdm.designsystem.R
import com.jdm.designsystem.theme.JdsTheme

@Composable
fun LabelL(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    maxLines: Int = Int.MAX_VALUE,
) {
    Label(
        modifier = modifier,
        text = text,
        textColor = textColor,
        textSize = 18.sp,
        fontWeight = FontWeight.Bold,
        maxLines = maxLines
    )
}
@Composable
fun LabelM(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    maxLines: Int = Int.MAX_VALUE,
) {
    Label(
        modifier = modifier,
        text = text,
        textColor = textColor,
        textSize = 16.sp,
        fontWeight = FontWeight.Bold,
        maxLines = maxLines
    )
}
@Composable
fun LabelS(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    maxLines: Int = Int.MAX_VALUE,
) {
    Label(
        modifier = modifier,
        text = text,
        textColor = textColor,
        textSize = 14.sp,
        fontWeight = FontWeight.Bold,
        maxLines = maxLines
    )
}
@Composable
fun LabelXS(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    maxLines: Int = Int.MAX_VALUE,
) {
    Label(
        modifier = modifier,
        text = text,
        textColor = textColor,
        textSize = 12.sp,
        fontWeight = FontWeight.Bold,
        maxLines = maxLines
    )
}
@Composable
fun LabelXXS(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    maxLines: Int = Int.MAX_VALUE,
) {
    Label(
        modifier = modifier,
        text = text,
        textColor = textColor,
        textSize = 10.sp,
        fontWeight = FontWeight.Bold,
        maxLines = maxLines
    )
}

@Composable
fun ParagraphL(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    maxLines: Int = Int.MAX_VALUE,
) {
    Label(
        modifier = modifier,
        text = text,
        textColor = textColor,
        textSize = 18.sp,
        fontWeight = FontWeight.Normal,
        maxLines = maxLines
    )
}
@Composable
fun ParagraphM(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    maxLines: Int = Int.MAX_VALUE,
) {
    Label(
        modifier = modifier,
        text = text,
        textColor = textColor,
        textSize = 16.sp,
        fontWeight = FontWeight.Normal,
        maxLines = maxLines
    )
}
@Composable
fun ParagraphS(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    maxLines: Int = Int.MAX_VALUE,
) {
    Label(
        modifier = modifier,
        text = text,
        textColor = textColor,
        textSize = 14.sp,
        fontWeight = FontWeight.Normal,
        maxLines = maxLines
    )
}
@Composable
fun ParagraphXS(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    maxLines: Int = Int.MAX_VALUE,
) {
    Label(
        modifier = modifier,
        text = text,
        textColor = textColor,
        textSize = 12.sp,
        fontWeight = FontWeight.Normal,
        maxLines = maxLines
    )
}
@Composable
fun ParagraphXXS(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    maxLines: Int = Int.MAX_VALUE,
) {
    Label(
        modifier = modifier,
        text = text,
        textColor = textColor,
        textSize = 10.sp,
        fontWeight = FontWeight.Normal,
        maxLines = maxLines
    )
}


@Composable
internal fun Label(
    modifier: Modifier,
    text: String,
    textColor: Color,
    textSize: TextUnit,
    fontWeight: FontWeight,
    maxLines: Int
) {
    val textStyle = TextStyle(
        color = textColor,
        fontWeight = fontWeight,
        fontSize = textSize
    )
    Text(
        modifier = modifier,
        text = text,
        style = textStyle,
        overflow = TextOverflow.Ellipsis,
        maxLines = maxLines
    )
}
@Composable
fun SelectorText(
    modifier: Modifier = Modifier,
    text: String,
    hint: String,
    textColor: Color,
    textSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    borderColor: Color = JdsTheme.colors.gray200
) {
    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            text = if (text.isEmpty()) hint else text,
            style = TextStyle(
                color = if (text.isEmpty()) JdsTheme.colors.gray400 else textColor,
                fontWeight = fontWeight,
                fontSize = textSize
            )
        )
        Image(
            modifier = Modifier.size(14.dp),
            painter = painterResource(id =R.drawable.ic_arrow_down_black) , contentDescription = "")
        Spacer(modifier = Modifier.width(16.dp))
    }

}
@Composable
fun UnderLineText(
    modifier: Modifier = Modifier,
    text: String,
    lineColor: Color = JdsTheme.colors.black,
    textStyle: TextStyle = JdsTheme.typography.P_M
) {
    Row(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.drawBehind {
                val strokeWidthPx = 1.dp.toPx()
                val verticalOffset = size.height - 2.sp.toPx()
                drawLine(
                    color = lineColor,
                    strokeWidth = strokeWidthPx,
                    start = Offset(0f, verticalOffset),
                    end = Offset(size.width, verticalOffset)
                )
            },
            text = text,
            style = textStyle
        )
    }

}