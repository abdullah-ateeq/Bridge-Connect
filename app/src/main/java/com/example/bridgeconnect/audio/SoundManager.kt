package com.example.bridgeconnect.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playNodePlaced(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            playTone(frequency = 600.0, durationMs = 60, decay = true)
        }
    }

    fun playBridgeFormed(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            playDualTone(freq1 = 523.25, freq2 = 659.25, durationMs = 130)
        }
    }

    fun playTurnSwitch(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            playTone(frequency = 440.0, durationMs = 35, decay = true)
        }
    }

    fun playWin(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50) // C5 - E5 - G5 - C6
            for (freq in notes) {
                playTone(frequency = freq, durationMs = 100, decay = true)
            }
        }
    }

    fun playDraw(enabled: Boolean) {
        if (!enabled) return
        scope.launch {
            val notes = listOf(587.33, 440.0)
            for (freq in notes) {
                playTone(frequency = freq, durationMs = 130, decay = true)
            }
        }
    }

    private fun playTone(frequency: Double, durationMs: Int, decay: Boolean = false) {
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            if (numSamples <= 0) return
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val rawSine = sin(2.0 * Math.PI * frequency * time)
                val envelope = if (decay) {
                    val progress = i.toDouble() / numSamples
                    (1.0 - progress) * (1.0 - progress)
                } else {
                    1.0
                }
                buffer[i] = (rawSine * envelope * 0.35 * Short.MAX_VALUE).toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
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
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 5L)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // Silently ignore audio playback failures on unusual devices
        }
    }

    private fun playDualTone(freq1: Double, freq2: Double, durationMs: Int) {
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            if (numSamples <= 0) return
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toDouble() / sampleRate
                val rawSine1 = sin(2.0 * Math.PI * freq1 * time)
                val rawSine2 = sin(2.0 * Math.PI * freq2 * time)
                val envelope = 1.0 - (i.toDouble() / numSamples)
                val sample = ((rawSine1 + rawSine2) / 2.0) * envelope * 0.35 * Short.MAX_VALUE
                buffer[i] = sample.toInt().toShort()
            }

            val audioTrack = AudioTrack.Builder()
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
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 5L)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) {
            // Silently ignore
        }
    }
}
