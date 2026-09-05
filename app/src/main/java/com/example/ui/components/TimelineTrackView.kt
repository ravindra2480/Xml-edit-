package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun TimelineTrackView(
    project: ProjectData,
    currentPositionMs: Long,
    onSeekTo: (Long) -> Unit,
    selectedTrackId: String?,
    selectedClipId: String?,
    onSelectClip: (Track, ClipItem) -> Unit,
    zoomScale: Float, // Pixels per second
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val totalSeconds = (project.durationMs / 1000f).coerceAtLeast(5f)
    val timelineWidthDp = (totalSeconds * zoomScale).coerceAtLeast(360f).dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(XmlSurface)
            .border(1.dp, XmlBorder)
    ) {
        // Timeline Header: Quick Snap, Beat Sync Markers, Timeline Zoom (+ / -)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(XmlSurfaceElevated)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Multi-Track",
                    tint = XmlPurple,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${project.tracks.size} TRACKS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = XmlTextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Beat Sync Indicator
                Box(
                    modifier = Modifier
                        .background(Color(0x2600E5FF), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "BEAT SNAP ON",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = XmlElectricCyan
                    )
                }
            }

            // Timeline Zoom Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { onZoomChange((zoomScale * 0.8f).coerceAtLeast(30f)) },
                    modifier = Modifier.size(28.dp).testTag("timeline_zoom_out")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom Out",
                        tint = XmlTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "${zoomScale.toInt()}x",
                    fontSize = 11.sp,
                    color = XmlTextMuted,
                    fontFamily = FontFamily.Monospace
                )

                IconButton(
                    onClick = { onZoomChange((zoomScale * 1.25f).coerceAtMost(250f)) },
                    modifier = Modifier.size(28.dp).testTag("timeline_zoom_in")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom In",
                        tint = XmlTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Horizontal Scrollable Ruler and Tracks Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 240.dp)
                .horizontalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .width(timelineWidthDp)
                    .padding(bottom = 12.dp)
            ) {
                // 1. Time Ruler Bar with Tick Marks and Numbers
                TimeRulerView(
                    totalDurationMs = project.durationMs,
                    zoomScale = zoomScale,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .pointerInput(project.durationMs, zoomScale) {
                            detectTapGestures { offset ->
                                val clickedSec = offset.x / zoomScale
                                val targetMs = (clickedSec * 1000L).toLong().coerceIn(0L, project.durationMs)
                                onSeekTo(targetMs)
                            }
                        }
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 2. Track Rows (Video, Overlay, Text, Audio, Effect)
                project.tracks.forEach { track ->
                    TrackRowItem(
                        track = track,
                        totalDurationMs = project.durationMs,
                        zoomScale = zoomScale,
                        selectedClipId = selectedClipId,
                        onSelectClip = { clip -> onSelectClip(track, clip) }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            // 3. Playhead Vertical Needle spanning the entire timeline
            val playheadX = (currentPositionMs / 1000f) * zoomScale
            Box(
                modifier = Modifier
                    .offset(x = playheadX.dp - 6.dp)
                    .width(12.dp)
                    .fillMaxHeight()
                    .pointerInput(project.durationMs, zoomScale) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val deltaSec = dragAmount.x / zoomScale
                            val deltaMs = (deltaSec * 1000L).toLong()
                            val newMs = (currentPositionMs + deltaMs).coerceIn(0L, project.durationMs)
                            onSeekTo(newMs)
                        }
                    }
            ) {
                // Playhead needle head
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Needle line
                    drawLine(
                        color = XmlPlayheadColor,
                        start = Offset(size.width / 2f, 16.dp.toPx()),
                        end = Offset(size.width / 2f, size.height),
                        strokeWidth = 2.dp.toPx()
                    )
                    // Needle glowing diamond marker
                    val markerPath = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width / 2f, 0f)
                        lineTo(size.width, 8.dp.toPx())
                        lineTo(size.width / 2f, 16.dp.toPx())
                        lineTo(0f, 8.dp.toPx())
                        close()
                    }
                    drawPath(markerPath, color = XmlPurple)
                }
            }
        }
    }
}

