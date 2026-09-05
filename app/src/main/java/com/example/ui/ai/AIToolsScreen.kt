package com.example.ui.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.ai.AIVideoService
import com.example.ai.StoryboardFrame
import com.example.data.model.AIFeature
import com.example.data.model.TemplatesDataProvider
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AIToolsScreen(
    onLaunchAIEdit: (prompt: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val aiFeatures = remember { TemplatesDataProvider.getAIFeatures() }
    val coroutineScope = rememberCoroutineScope()
    val aiService = remember { AIVideoService() }

    var promptInput by remember { mutableStateOf("") }
    var selectedFeatureForDialog by remember { mutableStateOf<AIFeature?>(null) }
    var isGeneratingScript by remember { mutableStateOf(false) }
    var generatedScript by remember { mutableStateOf<String?>(null) }
    var storyboardFrames by remember { mutableStateOf<List<StoryboardFrame>>(emptyList()) }

    val sampleHindiEnglishPrompts = listOf(
        "इस वीडियो को cinematic बना दो",
        "30 सेकंड की Reel बना दो",
        "Beat के हिसाब से cuts लगाओ",
        "इस वीडियो में Hindi captions लगाओ",
        "Auto remove silence & boost vocal presence",
        "Generate 3-scene cyberpunk storyboard"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(XmlBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // AI Hub Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(XmlPurple, XmlElectricCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = XmlBackground, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "XML AI Video Studio",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = XmlWhite
                        )
                        Text(
                            text = "16 Neural Video, Audio & Subtitle Engines",
                            fontSize = 11.sp,
                            color = XmlTextSecondary
                        )
                    }
                }
            }
        }

        // Natural Language Prompt Console Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(listOf(XmlPurple, XmlElectricCyan)),
                    shape = RoundedCornerShape(16.dp)
                ),
                colors = CardDefaults.cardColors(containerColor = XmlSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Natural Language Prompt (Hindi & English):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = XmlWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = { Text("e.g. Beat के हिसाब से cuts लगाओ...", color = XmlTextMuted, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_hub_prompt_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = XmlSurfaceHighlight,
                            unfocusedContainerColor = XmlSurfaceHighlight,
                            focusedBorderColor = XmlPurple,
                            unfocusedBorderColor = XmlBorder,
                            focusedTextColor = XmlWhite,
                            unfocusedTextColor = XmlWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick prompt chips
                    Text(text = "Try Prompts:", fontSize = 11.sp, color = XmlTextMuted, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(sampleHindiEnglishPrompts) { p ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(XmlSurfaceHighlight)
                                    .border(0.5.dp, XmlBorderGlow, RoundedCornerShape(16.dp))
                                    .clickable { promptInput = p }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(p, fontSize = 10.sp, color = XmlElectricCyan)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (promptInput.isNotBlank()) {
                                onLaunchAIEdit(promptInput)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("launch_ai_edit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = XmlPurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply AI Magic to Project", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Storyboard & Script Generator Demo Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, XmlBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = XmlSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MovieFilter, contentDescription = null, tint = XmlSunsetOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Script & Storyboard Studio", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = XmlWhite)
                        }

                        Button(
                            onClick = {
                                isGeneratingScript = true
                                coroutineScope.launch {
                                    val res = aiService.generateScriptAndStoryboards("Cyberpunk Mobile Creator")
                                    generatedScript = res.generatedScript
                                    storyboardFrames = res.generatedStoryboards
                                    isGeneratingScript = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = XmlSunsetOrange),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("generate_storyboard_btn")
                        ) {
                            if (isGeneratingScript) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = XmlWhite, strokeWidth = 2.dp)
                            } else {
                                Text("Generate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    AnimatedVisibility(visible = storyboardFrames.isNotEmpty()) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            storyboardFrames.forEach { frame ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = XmlSurfaceHighlight)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Scene #${frame.sceneNumber}: ${frame.title}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = XmlElectricCyan
                                            )
                                            Text(
                                                text = "${frame.shotType} • ${frame.durationSec}s",
                                                fontSize = 10.sp,
                                                color = XmlSunsetOrange
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Action: ${frame.actionDescription}", fontSize = 11.sp, color = XmlTextSecondary)
                                        Text(text = "Narration: \"${frame.dialogue}\"", fontSize = 11.sp, color = XmlWhite, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: 16 AI Video Tools Grid
        item {
            Text(
                text = "All AI Neural Engines",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = XmlWhite
            )
        }

        items(aiFeatures, key = { it.id }) { feature ->
            AIFeatureListItem(
                feature = feature,
                onClick = {
                    promptInput = feature.samplePrompt
                    onLaunchAIEdit(feature.samplePrompt)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun AIFeatureListItem(
    feature: AIFeature,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, XmlBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("ai_tool_item_${feature.id}"),
        colors = CardDefaults.cardColors(containerColor = XmlSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(XmlSurfaceHighlight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = feature.title,
                    tint = XmlElectricCyan,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = feature.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = XmlWhite
                    )
                    if (feature.badge != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(XmlPurpleDark, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = feature.badge,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = XmlElectricCyan
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = feature.subtitle,
                    fontSize = 11.sp,
                    color = XmlTextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = XmlTextMuted
            )
        }
    }
}
