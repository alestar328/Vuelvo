package com.delta.vuelvo.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.delta.vuelvo.ui.theme.VuInk
import com.delta.vuelvo.ui.theme.VuInk2

/** Screen header: brand row + large title + optional subtitle. */
@Composable
fun Header(title: String, subtitle: String? = null) {
    Column(Modifier.padding(top = 20.dp, bottom = 18.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VuelvoMark(26.dp)
            Text(
                "Vuelvo",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
                color = VuInk,
            )
        }
        Text(
            title,
            modifier = Modifier.padding(top = 16.dp),
            fontSize = 33.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.9).sp,
            color = VuInk,
        )
        if (subtitle != null) {
            Text(
                subtitle,
                modifier = Modifier.padding(top = 5.dp),
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Medium,
                color = VuInk2,
            )
        }
    }
}
