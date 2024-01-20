package com.jdm.designsystem.kit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jdm.designsystem.R
import com.jdm.designsystem.theme.JdsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconCard(
    modifier: Modifier = Modifier,
    text: String,
    onValueChange: (String) -> Unit,
    hintText: String
) {
    Box(
        modifier = modifier.background(color = Color.Transparent)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 12.dp)
                .clip(
                    shape = RoundedCornerShape(8.dp)
                )
                .background(color = JdsTheme.colors.gray50),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = onValueChange,
                singleLine = true,
                maxLines = 1,

            )
            LabelL(text = "sdfsdfsdf", textColor = JdsTheme.colors.black)
        }
        Column(
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_circle_location_blue400),
                contentDescription = ""
            )
        }

    }
}

@Preview
@Composable
fun Card() {
    JdsTheme {
        var test by remember {
            mutableStateOf("")
        }
        IconCard(
            modifier = Modifier
                .width(300.dp)
                .height(200.dp),
            text = test,
            onValueChange = {
                test = it
            },
            hintText = "플레이스 홀더"
        )
    }
}