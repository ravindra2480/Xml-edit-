package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import kotlin.math.sin

@Composable
fun VideoPreviewEngine(
    project: ProjectData,
    currentPositionMs: Long,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onStepFrame: (Boolean) -> Unit, // true = forward, false = backward
    showSafeGuides: Boolean,
    onToggleSafeGuides: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Find active clips across tracks at currentPositionMs
    val activeClips = remember(project, currentPositionMs) {
        val list = mutableListOf<ClipItem>()
        for (track in project.tracks) {
            if (track.isHidden) continue
            for (clip in track.clips) {
                if (currentPositionMs in clip.startMs..clip.endMs) {
                    list.add(clip)
                }
            }
        }
        list
    }

    val mainVideoClip = activeClips.find { it.mediaType == MediaType.VIDEO }
    val activeTextClip = activeClips.find { it.mediaType == MediaType.TEXT }
    val activeEffect = mainVideoClip?.effect ?: activeClips.find { it.mediaType == MediaType.EFFECT }?.effect ?: EffectPreset.NONE
    val activeFilter = mainVideoClip?.filter ?: FilterLUT.NONE

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(XmlBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Video Preview Viewport with aspect ratio container
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .heightIn(min = 220.dp, max = 340.dp)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            val containerWidth = maxWidth
            val containerHeight = maxHeight

            val targetRatio = project.aspectRatio.ratio
            val previewWidth = if (containerWidth / containerHeight > targetRatio) {
                containerHeight * targetRatio
            } else {
                containerWidth
            }
            val previewHeight = previewWidth / targetRatio

            Box(
                modifier = Modifier
                    .size(width = previewWidth, height = previewHeight)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0D0F18))
                    .border(1.dp, XmlBorder, RoundedCornerShape(10.dp))
            ) {
                // Compose Canvas rendering the synthesized video frame
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val progress = (currentPositionMs.toFloat() / project.durationMs.coerceAtLeast(1L)).coerceIn(0f, 1f)

                    // Interpolate Keyframes for main clip
                    var currentScale = mainVideoClip?.scale ?: 1.0f
                    var currentRot = mainVideoClip?.rotation ?: 0f
                    var currentTransX = (mainVideoClip?.posX ?: 0f) * w
                    var currentTransY = (mainVideoClip?.posY ?: 0f) * h

                    val keyframes = mainVideoClip?.keyframes ?: emptyList()
                    if (keyframes.size >= 2) {
                        val firstKf = keyframes.first()
                        val lastKf = keyframes.last()
                        if (currentPositionMs in firstKf.timeMs..lastKf.timeMs) {
                            val span = (lastKf.timeMs - firstKf.timeMs).coerceAtLeast(1L).toFloat()
                            val frac = ((currentPositionMs - firstKf.timeMs) / span).coerceIn(0f, 1f)
                            currentScale = firstKf.scale + (lastKf.scale - firstKf.scale) * frac
                            currentRot = firstKf.rotation + (lastKf.rotation - firstKf.rotation) * frac
                        }
                    }

                    // Background color & synthetic video layer
                    val baseGrad = when (activeFilter) {
                        FilterLUT.TEAL_ORANGE -> listOf(Color(0xFF003844), Color(0xFF5A2A18), Color(0xFFFF8500))
                        FilterLUT.CYBERPUNK -> listOf(Color(0xFF240046), Color(0xFF7B2CBF), Color(0xFF00F5D4))
                        FilterLUT.MOODY_NOIR -> listOf(Color(0xFF111111), Color(0xFF333333), Color(0xFFCCCCCC))
                        FilterLUT.GOLDEN_HOUR -> listOf(Color(0xFF5E2B04), Color(0xFFC75D00), Color(0xFFFFD166))
                        FilterLUT.EMERALD_MATRIX -> listOf(Color(0xFF06281B), Color(0xFF0D5C3A), Color(0xFF00F58D))
                        FilterLUT.VINTAGE_1970 -> listOf(Color(0xFF3E2723), Color(0xFF8D6E63), Color(0xFFFFE082))
                        else -> listOf(Color(0xFF15192B), Color(0xFF282F4E), Color(0xFF1E293B))
                    }

                    // Dynamic wave animation to simulate live video movement
                    val waveOffset = (sin(currentPositionMs / 400.0) * 15f).toFloat()

                    drawRect(
                        brush = Brush.linearGradient(
                            colors = baseGrad,
                            start = Offset(0f, waveOffset),
                            end = Offset(w, h - waveOffset)
                        )
                    )

                    // Cinematic letterbox bars if in 16:9 or custom
                    if (activeFilter == FilterLUT.TEAL_ORANGE) {
                        val barHeight = h * 0.08f
                        drawRect(Color.Black, size = Size(w, barHeight))
                        drawRect(Color.Black, topLeft = Offset(0f, h - barHeight), size = Size(w, barHeight))
                    }

                    // Masking (Circle or Rectangle)
                    if (mainVideoClip?.maskType == MaskType.CIRCLE) {
                        val clipPath = Path().apply {
                            addOval(androidx.compose.ui.geometry.Rect(w * 0.15f, h * 0.15f, w * 0.85f, h * 0.85f))
                        }
                        // Draw decorative mask border
                        drawPath(clipPath, Color(0x60A855F7), style = Stroke(width = 2f))
                    }

                    // Effects overlay
                    when (activeEffect) {
                        EffectPreset.CINEMATIC_GLOW -> {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(Color(0x5500E5FF), Color.Transparent),
                                    center = Offset(w * 0.5f, h * 0.45f),
                                    radius = w * 0.6f
                                )
                            )
                        }
                        EffectPreset.GLITCH -> {
                            // Glitch scanline horizontal bars
                            val barY = ((currentPositionMs * 2) % h.toInt()).toFloat()
                            drawRect(
                                Color(0x6600E5FF),
                                topLeft = Offset(0f, barY),
                                size = Size(w, 8f)
                            )
                            drawRect(
                                Color(0x66FF0055),
                                topLeft = Offset(12f, (barY + 20) % h),
                                size = Size(w - 24, 6f)
                            )
                        }
                        EffectPreset.LIGHT_LEAK -> {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    listOf(Color(0x88FFA500), Color(0x33FF4500), Color.Transparent),
                                    center = Offset(w * 0.1f, h * 0.2f),
                                    radius = w * 0.8f
                                )
                            )
                        }
                        EffectPreset.FLASH -> {
                            if ((currentPositionMs / 250) % 2 == 0L) {
                                drawRect(Color(0x40FFFFFF))
                            }
                        }
                        EffectPreset.RGB_SPLIT -> {
                            drawRect(
                                Color(0x30FF0000),
                                topLeft = Offset(-4f, 0f),
                                size = Size(w, h)
                            )
                            drawRect(
                                Color(0x3000FFFF),
                                topLeft = Offset(4f, 0f),
                                size = Size(w, h)
                            )
                        }
                        else -> {}
                    }

                    // Safe Area Overlay Guides
                    if (showSafeGuides) {
                        // Title safe zone (80%)
                        drawRect(
                            color = Color(0x6638BDF8),
                            topLeft = Offset(w * 0.1f, h * 0.1f),
                            size = Size(w * 0.8f, h * 0.8f),
                            style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                        )
                        // Action safe zone (90%)
                        drawRect(
                            color = Color(0x66F59E0B),
                            topLeft = Offset(w * 0.05f, h * 0.05f),
                            size = Size(w * 0.9f, h * 0.9f),
                            style = Stroke(width = 1.5f)
                        )
                        // Center crosshair
                        drawLine(Color(0x80FFFFFF), Offset(w * 0.5f - 12f, h * 0.5f), Offset(w * 0.5f + 12f, h * 0.5f), strokeWidth = 1.5f)
                        drawLine(Color(0x80FFFFFF), Offset(w * 0.5f, h * 0.5f - 12f), Offset(w * 0.5f, h * 0.5f + 12f), strokeWidth = 1.5f)
                    }
                }

                // Dynamic Caption / Subtitle Overlay
                val activeSubtitle = activeTextClip?.subtitles?.find { currentPositionMs in it.startMs..it.endMs }
                val displayCaption = activeSubtitle?.text ?: activeTextClip?.textContent ?: ""

                if (displayCaption.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .padding(horizontal = 12.dp)
                            .background(
                                color = Color(0xB3090A12),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(1.dp, Color(0x40A855F7), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = displayCaption,
                                color = Color(android.graphics.Color.parseColor(activeTextClip?.textColorHex ?: "#FFFFFF")),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            if (activeSubtitle?.translation?.isNotBlank() == true) {
                                Text(
                                    text = activeSubtitle.translation,
                                    color = XmlElectricCyan,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Aspect Ratio indicator badge on top-left of preview
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color(0x99000000), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${project.aspectRatio.widthRatio}:${project.aspectRatio.heightRatio} | ${project.fps}FPS",
                        color = XmlTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Filter / Effect badge on top-right of preview
                if (activeFilter != FilterLUT.NONE || activeEffect != EffectPreset.NONE) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color(0xCC7C3AED), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (activeFilter != FilterLUT.NONE) activeFilter.title else activeEffect.title,
                            color = XmlWhite,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Preview Control Toolbar: Timecode, Step Frame Backward, Play/Pause, Step Frame Forward, Safe Guides
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Precise Timecode (HH:MM:SS:FF)
            val totalSeconds = currentPositionMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            val frames = ((currentPositionMs % 1000) * project.fps / 1000).toInt()
            val timecodeStr = String.format("%02d:%02d:%02d", minutes, seconds, frames)

            val totalDurationSec = project.durationMs / 1000
            val totalMin = totalDurationSec / 60
            val totalSec = totalDurationSec % 60
            val totalTimeStr = String.format("%02d:%02d", totalMin, totalSec)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timecodeStr,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = XmlElectricCyan
                )
                Text(
                    text = " / $totalTimeStr",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = XmlTextMuted
                )
            }

            // Transport Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Step Backward 1 Frame
                IconButton(
                    onClick = { onStepFrame(false) },
                    modifier = Modifier.size(36.dp).testTag("step_backward_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Frame",
                        tint = XmlTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Play / Pause Master
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(XmlPurple, XmlElectricCyan)))
                        .clickable { onTogglePlay() }
                        .testTag("play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = XmlBackground,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Step Forward 1 Frame
                IconButton(
                    onClick = { onStepFrame(true) },
                    modifier = Modifier.size(36.dp).testTag("step_forward_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Frame",
                        tint = XmlTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Safe Area Guides Toggle
            IconButton(
                onClick = onToggleSafeGuides,
                modifier = Modifier.size(36.dp).testTag("toggle_safe_guides")
            ) {
                Icon(
                    imageVector = Icons.Default.GridOn,
                    contentDescription = "Safe Guides",
                    tint = if (showSafeGuides) XmlAmber else XmlTextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
