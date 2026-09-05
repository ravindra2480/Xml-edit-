package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectRatioType
import com.example.data.model.ProjectData
import com.example.ui.components.XmlBrandBadge
import com.example.ui.components.XmlTopBarLogo
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenProject: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToAI: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showRatioDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = XmlBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Top Brand Header: XML Logo + Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    XmlTopBarLogo()

                    IconButton(
                        onClick = onNavigateToProfile,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(XmlSurfaceHighlight)
                            .testTag("home_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = XmlTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = { Text("Search projects, drafts, templates...", color = XmlTextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = XmlTextMuted)
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = XmlTextSecondary)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = XmlSurface,
                        unfocusedContainerColor = XmlSurface,
                        focusedBorderColor = XmlPurple,
                        unfocusedBorderColor = XmlBorder,
                        focusedTextColor = XmlWhite,
                        unfocusedTextColor = XmlWhite
                    ),
                    singleLine = true
                )
            }

            // Hero Card: "Create New Project"
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2E1065),
                                    Color(0xFF1E1B4B),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(
                                listOf(XmlPurple, XmlElectricCyan, XmlSunsetOrange)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { showRatioDialog = true }
                        .padding(20.dp)
                        .testTag("create_new_project_hero_btn")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(XmlElectricCyan)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "MULTI-TRACK STUDIO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = XmlElectricCyan,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Create New Project",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = XmlWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Import videos, photos & music with 4K timeline",
                                fontSize = 12.sp,
                                color = XmlTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(XmlPurple, XmlElectricCyan))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Project",
                                tint = XmlBackground,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            // Quick Hub 4-Grid: AI Auto Edit | Templates | Projects | AI Tools
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.AutoAwesome,
                        title = "AI Auto Edit",
                        subtitle = "Prompt to Video",
                        accentColor = XmlPurple,
                        onClick = onNavigateToAI,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_action_ai_auto_edit"
                    )

                    QuickActionCard(
                        icon = Icons.Default.ViewCarousel,
                        title = "Templates",
                        subtitle = "Reels & Shorts",
                        accentColor = XmlElectricCyan,
                        onClick = onNavigateToTemplates,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_action_templates"
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        icon = Icons.Default.Psychology,
                        title = "AI Tools",
                        subtitle = "16 Pro Engines",
                        accentColor = XmlSunsetOrange,
                        onClick = onNavigateToAI,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_action_ai_tools"
                    )

                    QuickActionCard(
                        icon = Icons.Default.VideoLibrary,
                        title = "Projects",
                        subtitle = "${uiState.projects.size} Saved",
                        accentColor = XmlSkyBlue,
                        onClick = { /* Already on projects */ },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_action_projects"
                    )
                }
            }

            // Recent Projects Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Recent Projects",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = XmlWhite
                    )
                    Text(
                        text = "${uiState.filteredProjects.size} Projects",
                        fontSize = 12.sp,
                        color = XmlTextMuted
                    )
                }
            }

            // Projects List
            if (uiState.filteredProjects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.MovieCreation,
                                contentDescription = "Empty",
                                tint = XmlTextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("No projects found", color = XmlTextSecondary, fontSize = 14.sp)
                            Text("Tap 'Create New Project' to start editing", color = XmlTextMuted, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(uiState.filteredProjects, key = { it.id }) { project ->
                    ProjectCardItem(
                        project = project,
                        onClick = { onOpenProject(project.id) },
                        onDuplicate = { viewModel.duplicateProject(project.id) },
                        onDelete = { viewModel.deleteProject(project.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Aspect Ratio Selection Dialog for New Project
    if (showRatioDialog) {
        AlertDialog(
            onDismissRequest = { showRatioDialog = false },
            containerColor = XmlSurface,
            title = {
                Text("Select Canvas Ratio", fontWeight = FontWeight.Bold, color = XmlWhite)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AspectRatioType.values().forEach { ratio ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(XmlSurfaceHighlight)
                                .clickable {
                                    showRatioDialog = false
                                    viewModel.createNewProject(ratio) { newId ->
                                        onOpenProject(newId)
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Crop, contentDescription = ratio.label, tint = XmlElectricCyan)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = ratio.label, fontSize = 13.sp, color = XmlWhite)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRatioDialog = false }) {
                    Text("Cancel", color = XmlTextSecondary)
                }
            }
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(XmlSurface)
            .border(1.dp, XmlBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = XmlWhite)
            Text(text = subtitle, fontSize = 11.sp, color = XmlTextSecondary)
        }
    }
}

@Composable
private fun ProjectCardItem(
    project: ProjectData,
    onClick: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val dateStr = remember(project.lastModified) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(project.lastModified))
    }

    val durationSec = project.durationMs / 1000
    val durationMin = durationSec / 60
    val durationRemSec = durationSec % 60
    val durFormatted = String.format("%02d:%02d", durationMin, durationRemSec)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("project_card_${project.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = XmlSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(XmlBorder, XmlSurfaceHighlight)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Project Video Thumbnail Box
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        )
                    )
                    .border(1.dp, XmlBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Play",
                    tint = XmlElectricCyan,
                    modifier = Modifier.size(28.dp)
                )

                // Duration badge on thumbnail
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .background(Color(0xCC000000), RoundedCornerShape(3.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(text = durFormatted, fontSize = 8.sp, color = XmlWhite, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Project Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = XmlWhite,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${project.aspectRatio.widthRatio}:${project.aspectRatio.heightRatio}",
                        fontSize = 11.sp,
                        color = XmlElectricCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = " • ", color = XmlTextMuted, fontSize = 11.sp)
                    Text(
                        text = "${project.tracks.size} tracks",
                        fontSize = 11.sp,
                        color = XmlTextSecondary
                    )
                    Text(text = " • ", color = XmlTextMuted, fontSize = 11.sp)
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = XmlTextMuted
                    )
                }
            }

            // 3-dots Menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = XmlTextSecondary)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(XmlSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Open Project", color = XmlWhite) },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = XmlElectricCyan) },
                        onClick = {
                            showMenu = false
                            onClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Duplicate", color = XmlWhite) },
                        leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = XmlSunsetOrange) },
                        onClick = {
                            showMenu = false
                            onDuplicate()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = XmlError) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = XmlError) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
