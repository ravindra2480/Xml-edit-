package com.example.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AIVideoService
import com.example.data.model.*
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class EditorUiState(
    val project: ProjectData = ProjectRepository.createSampleCinematicProject(),
    val currentPositionMs: Long = 0L,
    val isPlaying: Boolean = false,
    val selectedTrackId: String? = null,
    val selectedClipId: String? = null,
    val activeTool: ActiveEditorTool = ActiveEditorTool.NONE,
    val zoomScale: Float = 60f,
    val showSafeGuides: Boolean = false,
    val isAIProcessing: Boolean = false,
    val aiNotificationMessage: String? = null,
    val showExportDialog: Boolean = false,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)

class VideoEditorViewModel(
    private val repository: ProjectRepository,
    private val aiService: AIVideoService = AIVideoService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<ProjectData>()
    private val redoStack = mutableListOf<ProjectData>()
    private var playbackJob: Job? = null

    fun loadProject(projectId: String?) {
        viewModelScope.launch {
            if (projectId.isNullOrBlank()) {
                val sample = ProjectRepository.createSampleCinematicProject()
                repository.saveProject(sample)
                _uiState.value = _uiState.value.copy(project = sample)
            } else {
                val existing = repository.getProject(projectId)
                if (existing != null) {
                    _uiState.value = _uiState.value.copy(project = existing)
                } else {
                    val sample = ProjectRepository.createSampleCinematicProject().copy(id = projectId)
                    repository.saveProject(sample)
                    _uiState.value = _uiState.value.copy(project = sample)
                }
            }
        }
    }

    private fun pushHistory(newProject: ProjectData) {
        undoStack.add(_uiState.value.project.copy())
        if (undoStack.size > 20) undoStack.removeAt(0)
        redoStack.clear()

        _uiState.value = _uiState.value.copy(
            project = newProject,
            canUndo = undoStack.isNotEmpty(),
            canRedo = false
        )

        viewModelScope.launch {
            repository.saveProject(newProject)
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeAt(undoStack.size - 1)
            redoStack.add(_uiState.value.project.copy())
            _uiState.value = _uiState.value.copy(
                project = previous,
                canUndo = undoStack.isNotEmpty(),
                canRedo = true
            )
            viewModelScope.launch { repository.saveProject(previous) }
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.size - 1)
            undoStack.add(_uiState.value.project.copy())
            _uiState.value = _uiState.value.copy(
                project = next,
                canUndo = true,
                canRedo = redoStack.isNotEmpty()
            )
            viewModelScope.launch { repository.saveProject(next) }
        }
    }

    fun togglePlay() {
        val playing = !_uiState.value.isPlaying
        _uiState.value = _uiState.value.copy(isPlaying = playing)

        if (playing) {
            startPlaybackLoop()
        } else {
            playbackJob?.cancel()
        }
    }

    private fun startPlaybackLoop() {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val frameDelay = 33L // ~30 FPS
            while (_uiState.value.isPlaying) {
                delay(frameDelay)
                val current = _uiState.value.currentPositionMs
                val next = current + (frameDelay * (_uiState.value.project.tracks.firstOrNull { it.type == TrackType.VIDEO }?.clips?.firstOrNull()?.speed ?: 1.0f)).toLong()
                if (next >= _uiState.value.project.durationMs) {
                    _uiState.value = _uiState.value.copy(currentPositionMs = 0L, isPlaying = false)
                    break
                } else {
                    _uiState.value = _uiState.value.copy(currentPositionMs = next)
                }
            }
        }
    }

    fun seekTo(positionMs: Long) {
        _uiState.value = _uiState.value.copy(
            currentPositionMs = positionMs.coerceIn(0L, _uiState.value.project.durationMs)
        )
    }

    fun stepFrame(forward: Boolean) {
        val frameDurationMs = 1000L / _uiState.value.project.fps
        val next = if (forward) {
            _uiState.value.currentPositionMs + frameDurationMs
        } else {
            _uiState.value.currentPositionMs - frameDurationMs
        }
        seekTo(next)
    }

    fun selectClip(track: Track, clip: ClipItem) {
        _uiState.value = _uiState.value.copy(
            selectedTrackId = track.id,
            selectedClipId = clip.id
        )
    }

    fun openTool(tool: ActiveEditorTool) {
        _uiState.value = _uiState.value.copy(activeTool = tool)
    }

    fun closeTool() {
        _uiState.value = _uiState.value.copy(activeTool = ActiveEditorTool.NONE)
    }

    fun setZoomScale(scale: Float) {
        _uiState.value = _uiState.value.copy(zoomScale = scale)
    }

    fun toggleSafeGuides() {
        _uiState.value = _uiState.value.copy(showSafeGuides = !_uiState.value.showSafeGuides)
    }

    fun setExportDialogVisible(visible: Boolean) {
        _uiState.value = _uiState.value.copy(showExportDialog = visible)
    }

    fun setAspectRatio(ratio: AspectRatioType) {
        val updated = _uiState.value.project.copy(aspectRatio = ratio)
        pushHistory(updated)
    }

    fun updateClip(updatedClip: ClipItem) {
        val updatedTracks = _uiState.value.project.tracks.map { track ->
            if (track.id == updatedClip.trackId) {
                track.copy(clips = track.clips.map { if (it.id == updatedClip.id) updatedClip else it })
            } else track
        }
        pushHistory(_uiState.value.project.copy(tracks = updatedTracks))
    }

    fun splitClipAtPlayhead() {
        val pos = _uiState.value.currentPositionMs
        val trackId = _uiState.value.selectedTrackId ?: _uiState.value.project.tracks.firstOrNull { it.type == TrackType.VIDEO }?.id ?: return
        val track = _uiState.value.project.tracks.find { it.id == trackId } ?: return

        val clipToSplit = track.clips.find { pos in (it.startMs + 100L)..(it.endMs - 100L) } ?: return

        val firstPart = clipToSplit.copy(endMs = pos)
        val secondPart = clipToSplit.copy(
            id = UUID.randomUUID().toString(),
            startMs = pos,
            title = "${clipToSplit.title}_part2"
        )

        val newClips = track.clips.flatMap { if (it.id == clipToSplit.id) listOf(firstPart, secondPart) else listOf(it) }
        val updatedTracks = _uiState.value.project.tracks.map {
            if (it.id == trackId) it.copy(clips = newClips) else it
        }

        pushHistory(_uiState.value.project.copy(tracks = updatedTracks))
        _uiState.value = _uiState.value.copy(selectedClipId = secondPart.id)
    }

    fun deleteSelectedClip() {
        val clipId = _uiState.value.selectedClipId ?: return
        val trackId = _uiState.value.selectedTrackId ?: return

        val updatedTracks = _uiState.value.project.tracks.map { track ->
            if (track.id == trackId) {
                track.copy(clips = track.clips.filter { it.id != clipId })
            } else track
        }

        pushHistory(_uiState.value.project.copy(tracks = updatedTracks))
        _uiState.value = _uiState.value.copy(selectedClipId = null, selectedTrackId = null)
    }

    fun duplicateSelectedClip() {
        val clipId = _uiState.value.selectedClipId ?: return
        val trackId = _uiState.value.selectedTrackId ?: return
        val track = _uiState.value.project.tracks.find { it.id == trackId } ?: return
        val original = track.clips.find { it.id == clipId } ?: return

        val clipDuration = original.endMs - original.startMs
        val duplicated = original.copy(
            id = UUID.randomUUID().toString(),
            title = "${original.title} (Copy)",
            startMs = original.endMs,
            endMs = original.endMs + clipDuration
        )

        val updatedTracks = _uiState.value.project.tracks.map {
            if (it.id == trackId) it.copy(clips = it.clips + duplicated) else it
        }

        val newDuration = maxOf(_uiState.value.project.durationMs, duplicated.endMs)
        pushHistory(_uiState.value.project.copy(tracks = updatedTracks, durationMs = newDuration))
        _uiState.value = _uiState.value.copy(selectedClipId = duplicated.id)
    }

    fun freezeFrameAtPlayhead() {
        val pos = _uiState.value.currentPositionMs
        val track = _uiState.value.project.tracks.find { it.type == TrackType.VIDEO } ?: return
        val activeClip = track.clips.find { pos in it.startMs..it.endMs } ?: return

        val freezeDuration = 2000L
        val freezeClip = activeClip.copy(
            id = UUID.randomUUID().toString(),
            title = "Freeze_Frame_${pos / 1000}s",
            speed = 0.1f,
            startMs = pos,
            endMs = pos + freezeDuration
        )

        // Shift subsequent clips forward
        val shiftedClips = track.clips.map {
            if (it.startMs >= pos) it.copy(startMs = it.startMs + freezeDuration, endMs = it.endMs + freezeDuration) else it
        }

        val updatedTracks = _uiState.value.project.tracks.map {
            if (it.id == track.id) it.copy(clips = shiftedClips + freezeClip) else it
        }

        pushHistory(_uiState.value.project.copy(
            tracks = updatedTracks,
            durationMs = _uiState.value.project.durationMs + freezeDuration
        ))
    }

    fun executeAIPrompt(prompt: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAIProcessing = true)
            val result = aiService.executeAIPrompt(prompt, _uiState.value.project)
            pushHistory(result.modifiedProject)
            _uiState.value = _uiState.value.copy(
                isAIProcessing = false,
                aiNotificationMessage = result.summary,
                activeTool = ActiveEditorTool.NONE
            )
        }
    }

    fun clearNotification() {
        _uiState.value = _uiState.value.copy(aiNotificationMessage = null)
    }
}
