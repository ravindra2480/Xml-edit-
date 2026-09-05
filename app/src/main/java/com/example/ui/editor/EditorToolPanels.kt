package com.example.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.data.model.*
import com.example.ui.theme.*

enum class ActiveEditorTool {
    NONE,
    AI_PROMPT,
    FILTER_LUT,
    EFFECTS,
    SPEED_CURVE,
    TEXT_EDITOR,
    AUDIO_LAB,
    MASK_CHROMA,
    KEYFRAMES,
    TRANSFORM
}

@Composable
fun EditorToolPanels(
    activeTool: ActiveEditorTool,
    onCloseTool: () -> Unit,
    selectedClip: ClipItem?,
    onUpdateClip: (ClipItem) -> Unit,
    onExecuteAIPrompt: (String) -> Unit,
    isAIProcessing: Boolean,
    modifier: Modifier = Modifier
) {
    if (activeTool == ActiveEditorTool.NONE) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(XmlSurfaceElevated)
            .border(1.dp, XmlBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(16.dp)
    ) {
        // Panel Header with Title and Close X button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = when (activeTool) {
                    ActiveEditorTool.AI_PROMPT -> "AI Cinematic Assistant"
                    ActiveEditorTool.FILTER_LUT -> "Filters & Color Grading"
                    ActiveEditorTool.EFFECTS -> "Visual Effects & Overlays"
                    ActiveEditorTool.SPEED_CURVE -> "Speed & Velocity Curve"
                    ActiveEditorTool.TEXT_EDITOR -> "Text & Smart Captions"
                    ActiveEditorTool.AUDIO_LAB -> "Audio & Beat Studio"
                    ActiveEditorTool.MASK_CHROMA -> "Masking & Chroma Key"
                    ActiveEditorTool.KEYFRAMES -> "Keyframe Animator"
                    ActiveEditorTool.TRANSFORM -> "Transform & Crop"
                    ActiveEditorTool.NONE -> ""
                },
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = XmlWhite
            )

            IconButton(
                onClick = onCloseTool,
                modifier = Modifier.size(28.dp).testTag("close_tool_panel")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = XmlTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tool Specific Panels
        when (activeTool) {
            ActiveEditorTool.AI_PROMPT -> {
                AIPromptPanel(
                    onExecuteAIPrompt = onExecuteAIPrompt,
                    isAIProcessing = isAIProcessing
                )
            }
            ActiveEditorTool.FILTER_LUT -> {
                selectedClip?.let { clip ->
                    FilterLutPanel(clip = clip, onUpdateClip = onUpdateClip)
                } ?: NoClipSelectedMessage()
            }
            ActiveEditorTool.EFFECTS -> {
                selectedClip?.let { clip ->
                    EffectsPanel(clip = clip, onUpdateClip = onUpdateClip)
                } ?: NoClipSelectedMessage()
            }
            ActiveEditorTool.SPEED_CURVE -> {
                selectedClip?.let { clip ->
                    SpeedCurvePanel(clip = clip, onUpdateClip = onUpdateClip)
                } ?: NoClipSelectedMessage()
            }
            ActiveEditorTool.TEXT_EDITOR -> {
                selectedClip?.let { clip ->
                    TextEditorPanel(clip = clip, onUpdateClip = onUpdateClip)
                } ?: NoClipSelectedMessage()
            }
            ActiveEditorTool.AUDIO_LAB -> {
                selectedClip?.let { clip ->
                    AudioLabPanel(clip = clip, onUpdateClip = onUpdateClip)
                } ?: NoClipSelectedMessage()
            }
            ActiveEditorTool.MASK_CHROMA -> {
                selectedClip?.let { clip ->
                    MaskChromaPanel(clip = clip, onUpdateClip = onUpdateClip)
                } ?: NoClipSelectedMessage()
            }
            ActiveEditorTool.KEYFRAMES -> {
                selectedClip?.let { clip ->
                    KeyframesPanel(clip = clip, onUpdateClip = onUpdateClip)
                } ?: NoClipSelectedMessage()
            }
            ActiveEditorTool.TRANSFORM -> {
                selectedClip?.let { clip ->
                    TransformPanel(clip = clip, onUpdateClip = onUpdateClip)
                } ?: NoClipSelectedMessage()
            }
            ActiveEditorTool.NONE -> {}
        }
    }
}

