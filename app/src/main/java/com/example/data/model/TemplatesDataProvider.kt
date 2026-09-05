package com.example.data.model

object TemplatesDataProvider {

    fun getTemplates(): List<TemplateItem> = listOf(
        TemplateItem(
            id = "tmpl_trending_reels",
            title = "Cyber Beat Drop",
            category = "Trending",
            durationSec = 15,
            aspectRatio = AspectRatioType.RATIO_9_16,
            description = "High-energy 128 BPM bass drop sync with RGB flashes and neon glows",
            beatsCount = 12,
            tags = listOf("Viral", "EDM", "Reels"),
            defaultPrompt = "Beat के हिसाब से cuts लगाओ"
        ),
        TemplateItem(
            id = "tmpl_cinematic_hollywood",
            title = "Hollywood 2.39:1 Anamorphic",
            category = "Cinematic",
            durationSec = 30,
            aspectRatio = AspectRatioType.RATIO_16_9,
            description = "Teal & Orange LUT, 24 FPS cadence, atmospheric film grain, and slow zooms",
            beatsCount = 6,
            tags = listOf("Cinema", "Film", "4K"),
            defaultPrompt = "इस वीडियो को cinematic बना दो"
        ),
        TemplateItem(
            id = "tmpl_travel_vlog",
            title = "Wanderlust Odyssey",
            category = "Travel",
            durationSec = 25,
            aspectRatio = AspectRatioType.RATIO_9_16,
            description = "Smooth whip pans, golden hour color tuning, and ambient breeze audio",
            beatsCount = 8,
            tags = listOf("Travel", "Nature", "Vlog"),
            defaultPrompt = "Create a vibrant travel montage with smooth transitions"
        ),
        TemplateItem(
            id = "tmpl_yt_intro",
            title = "Tech Channel YouTube Opener",
            category = "YouTube",
            durationSec = 10,
            aspectRatio = AspectRatioType.RATIO_16_9,
            description = "Futuristic 3D typography intro with glitch sound effects and dynamic title safe overlay",
            beatsCount = 4,
            tags = listOf("YouTube", "Tech", "Intro"),
            defaultPrompt = "Make a futuristic 16:9 YouTube title intro"
        ),
        TemplateItem(
            id = "tmpl_wedding_memory",
            title = "Eternal Royal Wedding",
            category = "Wedding",
            durationSec = 45,
            aspectRatio = AspectRatioType.RATIO_9_16,
            description = "Warm soft glow, elegant slow-motion speed curves, and classical strings",
            beatsCount = 5,
            tags = listOf("Wedding", "Love", "Emotional"),
            defaultPrompt = "Add warm romantic LUT, soft bokeh, and slow motion curves"
        ),
        TemplateItem(
            id = "tmpl_festival_diwali",
            title = "Diwali Festival of Lights",
            category = "Festival",
            durationSec = 20,
            aspectRatio = AspectRatioType.RATIO_9_16,
            description = "Golden sparkle particle overlays, festive beats, and celebratory Hindi titles",
            beatsCount = 10,
            tags = listOf("Festival", "Diwali", "Celebration"),
            defaultPrompt = "इस वीडियो में festive colors और Hindi captions लगाओ"
        ),
        TemplateItem(
            id = "tmpl_motivation_gym",
            title = "Hardcore Motivation",
            category = "Motivation",
            durationSec = 30,
            aspectRatio = AspectRatioType.RATIO_9_16,
            description = "High-contrast monochrome Noir, heavy impact hits, and bold kinetic typography",
            beatsCount = 14,
            tags = listOf("Gym", "Focus", "Success"),
            defaultPrompt = "High contrast black and white with impactful kinetic text"
        ),
        TemplateItem(
            id = "tmpl_birthday_cheer",
            title = "Happy Birthday Blast",
            category = "Birthday",
            durationSec = 15,
            aspectRatio = AspectRatioType.RATIO_9_16,
            description = "Confetti bursts, colorful pop animations, and upbeat celebratory tunes",
            beatsCount = 8,
            tags = listOf("Birthday", "Party", "Joy"),
            defaultPrompt = "Add colorful birthday animations and celebratory music"
        ),
        TemplateItem(
            id = "tmpl_shorts_viral",
            title = "Fast Facts Shorts",
            category = "Shorts",
            durationSec = 20,
            aspectRatio = AspectRatioType.RATIO_9_16,
            description = "Word-by-word animated auto captions with high-visibility yellow highlight boxes",
            beatsCount = 6,
            tags = listOf("Shorts", "Facts", "Captions"),
            defaultPrompt = "इस वीडियो में Hindi captions लगाओ"
        ),
        TemplateItem(
            id = "tmpl_aesthetic_status",
            title = "Retro VHS Chill",
            category = "Status",
            durationSec = 15,
            aspectRatio = AspectRatioType.RATIO_9_16,
            description = "1990s VHS tape glitch, tape tracking lines, and lo-fi chillhop background",
            beatsCount = 4,
            tags = listOf("Retro", "VHS", "Chill"),
            defaultPrompt = "Add vintage 1990s VHS tracking lines and lo-fi audio"
        )
    )