@Composable
private fun TimeRulerView(
    totalDurationMs: Long,
    zoomScale: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.background(XmlSurfaceElevated)) {
        val totalSec = totalDurationMs / 1000f
        val secStep = if (zoomScale < 50f) 2 else 1

        for (sec in 0..totalSec.toInt() step secStep) {
            val x = sec * zoomScale
            // Major second tick
            drawLine(
                color = Color(0xFF64748B),
                start = Offset(x, size.height - 12.dp.toPx()),
                end = Offset(x, size.height),
                strokeWidth = 1.5f
            )

            // Half second sub-ticks
            if (zoomScale > 70f) {
                val halfX = (sec + 0.5f) * zoomScale
                drawLine(
                    color = Color(0xFF334155),
                    start = Offset(halfX, size.height - 6.dp.toPx()),
                    end = Offset(halfX, size.height),
                    strokeWidth = 1.0f
                )
            }
        }
    }
}

@Composable
private fun TrackRowItem(
    track: Track,
    totalDurationMs: Long,
    zoomScale: Float,
    selectedClipId: String?,
    onSelectClip: (ClipItem) -> Unit
) {
    val trackColor = when (track.type) {
        TrackType.VIDEO -> XmlVideoTrackColor
        TrackType.OVERLAY -> XmlSkyBlue
        TrackType.TEXT -> XmlTextTrackColor
        TrackType.AUDIO -> XmlAudioTrackColor
        TrackType.EFFECT -> XmlEffectTrackColor
        TrackType.STICKER -> XmlStickerTrackColor
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(Color(0xFF0F1118), RoundedCornerShape(6.dp))
            .border(0.5.dp, XmlBorder, RoundedCornerShape(6.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Track Header Pill
        Box(
            modifier = Modifier
                .width(54.dp)
                .fillMaxHeight()
                .background(trackColor.copy(alpha = 0.2f))
                .border(0.5.dp, trackColor.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = when (track.type) {
                        TrackType.VIDEO -> Icons.Default.Videocam
                        TrackType.OVERLAY -> Icons.Default.FilterFrames
                        TrackType.TEXT -> Icons.Default.Title
                        TrackType.AUDIO -> Icons.Default.MusicNote
                        TrackType.EFFECT -> Icons.Default.AutoFixHigh
                        TrackType.STICKER -> Icons.Default.EmojiEmotions
                    },
                    contentDescription = track.name,
                    tint = trackColor,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = track.type.name.take(3),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = trackColor
                )
            }
        }

        // Clips along track timeline
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            track.clips.forEach { clip ->
                val clipStartSec = clip.startMs / 1000f
                val clipDurationSec = ((clip.endMs - clip.startMs) / 1000f).coerceAtLeast(0.1f)
                val clipLeftDp = (clipStartSec * zoomScale).dp
                val clipWidthDp = (clipDurationSec * zoomScale).dp
                val isSelected = clip.id == selectedClipId

                Box(
                    modifier = Modifier
                        .offset(x = clipLeftDp)
                        .width(clipWidthDp)
                        .fillMaxHeight()
                        .padding(vertical = 2.dp, horizontal = 1.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isSelected) trackColor.copy(alpha = 0.9f) else trackColor.copy(alpha = 0.5f)
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) XmlWhite else trackColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable { onSelectClip(clip) }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Clip contents: Title + Icons + Keyframes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (clip.textContent.isNotBlank()) clip.textContent else clip.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = XmlWhite,
                            maxLines = 1
                        )

                        // If clip has keyframes or effects, show indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (clip.keyframes.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = "Keyframes",
                                    tint = XmlAmber,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            if (clip.filter != FilterLUT.NONE) {
                                Text(
                                    text = "LUT",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = XmlElectricCyan
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
