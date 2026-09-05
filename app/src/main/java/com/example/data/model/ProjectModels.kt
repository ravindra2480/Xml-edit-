package com.example.data.model

import java.util.UUID

enum class AspectRatioType(val label: String, val ratio: Float, val widthRatio: Int, val heightRatio: Int) {
    RATIO_9_16("9:16 (Reels/TikTok)", 9f / 16f, 9, 16),
    RATIO_16_9("16:9 (YouTube)", 16f / 9f, 16, 9),
    RATIO_1_1("1:1 (Square)", 1f, 1, 1),
    RATIO_4_5("4:5 (Insta Portrait)", 4f / 5f, 4, 5),
    RATIO_3_4("3:4 (Standard)", 3f / 4f, 3, 4)
}

enum class TrackType {
    VIDEO,
    OVERLAY,
    TEXT,
    AUDIO,
    STICKER,
    EFFECT
}

enum class MediaType {
    VIDEO,
    PHOTO,
    AUDIO,
    TEXT,
    STICKER,
    EFFECT
}

enum class MaskType {
    NONE,
    CIRCLE,
    RECTANGLE,
    LINEAR,
    INVERTED
}

enum class BlendModeType {
    NORMAL,
    SCREEN,
    MULTIPLY,
    OVERLAY,
    ADD
}

enum class FilterLUT(val title: String, val description: String) {
    NONE("Original", "Natural colors"),
    TEAL_ORANGE("Teal & Orange", "Hollywood cinematic look"),
    CYBERPUNK("Cyberpunk", "Vibrant neon purple and cyan"),
    MOODY_NOIR("Moody Noir", "High contrast black and white"),
    GOLDEN_HOUR("Golden Hour", "Warm sunset glow"),
    EMERALD_MATRIX("Emerald", "Deep green cyber atmosphere"),
    VINTAGE_1970("Vintage 70s", "Warm film grain analog style"),
    BLEACH_BYPASS("Bleach Bypass", "Desaturated gritty action look"),
    VIBRANT_POP("Vibrant Pop", "Saturated punchy tones")
}

enum class EffectPreset(val title: String, val category: String) {
    NONE("None", "Standard"),
    CINEMATIC_GLOW("Cinematic Glow", "Cinematic"),
    GLITCH("Cyber Glitch", "Glitch"),
    VHS("VHS Tape 1994", "Retro"),
    RETRO_GRAIN("Film Grain 35mm", "Retro"),
    MOTION_BLUR("Motion Blur", "Blur"),
    SHAKE("Camera Shake", "Action"),
    FLASH("Flash Beat", "Lighting"),
    RGB_SPLIT("RGB Split", "Distortion"),
    NEON_EDGES("Neon Edges", "Futuristic"),
    LIGHT_LEAK("Golden Light Leak", "Lighting"),
    PARTICLES("Dust Particles", "Atmosphere")
}

enum class TransitionType(val title: String) {
    NONE("None"),
    FADE("Fade to Black"),
    DISSOLVE("Cross Dissolve"),
    ZOOM_IN("Zoom In"),
    SWIPE_LEFT("Swipe Left"),
    SPIN("Spin 360"),
    GLITCH("Glitch Cut"),
    FLASH("Flash White"),
    CINEMATIC_SLIDE("Cinematic Push")
}

data class KeyframePoint(
    val id: String = UUID.randomUUID().toString(),
    val timeMs: Long,
    val posX: Float = 0f,
    val posY: Float = 0f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f
)

data class ColorAdjustments(
    val contrast: Float = 0f,       // -100 to 100
    val brightness: Float = 0f,     // -100 to 100
    val saturation: Float = 0f,     // -100 to 100
    val exposure: Float = 0f,       // -100 to 100
    val temperature: Float = 0f,    // -100 to 100 (Cool to Warm)
    val tint: Float = 0f,           // -100 to 100
    val highlights: Float = 0f,     // -100 to 100
    val shadows: Float = 0f,        // -100 to 100
    val sharpen: Float = 0f,        // 0 to 100
    val vignette: Float = 0f        // 0 to 100
)

data class SubtitleItem(
    val id: String = UUID.randomUUID().toString(),
    val startMs: Long,
    val endMs: Long,
    val text: String,
    val translation: String = "",
    val isHighlighted: Boolean = false
)

data class ClipItem(
    val id: String = UUID.randomUUID().toString(),
    val trackId: String,
    val title: String,
    val mediaUri: String = "",
    val mediaType: MediaType = MediaType.VIDEO,
    val startMs: Long = 0L,
    val endMs: Long = 3000L,
    val sourceDurationMs: Long = 3000L,
    val speed: Float = 1.0f,
    val speedCurve: String = "Normal", // Normal, Montage, BulletTime, Hero
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val filter: FilterLUT = FilterLUT.NONE,
    val colorAdjustments: ColorAdjustments = ColorAdjustments(),
    val effect: EffectPreset = EffectPreset.NONE,
    val effectIntensity: Float = 0.8f,
    val transitionIn: TransitionType = TransitionType.NONE,
    val transitionDurationMs: Long = 500L,
    val posX: Float = 0f,
    val posY: Float = 0f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f,
    val maskType: MaskType = MaskType.NONE,
    val blendMode: BlendModeType = BlendModeType.NORMAL,
    val chromaKeyEnabled: Boolean = false,
    val bgRemovalEnabled: Boolean = false,
    val textContent: String = "",
    val fontStyle: String = "Futura Bold",
    val fontSize: Float = 24f,
    val textColorHex: String = "#FFFFFF",
    val textGlowHex: String = "#A855F7",
    val isCurvedText: Boolean = false,
    val is3DText: Boolean = false,
    val keyframes: List<KeyframePoint> = emptyList(),
    val subtitles: List<SubtitleItem> = emptyList()
)

data class Track(
    val id: String = UUID.randomUUID().toString(),
    val type: TrackType,
    val name: String,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false,
    val isHidden: Boolean = false,
    val clips: List<ClipItem> = emptyList()
)

data class ProjectData(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Cinematic Edit",
    val durationMs: Long = 10000L,
    val aspectRatio: AspectRatioType = AspectRatioType.RATIO_9_16,
    val fps: Int = 30,
    val resolution: String = "1080p",
    val thumbnailUri: String = "",
    val lastModified: Long = System.currentTimeMillis(),
    val tracks: List<Track> = emptyList()
)

data class TemplateItem(
    val id: String,
    val title: String,
    val category: String, // Reels, Shorts, YouTube, Birthday, Wedding, Festival, Travel, Cinematic, Motivation, Status, Trending
    val durationSec: Int,
    val aspectRatio: AspectRatioType,
    val description: String,
    val beatsCount: Int,
    val tags: List<String>,
    val defaultPrompt: String
)

data class AIFeature(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val badge: String? = null,
    val samplePrompt: String
)
