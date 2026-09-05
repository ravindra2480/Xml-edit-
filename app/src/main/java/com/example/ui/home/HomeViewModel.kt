package com.example.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AspectRatioType
import com.example.data.model.ProjectData
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class HomeUiState(
    val projects: List<ProjectData> = emptyList(),
    val searchQuery: String = "",
    val filteredProjects: List<ProjectData> = emptyList(),
    val isCreatingNewProject: Boolean = false
)

class HomeViewModel(private val repository: ProjectRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            repository.getAllProjects().collect { list ->
                if (list.isEmpty()) {
                    // Pre-populate with sample project so user immediately has a working project
                    val sample = ProjectRepository.createSampleCinematicProject()
                    repository.saveProject(sample)
                } else {
                    _uiState.value = _uiState.value.copy(
                        projects = list,
                        filteredProjects = filterList(list, _uiState.value.searchQuery)
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredProjects = filterList(_uiState.value.projects, query)
        )
    }

    private fun filterList(list: List<ProjectData>, query: String): List<ProjectData> {
        if (query.isBlank()) return list
        return list.filter { it.title.contains(query, ignoreCase = true) }
    }

    fun createNewProject(aspectRatio: AspectRatioType = AspectRatioType.RATIO_9_16, onProjectCreated: (String) -> Unit) {
        viewModelScope.launch {
            val newProject = ProjectRepository.createSampleCinematicProject().copy(
                id = UUID.randomUUID().toString(),
                title = "New Project ${System.currentTimeMillis() % 10000}",
                aspectRatio = aspectRatio
            )
            repository.saveProject(newProject)
            onProjectCreated(newProject.id)
        }
    }

    fun duplicateProject(id: String) {
        viewModelScope.launch {
            repository.duplicateProject(id)
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            repository.deleteProject(id)
        }
    }
}
