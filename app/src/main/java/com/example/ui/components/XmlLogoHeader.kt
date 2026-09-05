package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun XmlBrandBadge(
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.28f))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF26193E),
                        Color(0xFF131525),
                        Color(0xFF090A10)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.sweepGradient(
                    listOf(
                        XmlPurple,
                        XmlElectricCyan,
                        XmlSunsetOrange,
                        XmlPurple
                    )
                ),
                shape = RoundedCornerShape(size * 0.28f)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle Play triangle glow in background
        Box(
            modifier = Modifier
                .size(size * 0.5f)
                .drawBehind {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.toPx() * 0.16f, size.toPx() * 0.08f)
                        lineTo(size.toPx() * 0.42f, size.toPx() * 0.25f)
                        lineTo(size.toPx() * 0.16f, size.toPx() * 0.42f)
                        close()
                    }
                    drawPath(
                        path = path,
                        brush = Brush.linearGradient(
                            listOf(Color(0x40A855F7), Color(0x2000E5FF))
                        )
                    )
                }
        )

        // Large 3D Metallic XML Letters
        Text(
            text = "XML",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Black,
            fontSize = (size.value * 0.38f).sp,
            letterSpacing = 1.sp,
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFD4DCED),
                        Color(0xFF90A1C0),
                        Color(0xFFE2E8F0)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(size.value * 1.5f, size.value * 1.5f)
                )
            )
        )

        // Mini bottom timeline indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = size * 0.08f)
                .width(size * 0.65f)
                .height(2.5.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(XmlPurple, XmlElectricCyan, XmlSunsetOrange)
                    ),
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}

@Composable
fun XmlTopBarLogo(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        XmlBrandBadge(size = 40.dp)

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "XML",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = XmlWhite,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(listOf(XmlPurple, XmlElectricCyan)),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "AI PRO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = XmlBackground,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            Text(
                text = "Cinematic Video Studio",
                fontSize = 10.sp,
                color = XmlTextSecondary,
                letterSpacing = 0.5.sp
            )
        }
    }
}