@Composable
private fun NoClipSelectedMessage() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Select a clip on the timeline first to adjust properties", color = XmlTextMuted, fontSize = 13.sp)
    }
}

// 1. AI Prompt Assistant Panel
@Composable
private fun AIPromptPanel(
    onExecuteAIPrompt: (String) -> Unit,
    isAIProcessing: Boolean
) {
    var promptInput by remember { mutableStateOf("") }
    val quickPrompts = listOf(
        "इस वीडियो को cinematic बना दो",
        "30 सेकंड की Reel बना दो",
        "Beat के हिसाब से cuts लगाओ",
        "इस वीडियो में Hindi captions लगाओ",
        "Teal & Orange LUT with 24fps motion blur",
        "Auto crop to 9:16 and add hook title"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Enter any editing command in Hindi or English:",
            fontSize = 12.sp,
            color = XmlTextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = promptInput,
            onValueChange = { promptInput = it },
            placeholder = { Text("e.g. इस वीडियो को cinematic बना दो...", color = XmlTextMuted) },
            modifier = Modifier.fillMaxWidth().testTag("ai_prompt_input_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = XmlSurface,
                unfocusedContainerColor = XmlSurface,
                focusedBorderColor = XmlPurple,
                unfocusedBorderColor = XmlBorder,
                focusedTextColor = XmlWhite,
                unfocusedTextColor = XmlWhite
            ),
            trailingIcon = {
                if (isAIProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = XmlElectricCyan,
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = {
                            if (promptInput.isNotBlank()) {
                                onExecuteAIPrompt(promptInput)
                            }
                        },
                        modifier = Modifier.testTag("submit_ai_prompt_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Execute", tint = XmlPurple)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text("Quick AI Prompts:", fontSize = 11.sp, color = XmlTextMuted, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(quickPrompts) { p ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(XmlSurfaceHighlight)
                        .border(1.dp, XmlBorderGlow, RoundedCornerShape(20.dp))
                        .clickable { onExecuteAIPrompt(p) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = p, fontSize = 11.sp, color = XmlElectricCyan)
                }
            }
        }
    }
}

// 2. Filter & LUT Panel
@Composable
private fun FilterLutPanel(
    clip: ClipItem,
    onUpdateClip: (ClipItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("Cinematic Presets (3D LUTs):", fontSize = 12.sp, color = XmlTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(FilterLUT.values()) { lut ->
                val isSelected = clip.filter == lut
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) XmlPurple.copy(alpha = 0.3f) else XmlSurface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) XmlElectricCyan else XmlBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onUpdateClip(clip.copy(filter = lut)) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when (lut) {
                                        FilterLUT.TEAL_ORANGE -> Brush.linearGradient(listOf(Color(0xFF005F73), Color(0xFFCA6702)))
                                        FilterLUT.CYBERPUNK -> Brush.linearGradient(listOf(Color(0xFF7209B7), Color(0xFF4CC9F0)))
                                        FilterLUT.MOODY_NOIR -> Brush.linearGradient(listOf(Color.Black, Color.White))
                                        FilterLUT.GOLDEN_HOUR -> Brush.linearGradient(listOf(Color(0xFFD97706), Color(0xFFFDE68A)))
                                        else -> Brush.linearGradient(listOf(Color.DarkGray, Color.Gray))
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lut.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) XmlWhite else XmlTextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text("Manual Color Grading:", fontSize = 12.sp, color = XmlTextSecondary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        val adj = clip.colorAdjustments
        ColorSliderItem("Contrast", adj.contrast, -100f, 100f) {
            onUpdateClip(clip.copy(colorAdjustments = adj.copy(contrast = it)))
        }
        ColorSliderItem("Saturation", adj.saturation, -100f, 100f) {
            onUpdateClip(clip.copy(colorAdjustments = adj.copy(saturation = it)))
        }
        ColorSliderItem("Temperature", adj.temperature, -100f, 100f) {
            onUpdateClip(clip.copy(colorAdjustments = adj.copy(temperature = it)))
        }
        ColorSliderItem("Vignette", adj.vignette, 0f, 100f) {
            onUpdateClip(clip.copy(colorAdjustments = adj.copy(vignette = it)))
        }
    }
}

@Composable
private fun ColorSliderItem(
    label: String,
    value: Float,
    min: Float,
    max: Float,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = XmlTextMuted, modifier = Modifier.width(80.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = XmlPurple,
                activeTrackColor = XmlElectricCyan,
                inactiveTrackColor = XmlBorder
            )
        )
        Text(text = "${value.toInt()}", fontSize = 11.sp, color = XmlTextSecondary, modifier = Modifier.width(36.dp))
    }
}

