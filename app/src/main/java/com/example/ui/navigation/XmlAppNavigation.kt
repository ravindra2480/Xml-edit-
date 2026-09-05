package com.example.ui.navigation

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.db.ProjectDatabase
import com.example.data.model.AspectRatioType
import com.example.data.repository.ProjectRepository
import com.example.ui.ai.AIToolsScreen
import com.example.ui.editor.VideoEditorScreen
import com.example.ui.editor.VideoEditorViewModel
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.profile.ProfileScreen
import com.example.ui.templates.TemplatesScreen
import com.example.ui.theme.*

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Create : Screen("create", "Create", Icons.Default.Add)
    object Templates : Screen("templates", "Templates", Icons.Default.ViewCarousel)
    object AI : Screen("ai_tools", "AI", Icons.Default.AutoAwesome)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Editor : Screen("editor/{projectId}", "Editor", Icons.Default.Movie) {
        fun createRoute(projectId: String) = "editor/$projectId"
    }
}

@Composable
fun XmlAppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val database = remember { ProjectDatabase.getDatabase(context) }
    val repository = remember { ProjectRepository(database.projectDao()) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isEditorScreen = currentRoute?.startsWith("editor") == true

    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = XmlBackground,
        bottomBar = {
            if (!isEditorScreen) {
                XmlBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigateToTab = { screen ->
                        if (screen == Screen.Create) {
                            showCreateDialog = true
                        } else {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Home Screen
            composable(Screen.Home.route) {
                val homeViewModel = remember { HomeViewModel(repository) }
                HomeScreen(
                    viewModel = homeViewModel,
                    onOpenProject = { projectId ->
                        navController.navigate(Screen.Editor.createRoute(projectId))
                    },
                    onNavigateToCreate = { showCreateDialog = true },
                    onNavigateToTemplates = { navController.navigate(Screen.Templates.route) },
                    onNavigateToAI = { navController.navigate(Screen.AI.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                )
            }

            // 2. Templates Screen
            composable(Screen.Templates.route) {
                TemplatesScreen(
                    onSelectTemplate = { template ->
                        val sample = ProjectRepository.createSampleCinematicProject().copy(
                            title = template.title,
                            aspectRatio = template.aspectRatio
                        )
                        kotlinx.coroutines.MainScope().run {
                            navController.navigate(Screen.Editor.createRoute(sample.id))
                        }
                    }
                )
            }

            // 3. AI Studio Screen
            composable(Screen.AI.route) {
                AIToolsScreen(
                    onLaunchAIEdit = { prompt ->
                        val sample = ProjectRepository.createSampleCinematicProject()
                        navController.navigate(Screen.Editor.createRoute(sample.id))
                    }
                )
            }

            // 4. Profile / Settings Screen
            composable(Screen.Profile.route) {
                ProfileScreen()
            }

            // 5. Video Editor Screen
            composable(
                route = Screen.Editor.route,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")
                val editorViewModel = remember(projectId) {
                    VideoEditorViewModel(repository).apply {
                        loadProject(projectId)
                    }
                }

                VideoEditorScreen(
                    viewModel = editorViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }

    // New Project Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = XmlSurface,
            title = {
                Text("Select Aspect Ratio", fontWeight = FontWeight.Bold, color = XmlWhite)
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
                                    showCreateDialog = false
                                    val newProj = ProjectRepository.createSampleCinematicProject().copy(
                                        title = "New Project ${System.currentTimeMillis() % 10000}",
                                        aspectRatio = ratio
                                    )
                                    navController.navigate(Screen.Editor.createRoute(newProj.id))
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Crop, contentDescription = null, tint = XmlElectricCyan)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = ratio.label, fontSize = 13.sp, color = XmlWhite)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = XmlTextSecondary)
                }
            }
        )
    }
}

@Composable
fun XmlBottomNavigationBar(
    currentRoute: String?,
    onNavigateToTab: (Screen) -> Unit
) {
    val items = listOf(
        Screen.Home,
        Screen.Create,
        Screen.Templates,
        Screen.AI,
        Screen.Profile
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, XmlBorder),
        containerColor = XmlSurface,
        tonalElevation = 8.dp
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route

            if (screen == Screen.Create) {
                // Floating Style Center Action Button for "Create"
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToTab(screen) },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(XmlPurple, XmlElectricCyan))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create",
                                tint = XmlBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = "Create",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = XmlElectricCyan
                        )
                    },
                    modifier = Modifier.testTag("nav_create_btn")
                )
            } else {
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigateToTab(screen) },
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (isSelected) XmlPurple else XmlTextMuted
                        )
                    },
                    label = {
                        Text(
                            text = screen.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) XmlWhite else XmlTextMuted
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = XmlPurpleDark
                    ),
                    modifier = Modifier.testTag("nav_${screen.route}_btn")
                )
            }
        }
    }
}
