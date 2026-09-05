package com.example.ui.export

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ProjectData
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ExportDialog(
    project: ProjectData,
    onDismiss: () -> Unit,
    onExportComplete: (String) -> Unit
) {
    var selectedResolution by remember { mutableStateOf("1080p") }
    var selectedFps by remember { mutableIntStateOf(project.fps) }
    var selectedFormat by remember { mutableStateOf("MP4") }
    var isExporting by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableFloatStateOf(0f) }
    var currentStage by remember { mutableStateOf("Initializing GPU encoder...") }
    var isSuccess by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Dynamic file size calculation
    val durationSec = (project.durationMs / 1000f).coerceAtLeast(1f)
    val estimatedMb = remember(selectedResolution, selectedFps, selectedFormat, durationSec) {
        val baseRateMbps = when (selectedResolution) {
            "480p" -> 2.5f
            "720p" -> 5.0f
            "1080p" -> 12.0f
            "2K" -> 22.0f
            "4K" -> 45.0f
            else -> 12.0f
        }
        val fpsFactor = if (selectedFps == 60) 1.5f else if (selectedFps == 24) 0.85f else 1.0f
        val totalMb = (baseRateMbps * fpsFactor * durationSec) / 8f
        String.format("%.1f", totalMb)
    }

    Dialog(onDismissRequest = { if (!isExporting) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, XmlBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = XmlSurface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isSuccess) "Export Complete!" else "Export Project",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = XmlWhite
                    )
                    if (!isExporting) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = XmlTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isSuccess) {
                    // Success View
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = XmlSuccess,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${project.title}.$selectedFormat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = XmlWhite
                    )
                    Text(
                        text = "$selectedResolution @ ${selectedFps}FPS • $estimatedMb MB",
                        fontSize = 12.sp,
                        color = XmlElectricCyan
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            onExportComplete("${project.title}.$selectedFormat")
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("done_export_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = XmlPurple)
                    ) {
                        Text("Done & View in Gallery", fontWeight = FontWeight.Bold)
                    }
                } else if (isExporting) {
                    // Live Export Progress View
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        LinearProgressIndicator(
                            progress = { exportProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = XmlElectricCyan,
                            trackColor = XmlSurfaceHighlight
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = currentStage,
                                fontSize = 12.sp,
                                color = XmlTextSecondary
                            )
                            Text(
                                text = "${(exportProgress * 100).toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = XmlElectricCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    // Export Settings Form
                    // 1. Resolution
                    Text(
                        text = "Resolution:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = XmlTextSecondary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("480p", "720p", "1080p", "2K", "4K").forEach { res ->
                            val isSelected = selectedResolution == res
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) XmlPurple else XmlSurfaceHighlight)
                                    .border(1.dp, if (isSelected) XmlElectricCyan else XmlBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedResolution = res }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = res,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) XmlWhite else XmlTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Frame Rate
                    Text(
                        text = "Frame Rate (FPS):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = XmlTextSecondary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(24 to "24 (Cinema)", 30 to "30 (Standard)", 60 to "60 (Smooth)").forEach { (fps, label) ->
                            val isSelected = selectedFps == fps
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) XmlPurpleDark else XmlSurfaceHighlight)
                                    .border(1.dp, if (isSelected) XmlElectricCyan else XmlBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedFps = fps }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) XmlWhite else XmlTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Format
                    Text(
                        text = "Container Format:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = XmlTextSecondary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("MP4", "MOV", "Audio (MP3)").forEach { fmt ->
                            val isSelected = selectedFormat == fmt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) XmlPurpleDark else XmlSurfaceHighlight)
                                    .border(1.dp, if (isSelected) XmlSunsetOrange else XmlBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedFormat = fmt }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fmt,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) XmlWhite else XmlTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // File size & Hardware acceleration badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(XmlSurfaceHighlight, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SdStorage, contentDescription = "Size", tint = XmlSunsetOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Est. File Size: ~$estimatedMb MB", fontSize = 11.sp, color = XmlWhite, fontWeight = FontWeight.SemiBold)
                        }
                        Text("HW Accel: ON", fontSize = 10.sp, color = XmlSuccess, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Start Export Button
                    Button(
                        onClick = {
                            isExporting = true
                            coroutineScope.launch {
                                currentStage = "Compositing video & overlay tracks..."
                                delay(400)
                                exportProgress = 0.25f

                                currentStage = "Applying AI LUTs, filters & effects..."
                                delay(500)
                                exportProgress = 0.55f

                                currentStage = "Encoding H.264/AAC at $selectedResolution..."
                                delay(600)
                                exportProgress = 0.85f

                                currentStage = "Finalizing container metadata..."
                                delay(400)
                                exportProgress = 1.0f
                                isExporting = false
                                isSuccess = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("start_export_render_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = XmlPurple)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = "Export")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Export (${selectedResolution})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
