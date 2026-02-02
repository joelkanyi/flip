package com.joelkanyi.flip.haptic

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.getSystemService

class HapticManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService<VibratorManager>()?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService<Vibrator>()
    }

    fun vibrateOnLand() {
        vibrator?.let { vib ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 50, 30, 20)
                val amplitudes = intArrayOf(0, 200, 0, 100)
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vib.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(longArrayOf(0, 50, 30, 20), -1)
            }
        }
    }
}