// 3. Effects Panel
@Composable
private fun EffectsPanel(
    clip: ClipItem,
    onUpdateClip: (ClipItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Visual & Motion Effects:", fontSize = 12.sp, color = XmlTextSecondary)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(EffectPreset.values()) { fx ->
                val isSelected = clip.effect == fx
                Box(
                    modifier = Modifier
                        .width(92.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) XmlPurple.copy(alpha = 0.3f) else XmlSurface)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) XmlPurple else XmlBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onUpdateClip(clip.copy(effect = fx)) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = fx.title,
                            tint = if (isSelected) XmlElectricCyan else XmlTextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = fx.title,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) XmlWhite else XmlTextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Effect Intensity", fontSize = 12.sp, color = XmlTextSecondary, modifier = Modifier.width(100.dp))
            Slider(
                value = clip.effectIntensity,
                onValueChange = { onUpdateClip(clip.copy(effectIntensity = it)) },
                valueRange = 0.1f..1.0f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(thumbColor = XmlPurple, activeTrackColor = XmlPurple)
            )
            Text("${(clip.effectIntensity * 100).toInt()}%", fontSize = 11.sp, color = XmlWhite)
        }
    }
}

// 4. Speed & Speed Curve Panel
@Composable
private fun SpeedCurvePanel(
    clip: ClipItem,
    onUpdateClip: (ClipItem) -> Unit
) {
    val speedPresets = listOf(0.25f, 0.5f, 1.0f, 1.5f, 2.0f, 4.0f, 8.0f)
    val curvePresets = listOf("Normal", "Montage", "Bullet Time", "Hero Drop", "Flash Velocity")

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Playback Speed: ${clip.speed}x", fontSize = 12.sp, color = XmlTextSecondary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(speedPresets) { s ->
                val isSelected = clip.speed == s
                Button(
                    onClick = { onUpdateClip(clip.copy(speed = s)) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) XmlPurple else XmlSurface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("${s}x", fontSize = 12.sp, color = if (isSelected) XmlWhite else XmlTextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Speed Velocity Curves:", fontSize = 12.sp, color = XmlTextSecondary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(curvePresets) { curve ->
                val isSelected = clip.speedCurve == curve
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) XmlPurpleDark else XmlSurface)
                        .border(1.dp, if (isSelected) XmlElectricCyan else XmlBorder, RoundedCornerShape(8.dp))
                        .clickable { onUpdateClip(clip.copy(speedCurve = curve)) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(curve, fontSize = 11.sp, color = if (isSelected) XmlWhite else XmlTextSecondary)
                }
            }
        }
    }
}

// 5. Text & Subtitle Editor Panel
@Composable
private fun TextEditorPanel(
    clip: ClipItem,
    onUpdateClip: (ClipItem) -> Unit
) {
    var textValue by remember(clip) { mutableStateOf(clip.textContent) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = textValue,
            onValueChange = {
                textValue = it
                onUpdateClip(clip.copy(textContent = it))
            },
            label = { Text("Title or Caption Text", color = XmlTextMuted) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = XmlSurface,
                unfocusedContainerColor = XmlSurface,
                focusedBorderColor = XmlPurple,
                focusedTextColor = XmlWhite,
                unfocusedTextColor = XmlWhite
            )
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = clip.is3DText,
                    onCheckedChange = { onUpdateClip(clip.copy(is3DText = it)) },
                    colors = CheckboxDefaults.colors(checkedColor = XmlPurple)
                )
                Text("3D Metallic Text", fontSize = 12.sp, color = XmlWhite)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = clip.isCurvedText,
                    onCheckedChange = { onUpdateClip(clip.copy(isCurvedText = it)) },
                    colors = CheckboxDefaults.colors(checkedColor = XmlElectricCyan)
                )
                Text("Curved Path", fontSize = 12.sp, color = XmlWhite)
            }
        }
    }
}

