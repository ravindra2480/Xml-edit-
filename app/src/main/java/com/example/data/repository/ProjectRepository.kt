package com.example.data.repository

import com.example.data.db.ProjectDao
import com.example.data.db.ProjectEntity
import com.example.data.db.ProjectJsonConverter
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class ProjectRepository(private val projectDao: ProjectDao) {

    fun getAllProjects(): Flow<List<ProjectData>> {
        return projectDao.getAllProjects().map { entities ->
            entities.map { entity ->
                ProjectData(
                    id = entity.id,
                    title = entity.title,
                    durationMs = entity.durationMs,
                    aspectRatio = try { AspectRatioType.valueOf(entity.aspectRatio) } catch (e: Exception) { AspectRatioType.RATIO_9_16 },
                    fps = entity.fps,
                    resolution = entity.resolution,
                    thumbnailUri = entity.thumbnailUri,
                    lastModified = entity.lastModified,
                    tracks = ProjectJsonConverter.jsonToTracks(entity.tracksJson)
                )
            }
        }
    }

    suspend fun getProject(id: String): ProjectData? {
        val entity = projectDao.getProjectById(id) ?: return null
        return ProjectData(
            id = entity.id,
            title = entity.title,
            durationMs = entity.durationMs,
            aspectRatio = try { AspectRatioType.valueOf(entity.aspectRatio) } catch (e: Exception) { AspectRatioType.RATIO_9_16 },
            fps = entity.fps,
            resolution = entity.resolution,
            thumbnailUri = entity.thumbnailUri,
            lastModified = entity.lastModified,
            tracks = ProjectJsonConverter.jsonToTracks(entity.tracksJson)
        )
    }

    suspend fun saveProject(project: ProjectData) {
        val entity = ProjectEntity(
            id = project.id,
            title = project.title,
            durationMs = project.durationMs,
            aspectRatio = project.aspectRatio.name,
            fps = project.fps,
            resolution = project.resolution,
            thumbnailUri = project.thumbnailUri,
            lastModified = System.currentTimeMillis(),
            tracksJson = ProjectJsonConverter.tracksToJson(project.tracks)
        )
        projectDao.insertProject(entity)
    }

    suspend fun duplicateProject(id: String): String? {
        val original = getProject(id) ?: return null
        val newId = UUID.randomUUID().toString()
        val duplicated = original.copy(
            id = newId,
            title = "${original.title} (Copy)",
            lastModified = System.currentTimeMillis()
        )
        saveProject(duplicated)
        return newId
    }

    suspend fun deleteProject(id: String) {
        projectDao.deleteProject(id)
    }

    companion object {
        fun createSampleCinematicProject(): ProjectData {
            val projectId = UUID.randomUUID().toString()
            val videoTrackId = UUID.randomUUID().toString()
            val overlayTrackId = UUID.randomUUID().toString()
            val textTrackId = UUID.randomUUID().toString()
            val audioTrackId = UUID.randomUUID().toString()
            val effectTrackId = UUID.randomUUID().toString()

            val clip1 = ClipItem(
                id = UUID.randomUUID().toString(),
                trackId = videoTrackId,
                title = "Cyber_Drift_Scene1.mp4",
                mediaType = MediaType.VIDEO,
                startMs = 0L,
                endMs = 4000L,
                sourceDurationMs = 6000L,
                speed = 1.0f,
                filter = FilterLUT.TEAL_ORANGE,
                effect = EffectPreset.CINEMATIC_GLOW,
                transitionIn = TransitionType.FADE,
                keyframes = listOf(
                    KeyframePoint(timeMs = 0L, scale = 1.0f, posX = 0f, posY = 0f),
                    KeyframePoint(timeMs = 3800L, scale = 1.12f, posX = 0f, posY = 0f)
                )
            )

            val clip2 = ClipItem(
                id = UUID.randomUUID().toString(),
                trackId = videoTrackId,
                title = "Night_Neon_Walk.mp4",
                mediaType = MediaType.VIDEO,
                startMs = 4000L,
                endMs = 9000L,
                sourceDurationMs = 8000L,
                speed = 1.0f,
                filter = FilterLUT.CYBERPUNK,
                effect = EffectPreset.GLITCH,
                effectIntensity = 0.4f,
                transitionIn = TransitionType.ZOOM_IN
            )

            val overlayClip = ClipItem(
                id = UUID.randomUUID().toString(),
                trackId = overlayTrackId,
                title = "Light_Streak_Overlay.mp4",
                mediaType = MediaType.VIDEO,
                startMs = 2000L,
                endMs = 7000L,
                sourceDurationMs = 5000L,
                blendMode = BlendModeType.SCREEN,
                opacity = 0.75f,
                scale = 1.0f
            )

            val textClip1 = ClipItem(
                id = UUID.randomUUID().toString(),
                trackId = textTrackId,
                title = "XML CINEMATIC",
                mediaType = MediaType.TEXT,
                startMs = 800L,
                endMs = 3800L,
                textContent = "XML CINEMATIC AI",
                fontStyle = "Futura Bold",
                fontSize = 28f,
                textColorHex = "#FFFFFF",
                textGlowHex = "#00E5FF",
                is3DText = true,
                subtitles = listOf(
                    SubtitleItem(startMs = 800L, endMs = 2000L, text = "XML Cinematic Editor", translation = "एक्सएमएल सिनेमैटिक"),
                    SubtitleItem(startMs = 2000L, endMs = 3800L, text = "Craft Tomorrow's Films", translation = "कल की फिल्में आज बनाएं")
                )
            )

            val textClip2 = ClipItem(
                id = UUID.randomUUID().toString(),
                trackId = textTrackId,
                title = "Beat Drop Text",
                mediaType = MediaType.TEXT,
                startMs = 4200L,
                endMs = 8500L,
                textContent = "AI AUTO CUTS ACTIVATED",
                fontStyle = "Impact Heavy",
                fontSize = 26f,
                textColorHex = "#FFD166",
                textGlowHex = "#FF7A00"
            )

            val audioClip = ClipItem(
                id = UUID.randomUUID().toString(),
                trackId = audioTrackId,
                title = "Synthwave_Cyber_Bass.mp3",
                mediaType = MediaType.AUDIO,
                startMs = 0L,
                endMs = 9000L,
                sourceDurationMs = 12000L,
                volume = 0.9f
            )

            val effectClip = ClipItem(
                id = UUID.randomUUID().toString(),
                trackId = effectTrackId,
                title = "Anamorphic Flare",
                mediaType = MediaType.EFFECT,
                startMs = 3500L,
                endMs = 5500L,
                effect = EffectPreset.LIGHT_LEAK,
                effectIntensity = 0.85f
            )

            val tracks = listOf(
                Track(id = videoTrackId, type = TrackType.VIDEO, name = "Main Video", clips = listOf(clip1, clip2)),
                Track(id = overlayTrackId, type = TrackType.OVERLAY, name = "PIP & Overlays", clips = listOf(overlayClip)),
                Track(id = textTrackId, type = TrackType.TEXT, name = "Text & Subtitles", clips = listOf(textClip1, textClip2)),
                Track(id = audioTrackId, type = TrackType.AUDIO, name = "Music & Beats", clips = listOf(audioClip)),
                Track(id = effectTrackId, type = TrackType.EFFECT, name = "FX & Transitions", clips = listOf(effectClip))
            )

            return ProjectData(
                id = projectId,
                title = "Cyberpunk Reel Edit",
                durationMs = 9000L,
                aspectRatio = AspectRatioType.RATIO_9_16,
                fps = 30,
                resolution = "1080p",
                tracks = tracks
            )
        }
    }
}
