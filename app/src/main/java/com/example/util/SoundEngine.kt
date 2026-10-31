package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class BgmScene {
    NONE,
    HOME,
    EXAM,
    PET_ZONE,
    SHOP,
    RESULT
}

enum class SfxType {
    BUTTON_CLICK,
    CORRECT_ANSWER,
    WRONG_ANSWER,
    COMBO,
    LEVEL_UP,
    ACHIEVEMENT,
    PURCHASE,
    EGG_SHAKE,
    EGG_BREAK,
    PET_REVEAL,
    STREAK_MILESTONE
}

object SoundEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var bgmJob: Job? = null
    private var currentScene: BgmScene = BgmScene.NONE

    @Volatile var bgmEnabled: Boolean = true
    @Volatile var sfxEnabled: Boolean = true
    @Volatile var bgmVolume: Float = 0.35f
    @Volatile var sfxVolume: Float = 0.65f
    @Volatile var muteAll: Boolean = false
    @Volatile var userHasInteracted: Boolean = false

    fun updateSettings(
        bgmOn: Boolean,
        sfxOn: Boolean,
        bgmVol: Float,
        sfxVol: Float,
        muted: Boolean
    ) {
        bgmEnabled = bgmOn
        sfxEnabled = sfxOn
        bgmVolume = bgmVol.coerceIn(0f, 1f)
        sfxVolume = sfxVol.coerceIn(0f, 1f)
        muteAll = muted
        if (muteAll || !bgmEnabled) {
            bgmJob?.cancel()
            bgmJob = null
        } else if (userHasInteracted && currentScene != BgmScene.NONE && bgmJob?.isActive != true) {
            startBgmLoop(currentScene)
        }
    }

    fun notifyUserInteraction() {
        if (!userHasInteracted) {
            userHasInteracted = true
            if (!muteAll && bgmEnabled && currentScene != BgmScene.NONE && bgmJob?.isActive != true) {
                startBgmLoop(currentScene)
            }
        }
    }

    fun setScene(scene: BgmScene) {
        if (currentScene == scene && bgmJob?.isActive == true) return
        currentScene = scene
        bgmJob?.cancel()
        bgmJob = null
        if (scene != BgmScene.NONE && userHasInteracted && !muteAll && bgmEnabled) {
            startBgmLoop(scene)
        }
    }

    private fun startBgmLoop(scene: BgmScene) {
        bgmJob?.cancel()
        bgmJob = scope.launch {
            val notes: List<Pair<Double, Int>> = when (scene) {
                BgmScene.HOME -> listOf(
                    523.25 to 320, 659.25 to 320, 783.99 to 320, 1046.50 to 480,
                    880.00 to 320, 783.99 to 320, 659.25 to 480, 587.33 to 480
                )
                BgmScene.EXAM -> listOf(
                    392.00 to 520, 440.00 to 520, 493.88 to 520, 523.25 to 680,
                    493.88 to 520, 440.00 to 680
                )
                BgmScene.PET_ZONE -> listOf(
                    659.25 to 280, 783.99 to 280, 987.77 to 280, 1174.66 to 420,
                    1046.50 to 280, 783.99 to 420, 880.00 to 420
                )
                BgmScene.SHOP -> listOf(
                    587.33 to 240, 739.99 to 240, 880.00 to 240, 1174.66 to 360,
                    987.77 to 240, 880.00 to 360
                )
                BgmScene.RESULT -> listOf(
                    523.25 to 220, 659.25 to 220, 783.99 to 220, 1046.50 to 440,
                    1318.51 to 520
                )
                BgmScene.NONE -> emptyList()
            }
            while (isActive && !muteAll && bgmEnabled && notes.isNotEmpty()) {
                for ((freq, durMs) in notes) {
                    if (!isActive || muteAll || !bgmEnabled) break
                    val effectiveVol = (bgmVolume * 0.16f).coerceIn(0f, 0.25f)
                    if (effectiveVol > 0.005f) {
                        playToneSequence(listOf(freq to durMs), effectiveVol, softEnvelope = true)
                    }
                    delay(110L)
                }
                delay(450L)
            }
        }
    }

    fun playSfx(type: SfxType) {
        notifyUserInteraction()
        if (muteAll || !sfxEnabled) return
        val vol = (sfxVolume * 0.42f).coerceIn(0f, 0.6f)
        if (vol <= 0.01f) return
        scope.launch {
            val sequence: List<Pair<Double, Int>> = when (type) {
                SfxType.BUTTON_CLICK -> listOf(880.0 to 38)
                SfxType.CORRECT_ANSWER -> listOf(659.25 to 75, 880.0 to 90, 1046.50 to 135)
                SfxType.WRONG_ANSWER -> listOf(311.13 to 110, 261.63 to 160)
                SfxType.COMBO -> listOf(587.33 to 65, 783.99 to 65, 987.77 to 65, 1174.66 to 140)
                SfxType.LEVEL_UP -> listOf(523.25 to 85, 659.25 to 85, 783.99 to 85, 1046.50 to 110, 1318.51 to 210)
                SfxType.ACHIEVEMENT -> listOf(659.25 to 90, 830.61 to 90, 987.77 to 90, 1318.51 to 220)
                SfxType.PURCHASE -> listOf(783.99 to 65, 1046.50 to 65, 1567.98 to 150)
                SfxType.EGG_SHAKE -> listOf(380.0 to 55, 430.0 to 55)
                SfxType.EGG_BREAK -> listOf(520.0 to 60, 740.0 to 60, 960.0 to 90)
                SfxType.PET_REVEAL -> listOf(659.25 to 80, 783.99 to 80, 1046.50 to 100, 1318.51 to 120, 1567.98 to 260)
                SfxType.STREAK_MILESTONE -> listOf(587.33 to 75, 880.0 to 75, 1174.66 to 180)
            }
            playToneSequence(sequence, vol, softEnvelope = false)
        }
    }

    private fun playToneSequence(
        tones: List<Pair<Double, Int>>,
        volume: Float,
        softEnvelope: Boolean
    ) {
        val sampleRate = 22050
        val totalMs = tones.sumOf { it.second }.coerceAtLeast(20)
        val totalSamples = (sampleRate * totalMs) / 1000
        if (totalSamples <= 0) return

        val pcm = ShortArray(totalSamples)
        var offset = 0
        for ((freq, ms) in tones) {
            val count = ((sampleRate * ms) / 1000).coerceAtMost(totalSamples - offset)
            for (i in 0 until count) {
                val t = i.toDouble() / sampleRate
                val decay = if (softEnvelope) {
                    val attack = (i.toDouble() / (count * 0.15).coerceAtLeast(1.0)).coerceAtMost(1.0)
                    val release = ((count - i).toDouble() / (count * 0.35).coerceAtLeast(1.0)).coerceAtMost(1.0)
                    attack * release
                } else {
                    exp(-3.2 * (i.toDouble() / count.coerceAtLeast(1)))
                }
                val wave = sin(2.0 * PI * freq * t) + 0.25 * sin(4.0 * PI * freq * t)
                val sample = (wave * 0.75 * decay * volume * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                pcm[offset + i] = sample.toShort()
            }
            offset += count
        }

        var track: AudioTrack? = null
        try {
            val byteSize = pcm.size * 2
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(byteSize.coerceAtLeast(2048))
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(pcm, 0, pcm.size)
            track.play()
            Thread.sleep(totalMs.toLong() + 25L)
        } catch (_: Throwable) {
            // Audio device unavailable in headless/test environments; ignore safely
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Throwable) {
            }
        }
    }
}
