package com.joelkanyi.flip.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.joelkanyi.flip.R

class SoundManager(context: Context) {

    private val soundPool: SoundPool
    private var coinFlipSoundId: Int = 0
    private var coinSpinSoundId: Int = 0
    private var isLoaded = false
    private var loadedCount = 0

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                loadedCount++
                if (loadedCount >= 2) {
                    isLoaded = true
                }
            }
        }

        // Load sounds
        coinFlipSoundId = soundPool.load(context, R.raw.coin_flip, 1)
        coinSpinSoundId = soundPool.load(context, R.raw.coin_spin, 1)
    }

    fun playCoinSpin() {
        if (isLoaded && coinSpinSoundId != 0) {
            soundPool.play(
                coinSpinSoundId,
                0.5f,  // Left volume (lower for spinning)
                0.5f,  // Right volume
                1,     // Priority
                0,     // Loop (0 = no loop)
                1.2f   // Playback rate (slightly faster)
            )
        }
    }

    fun playCoinLand() {
        if (isLoaded && coinFlipSoundId != 0) {
            soundPool.play(
                coinFlipSoundId,
                0.8f,  // Left volume
                0.8f,  // Right volume
                1,     // Priority
                0,     // Loop (0 = no loop)
                1.0f   // Playback rate
            )
        }
    }

    fun release() {
        soundPool.release()
    }
}