// 6. Audio Lab Panel
@Composable
private fun AudioLabPanel(
    clip: ClipItem,
    onUpdateClip: (ClipItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Volume", fontSize = 12.sp, color = XmlTextSecondary, modifier = Modifier.width(70.dp))
            Slider(
                value = clip.volume,
                onValueChange = { onUpdateClip(clip.copy(volume = it)) },
                valueRange = 0f..2f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(thumbColor = XmlAudioTrackColor, activeTrackColor = XmlAudioTrackColor)
            )
            Text("${(clip.volume * 100).toInt()}%", fontSize = 11.sp, color = XmlWhite)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { onUpdateClip(clip.copy(isMuted = !clip.isMuted)) },
                colors = ButtonDefaults.buttonColors(containerColor = if (clip.isMuted) XmlError else XmlSurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (clip.isMuted) "Muted" else "Mute Clip", fontSize = 11.sp)
            }

            Button(
                onClick = { /* Beat detection simulated markers */ },
                colors = ButtonDefaults.buttonColors(containerColor = XmlSurfaceHighlight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("AI Beat Detect", fontSize = 11.sp, color = XmlElectricCyan)
            }

            Button(
                onClick = { /* AI Voice Boost */ },
                colors = ButtonDefaults.buttonColors(containerColor = XmlSurfaceHighlight),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Vocal Clarity", fontSize = 11.sp, color = XmlAmber)
            }
        }
    }
}

// 7. Mask & Chroma Panel
@Composable
private fun MaskChromaPanel(
    clip: ClipItem,
    onUpdateClip: (ClipItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Shape Mask:", fontSize = 12.sp, color = XmlTextSecondary)
        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MaskType.values().forEach { m ->
                val isSelected = clip.maskType == m
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) XmlPurple else XmlSurface)
                        .border(1.dp, XmlBorder, RoundedCornerShape(8.dp))
                        .clickable { onUpdateClip(clip.copy(maskType = m)) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(m.name, fontSize = 10.sp, color = if (isSelected) XmlWhite else XmlTextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Chroma Key (Green Screen)", fontSize = 12.sp, color = XmlWhite)
            Switch(
                checked = clip.chromaKeyEnabled,
                onCheckedChange = { onUpdateClip(clip.copy(chromaKeyEnabled = it)) },
                colors = SwitchDefaults.colors(checkedThumbColor = XmlSuccess)
            )
        }
    }
}

// 8. Keyframes Panel
@Composable
private fun KeyframesPanel(
    clip: ClipItem,
    onUpdateClip: (ClipItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Timeline Keyframes (${clip.keyframes.size})", fontSize = 12.sp, color = XmlTextSecondary)
            Button(
                onClick = {
                    val newKf = KeyframePoint(timeMs = clip.startMs + 500L, scale = 1.15f)
                    onUpdateClip(clip.copy(keyframes = clip.keyframes + newKf))
                },
                colors = ButtonDefaults.buttonColors(containerColor = XmlPurple),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Keyframe", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        clip.keyframes.forEachIndexed { idx, kf ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(XmlSurface, RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("KF #${idx + 1} at ${kf.timeMs}ms (Scale: ${kf.scale}x)", fontSize = 11.sp, color = XmlWhite)
                IconButton(
                    onClick = {
                        onUpdateClip(clip.copy(keyframes = clip.keyframes.filter { it.id != kf.id }))
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = XmlError, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// 9. Transform Panel
@Composable
private fun TransformPanel(
    clip: ClipItem,
    onUpdateClip: (ClipItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Scale", fontSize = 12.sp, color = XmlTextSecondary, modifier = Modifier.width(70.dp))
            Slider(
                value = clip.scale,
                onValueChange = { onUpdateClip(clip.copy(scale = it)) },
                valueRange = 0.5f..3.0f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(thumbColor = XmlPurple, activeTrackColor = XmlElectricCyan)
            )
            Text("${(clip.scale * 100).toInt()}%", fontSize = 11.sp, color = XmlWhite)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Rotation", fontSize = 12.sp, color = XmlTextSecondary, modifier = Modifier.width(70.dp))
            Slider(
                value = clip.rotation,
                onValueChange = { onUpdateClip(clip.copy(rotation = it)) },
                valueRange = -180f..180f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(thumbColor = XmlSunsetOrange, activeTrackColor = XmlSunsetOrange)
            )
            Text("${clip.rotation.toInt()}°", fontSize = 11.sp, color = XmlWhite)
        }
    }
}
