package com.example.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.XmlBrandBadge
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    var hwAcceleration by remember { mutableStateOf(true) }
    var proxyEditing by remember { mutableStateOf(true) }
    var cloudRendering by remember { mutableStateOf(false) }
    var hapticFeedback by remember { mutableStateOf(true) }
    var cacheSizeMb by remember { mutableStateOf(142) }
    var showCacheClearedSnackbar by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(XmlBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, XmlBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = XmlSurface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    XmlBrandBadge(size = 54.dp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "XML Cinematic Pro",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = XmlWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Creator Studio Edition • Unlimited 4K",
                            fontSize = 11.sp,
                            color = XmlElectricCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(XmlSuccess)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "GPU Acceleration Active", fontSize = 10.sp, color = XmlSuccess)
                        }
                    }
                }
            }
        }

        // Section: Performance & Engine
        item {
            Text("PERFORMANCE & HARDWARE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = XmlTextMuted)
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, XmlBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = XmlSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingSwitchItem(
                        title = "Hardware Acceleration",
                        subtitle = "Hardware-accelerated MediaCodec for 60FPS preview",
                        checked = hwAcceleration,
                        onCheckedChange = { hwAcceleration = it }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = XmlBorder)

                    SettingSwitchItem(
                        title = "Proxy Editing Mode",
                        subtitle = "Smooth multi-track playback for high-res 4K clips",
                        checked = proxyEditing,
                        onCheckedChange = { proxyEditing = it }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = XmlBorder)

                    SettingSwitchItem(
                        title = "Cloud Render Acceleration",
                        subtitle = "Offload heavy neural upscaling to cloud cluster",
                        checked = cloudRendering,
                        onCheckedChange = { cloudRendering = it }
                    )
                }
            }
        }

        // Section: AI Intelligence
        item {
            Text("AI VIDEO ENGINES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = XmlTextMuted)
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, XmlBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = XmlSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Primary AI Provider", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = XmlWhite)
                            Text("Gemini 2.5 Flash + Local Heuristic Engine", fontSize = 11.sp, color = XmlElectricCyan)
                        }
                        Box(
                            modifier = Modifier
                                .background(XmlPurpleDark, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("CONNECTED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = XmlElectricCyan)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = XmlBorder)

                    SettingSwitchItem(
                        title = "Haptic Beat Feedback",
                        subtitle = "Vibrate device motor on timeline beat markers",
                        checked = hapticFeedback,
                        onCheckedChange = { hapticFeedback = it }
                    )
                }
            }
        }

        // Section: Storage
        item {
            Text("STORAGE & CACHE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = XmlTextMuted)
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, XmlBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = XmlSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Proxy & Temp Cache", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = XmlWhite)
                        Text("$cacheSizeMb MB used by cached frames & audio waveforms", fontSize = 11.sp, color = XmlTextSecondary)
                    }

                    Button(
                        onClick = {
                            cacheSizeMb = 0
                            showCacheClearedSnackbar = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = XmlSurfaceHighlight),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("clear_cache_btn")
                    ) {
                        Text("Clear", fontSize = 11.sp, color = XmlSunsetOrange, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // App Info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("XML Video Editor v2.5.0", fontSize = 11.sp, color = XmlTextMuted)
                Text("Crafted with Android Jetpack Compose & Cinematic AI", fontSize = 10.sp, color = XmlTextMuted)
            }
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
private fun SettingSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = XmlWhite)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 11.sp, color = XmlTextSecondary)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = XmlPurple,
                checkedTrackColor = XmlPurpleDark,
                uncheckedThumbColor = XmlTextMuted,
                uncheckedTrackColor = XmlSurfaceHighlight
            )
        )
    }
}
