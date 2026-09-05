package com.example.data.db

import com.example.data.model.*
import org.json.JSONArray
import org.json.JSONObject

object ProjectJsonConverter {

    fun tracksToJson(tracks: List<Track>): String {
        val root = JSONArray()
        for (track in tracks) {
            val trackObj = JSONObject()
            trackObj.put("id", track.id)
            trackObj.put("type", track.type.name)
            trackObj.put("name", track.name)
            trackObj.put("isMuted", track.isMuted)
            trackObj.put("isLocked", track.isLocked)
            trackObj.put("isHidden", track.isHidden)

            val clipsArray = JSONArray()
            for (clip in track.clips) {
                val clipObj = JSONObject()
                clipObj.put("id", clip.id)
                clipObj.put("trackId", clip.trackId)
                clipObj.put("title", clip.title)
                clipObj.put("mediaUri", clip.mediaUri)
                clipObj.put("mediaType", clip.mediaType.name)
                clipObj.put("startMs", clip.startMs)
                clipObj.put("endMs", clip.endMs)
                clipObj.put("sourceDurationMs", clip.sourceDurationMs)
                clipObj.put("speed", clip.speed.toDouble())
                clipObj.put("speedCurve", clip.speedCurve)
                clipObj.put("volume", clip.volume.toDouble())
                clipObj.put("isMuted", clip.isMuted)
                clipObj.put("filter", clip.filter.name)
                clipObj.put("effect", clip.effect.name)
                clipObj.put("effectIntensity", clip.effectIntensity.toDouble())
                clipObj.put("transitionIn", clip.transitionIn.name)
                clipObj.put("transitionDurationMs", clip.transitionDurationMs)
                clipObj.put("scale", clip.scale.toDouble())
                clipObj.put("rotation", clip.rotation.toDouble())
                clipObj.put("opacity", clip.opacity.toDouble())
                clipObj.put("posX", clip.posX.toDouble())
                clipObj.put("posY", clip.posY.toDouble())
                clipObj.put("maskType", clip.maskType.name)
                clipObj.put("blendMode", clip.blendMode.name)
                clipObj.put("chromaKeyEnabled", clip.chromaKeyEnabled)
                clipObj.put("bgRemovalEnabled", clip.bgRemovalEnabled)
                clipObj.put("textContent", clip.textContent)
                clipObj.put("fontStyle", clip.fontStyle)
                clipObj.put("fontSize", clip.fontSize.toDouble())
                clipObj.put("textColorHex", clip.textColorHex)
                clipObj.put("textGlowHex", clip.textGlowHex)
                clipObj.put("isCurvedText", clip.isCurvedText)
                clipObj.put("is3DText", clip.is3DText)

                // Subtitles
                val subsArray = JSONArray()
                for (sub in clip.subtitles) {
                    val subObj = JSONObject()
                    subObj.put("id", sub.id)
                    subObj.put("startMs", sub.startMs)
                    subObj.put("endMs", sub.endMs)
                    subObj.put("text", sub.text)
                    subObj.put("translation", sub.translation)
                    subsArray.put(subObj)
                }
                clipObj.put("subtitles", subsArray)

                // Keyframes
                val kfArray = JSONArray()
                for (kf in clip.keyframes) {
                    val kfObj = JSONObject()
                    kfObj.put("id", kf.id)
                    kfObj.put("timeMs", kf.timeMs)
                    kfObj.put("posX", kf.posX.toDouble())
                    kfObj.put("posY", kf.posY.toDouble())
                    kfObj.put("scale", kf.scale.toDouble())
                    kfObj.put("rotation", kf.rotation.toDouble())
                    kfObj.put("opacity", kf.opacity.toDouble())
                    kfArray.put(kfObj)
                }
                clipObj.put("keyframes", kfArray)

                clipsArray.put(clipObj)
            }
            trackObj.put("clips", clipsArray)
            root.put(trackObj)
        }
        return root.toString()
    }