    fun getAIFeatures(): List<AIFeature> = listOf(
        AIFeature(
            id = "ai_auto_edit",
            title = "AI Auto Edit",
            subtitle = "Natural language prompt to full multi-track project",
            iconName = "auto_awesome",
            badge = "POPULAR",
            samplePrompt = "इस वीडियो को cinematic बना दो"
        ),
        AIFeature(
            id = "ai_auto_cut",
            title = "AI Auto Cut & Trim",
            subtitle = "Smart silence and mistake trimmer",
            iconName = "content_cut",
            samplePrompt = "Cut out all pauses and duplicate sentences"
        ),
        AIFeature(
            id = "ai_beat_sync",
            title = "AI Beat Sync",
            subtitle = "Automatically aligns clip cuts to music drops & transients",
            iconName = "graphic_eq",
            badge = "120 BPM",
            samplePrompt = "Beat के हिसाब से cuts लगाओ"
        ),
        AIFeature(
            id = "ai_bg_removal",
            title = "AI Background Removal",
            subtitle = "Zero green-screen instant person cutout & mask",
            iconName = "person_remove",
            samplePrompt = "Remove background and replace with neon cyber studio"
        ),
        AIFeature(
            id = "ai_captions",
            title = "AI Smart Captions",
            subtitle = "Auto speech-to-text with bilingual Hindi & English styles",
            iconName = "subtitles",
            badge = "MULTILINGUAL",
            samplePrompt = "इस वीडियो में Hindi captions लगाओ"
        ),
        AIFeature(
            id = "ai_upscaling",
            title = "AI Video Upscaler (4K)",
            subtitle = "Super-resolution neural edge detail reconstruction",
            iconName = "high_quality",
            samplePrompt = "Upscale 720p footage to crisp 4K Ultra HD"
        ),
        AIFeature(
            id = "ai_noise_reduction",
            title = "AI Audio Denoise & Voice Boost",
            subtitle = "Eliminate wind, traffic, and room echo instantly",
            iconName = "mic_external_on",
            samplePrompt = "Remove background hum and boost vocal clarity"
        ),
        AIFeature(
            id = "ai_script_gen",
            title = "AI Script & Storyboard",
            subtitle = "Generate hook, shot list, and narration lines",
            iconName = "movie_creation",
            badge = "NEW",
            samplePrompt = "Write a 30s viral teaser script for tech product"
        ),
        AIFeature(
            id = "ai_highlight_detect",
            title = "AI Highlight Detection",
            subtitle = "Finds peak energy moments, laughter, and high action",
            iconName = "insights",
            samplePrompt = "Extract top 3 intense highlights from 5min raw footage"
        ),
        AIFeature(
            id = "ai_object_remove",
            title = "AI Object Inpainting",
            subtitle = "Erase unwanted passersby, microphones, or logos",
            iconName = "brush",
            samplePrompt = "Erase background water bottle and blend seamlessly"
        ),
        AIFeature(
            id = "ai_face_tracking",
            title = "AI Face & Motion Tracking",
            subtitle = "Attach 3D text or stickers to tracked face coordinates",
            iconName = "track_changes",
            samplePrompt = "Track my face and attach neon glowing arrow"
        ),
        AIFeature(
            id = "ai_subtitle_translate",
            title = "AI Subtitle Translation",
            subtitle = "Translate English to Hindi, Spanish, Japanese in real-time",
            iconName = "translate",
            samplePrompt = "Translate video subtitles to Hindi and English bilingual"
        ),
        AIFeature(
            id = "ai_text_to_video",
            title = "Text-to-Video Engine",
            subtitle = "Generate motion clips directly from descriptive text",
            iconName = "smart_display",
            samplePrompt = "Cinematic slow-motion drone flyover of neon city at sunset"
        ),
        AIFeature(
            id = "ai_broll_suggest",
            title = "Auto B-Roll Suggestions",
            subtitle = "Contextual stock footage recommendations for voiceover",
            iconName = "video_library",
            samplePrompt = "Suggest 4 matching atmospheric B-Roll clips"
        )
    )
}
