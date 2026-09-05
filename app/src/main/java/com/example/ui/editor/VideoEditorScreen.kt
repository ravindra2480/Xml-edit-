package com.example.ui.editor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatioType
import com.example.data.model.ClipItem
import com.example.ui.components.TimelineTrackView
import com.example.ui.components.VideoPreviewEngine
import com.example.ui.export.ExportDialog
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoEditorScreen(
    viewModel: VideoEditorViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Find selected clip object if any
    val selectedClip = remember(uiState.project, uiState.selectedTrackId, uiState.selectedClipId) {
        val track = uiState.project.tracks.find { it.id == uiState.selectedTrackId }
        track?.clips?.find { it.id == uiState.selectedClipId }
    }

    var showAspectRatioMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = XmlBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.project.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = XmlWhite,
                            maxLines = 1
                        )
                        Text(
                            text = "${uiState.project.resolution} • ${uiState.project.fps}FPS • ${uiState.project.aspectRatio.widthRatio}:${uiState.project.aspectRatio.heightRatio}",
                            fontSize = 10.sp,
                            color = XmlTextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("editor_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = XmlWhite
                        )
                    }
                },
                actions = {
                    // Aspect Ratio Selector Dropdown
                    Box {
                        IconButton(
                            onClick = { showAspectRatioMenu = true },
                            modifier = Modifier.testTag("editor_aspect_ratio_btn")
                        ) {
                            Icon(Icons.Default.AspectRatio, contentDescription = "Aspect Ratio", tint = XmlElectricCyan)
                        }
                        DropdownMenu(
                            expanded = showAspectRatioMenu,
                            onDismissRequest = { showAspectRatioMenu = false },
                            modifier = Modifier.background(XmlSurface)
                        ) {
                            AspectRatioType.values().forEach { ratio ->
                                DropdownMenuItem(
                                    text = { Text(ratio.label, color = XmlWhite, fontSize = 13.sp) },
                                    onClick = {
                                        viewModel.setAspectRatio(ratio)
                                        showAspectRatioMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Undo Button
                    IconButton(
                        onClick = { viewModel.undo() },
                        enabled = uiState.canUndo,
                        modifier = Modifier.testTag("editor_undo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Undo,
                            contentDescription = "Undo",
                            tint = if (uiState.canUndo) XmlWhite else XmlTextMuted
                        )
                    }

                    // Redo Button
                    IconButton(
                        onClick = { viewModel.redo() },
                        enabled = uiState.canRedo,
                        modifier = Modifier.testTag("editor_redo_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Redo,
                            contentDescription = "Redo",
                            tint = if (uiState.canRedo) XmlWhite else XmlTextMuted
                        )
                    }

                    // Export Button
                    Button(
                        onClick = { viewModel.setExportDialogVisible(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = XmlPurple),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 8.dp).testTag("editor_export_modal_btn")
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = "Export", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = XmlSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // AI Status Notification Banner
            AnimatedVisibility(visible = uiState.aiNotificationMessage != null) {
                uiState.aiNotificationMessage?.let { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.horizontalGradient(listOf(XmlPurpleDark, Color(0xFF1E1B4B))))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = XmlElectricCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = msg, fontSize = 12.sp, color = XmlWhite, maxLines = 2)
                        }
                        IconButton(onClick = { viewModel.clearNotification() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = XmlTextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // 1. Video Canvas Preview Viewport
            VideoPreviewEngine(
                project = uiState.project,
                currentPositionMs = uiState.currentPositionMs,
                isPlaying = uiState.isPlaying,
                onTogglePlay = { viewModel.togglePlay() },
                onStepFrame = { forward -> viewModel.stepFrame(forward) },
                showSafeGuides = uiState.showSafeGuides,
                onToggleSafeGuides = { viewModel.toggleSafeGuides() },
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Timeline Track View
            TimelineTrackView(
                project = uiState.project,
                currentPositionMs = uiState.currentPositionMs,
                onSeekTo = { viewModel.seekTo(it) },
                selectedTrackId = uiState.selectedTrackId,
                selectedClipId = uiState.selectedClipId,
                onSelectClip = { track, clip -> viewModel.selectClip(track, clip) },
                zoomScale = uiState.zoomScale,
                onZoomChange = { viewModel.setZoomScale(it) },
                modifier = Modifier.weight(1f)
            )

            // 3. Active Tool Context Panel (if opened)
            EditorToolPanels(
                activeTool = uiState.activeTool,
                onCloseTool = { viewModel.closeTool() },
                selectedClip = selectedClip,
                onUpdateClip = { viewModel.updateClip(it) },
                onExecuteAIPrompt = { viewModel.executeAIPrompt(it) },
                isAIProcessing = uiState.isAIProcessing
            )

            // 4. Primary Bottom Editing Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(XmlSurface)
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // AI Video Prompt Assistant
                EditorToolbarItem(
                    icon = Icons.Default.AutoAwesome,
                    label = "AI Tools",
                    tint = XmlElectricCyan,
                    onClick = { viewModel.openTool(ActiveEditorTool.AI_PROMPT) },
                    testTag = "tool_ai_prompt"
                )

                // Split at playhead
                EditorToolbarItem(
                    icon = Icons.Default.ContentCut,
                    label = "Split",
                    onClick = { viewModel.splitClipAtPlayhead() },
                    testTag = "tool_split"
                )

                // Speed & Velocity Curve
                EditorToolbarItem(
                    icon = Icons.Default.Speed,
                    label = "Speed",
                    onClick = { viewModel.openTool(ActiveEditorTool.SPEED_CURVE) },
                    testTag = "tool_speed"
                )

                // Cinematic Filters & LUTs
                EditorToolbarItem(
                    icon = Icons.Default.FilterVintage,
                    label = "Filters",
                    onClick = { viewModel.openTool(ActiveEditorTool.FILTER_LUT) },
                    testTag = "tool_filters"
                )

                // Effects
                EditorToolbarItem(
                    icon = Icons.Default.Flare,
                    label = "Effects",
                    onClick = { viewModel.openTool(ActiveEditorTool.EFFECTS) },
                    testTag = "tool_effects"
                )

                // Text & Smart Captions
                EditorToolbarItem(
                    icon = Icons.Default.Title,
                    label = "Text",
                    onClick = { viewModel.openTool(ActiveEditorTool.TEXT_EDITOR) },
                    testTag = "tool_text"
                )

                // Audio & Beats
                EditorToolbarItem(
                    icon = Icons.Default.GraphicEq,
                    label = "Audio",
                    onClick = { viewModel.openTool(ActiveEditorTool.AUDIO_LAB) },
                    testTag = "tool_audio"
                )

                // Mask & Chroma
                EditorToolbarItem(
                    icon = Icons.Default.CropRotate,
                    label = "Mask",
                    onClick = { viewModel.openTool(ActiveEditorTool.MASK_CHROMA) },
                    testTag = "tool_mask"
                )

                // Keyframe Animator
                EditorToolbarItem(
                    icon = Icons.Default.Diamond,
                    label = "Keyframe",
                    tint = XmlAmber,
                    onClick = { viewModel.openTool(ActiveEditorTool.KEYFRAMES) },
                    testTag = "tool_keyframes"
                )

                // Transform & Scale
                EditorToolbarItem(
                    icon = Icons.Default.OpenWith,
                    label = "Transform",
                    onClick = { viewModel.openTool(ActiveEditorTool.TRANSFORM) },
                    testTag = "tool_transform"
                )

                // Freeze Frame
                EditorToolbarItem(
                    icon = Icons.Default.AcUnit,
                    label = "Freeze",
                    onClick = { viewModel.freezeFrameAtPlayhead() },
                    testTag = "tool_freeze"
                )

                // Duplicate
                EditorToolbarItem(
                    icon = Icons.Default.ContentCopy,
                    label = "Duplicate",
                    onClick = { viewModel.duplicateSelectedClip() },
                    testTag = "tool_duplicate"
                )

                // Delete Clip
                EditorToolbarItem(
                    icon = Icons.Default.Delete,
                    label = "Delete",
                    tint = XmlError,
                    onClick = { viewModel.deleteSelectedClip() },
                    testTag = "tool_delete"
                )
            }
        }
    }

    // Export Dialog
    if (uiState.showExportDialog) {
        ExportDialog(
            project = uiState.project,
            onDismiss = { viewModel.setExportDialogVisible(false) },
            onExportComplete = { /* Saved to gallery */ }
        )
    }
}

@Composable
private fun EditorToolbarItem(
    icon: ImageVector,
    label: String,
    tint: Color = XmlTextSecondary,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 10.sp, color = XmlWhite, fontWeight = FontWeight.Medium)
    }
}