    fun jsonToTracks(jsonStr: String): List<Track> {
        if (jsonStr.isBlank()) return emptyList()
        val result = mutableListOf<Track>()
        try {
            val root = JSONArray(jsonStr)
            for (i in 0 until root.length()) {
                val trackObj = root.getJSONObject(i)
                val id = trackObj.optString("id", "")
                val type = try { TrackType.valueOf(trackObj.optString("type", "VIDEO")) } catch (e: Exception) { TrackType.VIDEO }
                val name = trackObj.optString("name", "Track")
                val isMuted = trackObj.optBoolean("isMuted", false)
                val isLocked = trackObj.optBoolean("isLocked", false)
                val isHidden = trackObj.optBoolean("isHidden", false)

                val clipsList = mutableListOf<ClipItem>()
                val clipsArray = trackObj.optJSONArray("clips")
                if (clipsArray != null) {
                    for (j in 0 until clipsArray.length()) {
                        val clipObj = clipsArray.getJSONObject(j)
                        val clipId = clipObj.optString("id", "")
                        val clipTrackId = clipObj.optString("trackId", id)
                        val title = clipObj.optString("title", "Clip")
                        val mediaUri = clipObj.optString("mediaUri", "")
                        val mediaType = try { MediaType.valueOf(clipObj.optString("mediaType", "VIDEO")) } catch (e: Exception) { MediaType.VIDEO }
                        val startMs = clipObj.optLong("startMs", 0L)
                        val endMs = clipObj.optLong("endMs", 3000L)
                        val sourceDurationMs = clipObj.optLong("sourceDurationMs", 3000L)
                        val speed = clipObj.optDouble("speed", 1.0).toFloat()
                        val speedCurve = clipObj.optString("speedCurve", "Normal")
                        val volume = clipObj.optDouble("volume", 1.0).toFloat()
                        val isClipMuted = clipObj.optBoolean("isMuted", false)
                        val filter = try { FilterLUT.valueOf(clipObj.optString("filter", "NONE")) } catch (e: Exception) { FilterLUT.NONE }
                        val effect = try { EffectPreset.valueOf(clipObj.optString("effect", "NONE")) } catch (e: Exception) { EffectPreset.NONE }
                        val effectIntensity = clipObj.optDouble("effectIntensity", 0.8).toFloat()
                        val transitionIn = try { TransitionType.valueOf(clipObj.optString("transitionIn", "NONE")) } catch (e: Exception) { TransitionType.NONE }
                        val transitionDurationMs = clipObj.optLong("transitionDurationMs", 500L)
                        val scale = clipObj.optDouble("scale", 1.0).toFloat()
                        val rotation = clipObj.optDouble("rotation", 0.0).toFloat()
                        val opacity = clipObj.optDouble("opacity", 1.0).toFloat()
                        val posX = clipObj.optDouble("posX", 0.0).toFloat()
                        val posY = clipObj.optDouble("posY", 0.0).toFloat()
                        val maskType = try { MaskType.valueOf(clipObj.optString("maskType", "NONE")) } catch (e: Exception) { MaskType.NONE }
                        val blendMode = try { BlendModeType.valueOf(clipObj.optString("blendMode", "NORMAL")) } catch (e: Exception) { BlendModeType.NORMAL }
                        val chromaKeyEnabled = clipObj.optBoolean("chromaKeyEnabled", false)
                        val bgRemovalEnabled = clipObj.optBoolean("bgRemovalEnabled", false)
                        val textContent = clipObj.optString("textContent", "")
                        val fontStyle = clipObj.optString("fontStyle", "Futura Bold")
                        val fontSize = clipObj.optDouble("fontSize", 24.0).toFloat()
                        val textColorHex = clipObj.optString("textColorHex", "#FFFFFF")
                        val textGlowHex = clipObj.optString("textGlowHex", "#A855F7")
                        val isCurvedText = clipObj.optBoolean("isCurvedText", false)
                        val is3DText = clipObj.optBoolean("is3DText", false)

                        val subList = mutableListOf<SubtitleItem>()
                        val subArr = clipObj.optJSONArray("subtitles")
                        if (subArr != null) {
                            for (k in 0 until subArr.length()) {
                                val sObj = subArr.getJSONObject(k)
                                subList.add(
                                    SubtitleItem(
                                        id = sObj.optString("id", ""),
                                        startMs = sObj.optLong("startMs", 0L),
                                        endMs = sObj.optLong("endMs", 1000L),
                                        text = sObj.optString("text", ""),
                                        translation = sObj.optString("translation", "")
                                    )
                                )
                            }
                        }

                        val kfList = mutableListOf<KeyframePoint>()
                        val kfArr = clipObj.optJSONArray("keyframes")
                        if (kfArr != null) {
                            for (k in 0 until kfArr.length()) {
                                val kfObj = kfArr.getJSONObject(k)
                                kfList.add(
                                    KeyframePoint(
                                        id = kfObj.optString("id", ""),
                                        timeMs = kfObj.optLong("timeMs", 0L),
                                        posX = kfObj.optDouble("posX", 0.0).toFloat(),
                                        posY = kfObj.optDouble("posY", 0.0).toFloat(),
                                        scale = kfObj.optDouble("scale", 1.0).toFloat(),
                                        rotation = kfObj.optDouble("rotation", 0.0).toFloat(),
                                        opacity = kfObj.optDouble("opacity", 1.0).toFloat()
                                    )
                                )
                            }
                        }

                        clipsList.add(
                            ClipItem(
                                id = clipId,
                                trackId = clipTrackId,
                                title = title,
                                mediaUri = mediaUri,
                                mediaType = mediaType,
                                startMs = startMs,
                                endMs = endMs,
                                sourceDurationMs = sourceDurationMs,
                                speed = speed,
                                speedCurve = speedCurve,
                                volume = volume,
                                isMuted = isClipMuted,
                                filter = filter,
                                effect = effect,
                                effectIntensity = effectIntensity,
                                transitionIn = transitionIn,
                                transitionDurationMs = transitionDurationMs,
                                scale = scale,
                                rotation = rotation,
                                opacity = opacity,
                                posX = posX,
                                posY = posY,
                                maskType = maskType,
                                blendMode = blendMode,
                                chromaKeyEnabled = chromaKeyEnabled,
                                bgRemovalEnabled = bgRemovalEnabled,
                                textContent = textContent,
                                fontStyle = fontStyle,
                                fontSize = fontSize,
                                textColorHex = textColorHex,
                                textGlowHex = textGlowHex,
                                isCurvedText = isCurvedText,
                                is3DText = is3DText,
                                subtitles = subList,
                                keyframes = kfList
                            )
                        )
                    }
                }
                result.add(
                    Track(
                        id = id,
                        type = type,
                        name = name,
                        isMuted = isMuted,
                        isLocked = isLocked,
                        isHidden = isHidden,
                        clips = clipsList
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
}
