package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext

/**
 * Tactile Haptic Signatures for the CASS Executive Experience.
 * Provides subtle, nuanced physical feedback for scans, touch interactions,
 * template selections, and security alerts.
 */
enum class CassHapticType {
    LIGHT_TICK,         // Navigation tabs, filter chips, radio choices, template carousel
    BUTTON_CLICK,       // Action buttons, toolbar controls, dialog confirms
    SCAN_SUCCESS,       // Dual-pulse camera shutter lock
    SECURITY_WARNING,   // Caution alert for flagged QR links
    SUCCESS_CELEBRATE,  // Copy to clipboard, vault save, high-res export
    TOGGLE_POP          // Switches, favorite toggles, flashlight toggle
}

object CassHaptics {

    private var isHapticsEnabled: Boolean = true

    fun setHapticsEnabled(enabled: Boolean) {
        isHapticsEnabled = enabled
    }

    fun isEnabled(): Boolean = isHapticsEnabled

    /**
     * Trigger a tailored tactile feedback pattern.
     */
    fun perform(context: Context, type: CassHapticType) {
        if (!isHapticsEnabled) return

        try {
            val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator == null || !vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                when (type) {
                    CassHapticType.LIGHT_TICK -> {
                        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                    }
                    CassHapticType.BUTTON_CLICK -> {
                        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    }
                    CassHapticType.SCAN_SUCCESS -> {
                        // Refined double pulse with amplitude curve: crisp lock + confirmation
                        val timings = longArrayOf(0, 14, 32, 20)
                        val amplitudes = intArrayOf(0, 170, 0, 240)
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    CassHapticType.SECURITY_WARNING -> {
                        // Distinct double-thud warning pulse
                        val timings = longArrayOf(0, 35, 40, 50)
                        val amplitudes = intArrayOf(0, 255, 0, 255)
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    CassHapticType.SUCCESS_CELEBRATE -> {
                        // Gentle ascending triple micro-burst for vault/export/copy
                        val timings = longArrayOf(0, 10, 25, 15, 25, 25)
                        val amplitudes = intArrayOf(0, 120, 0, 180, 0, 255)
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                    }
                    CassHapticType.TOGGLE_POP -> {
                        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    }
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                when (type) {
                    CassHapticType.LIGHT_TICK -> {
                        vibrator.vibrate(VibrationEffect.createOneShot(8, 80))
                    }
                    CassHapticType.BUTTON_CLICK, CassHapticType.TOGGLE_POP -> {
                        vibrator.vibrate(VibrationEffect.createOneShot(16, 180))
                    }
                    CassHapticType.SCAN_SUCCESS -> {
                        val timings = longArrayOf(0, 15, 35, 22)
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                    }
                    CassHapticType.SECURITY_WARNING -> {
                        val timings = longArrayOf(0, 40, 40, 55)
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                    }
                    CassHapticType.SUCCESS_CELEBRATE -> {
                        val timings = longArrayOf(0, 12, 25, 18, 25, 28)
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                when (type) {
                    CassHapticType.LIGHT_TICK -> vibrator.vibrate(8)
                    CassHapticType.BUTTON_CLICK, CassHapticType.TOGGLE_POP -> vibrator.vibrate(16)
                    CassHapticType.SCAN_SUCCESS -> vibrator.vibrate(longArrayOf(0, 15, 35, 22), -1)
                    CassHapticType.SECURITY_WARNING -> vibrator.vibrate(longArrayOf(0, 40, 40, 55), -1)
                    CassHapticType.SUCCESS_CELEBRATE -> vibrator.vibrate(longArrayOf(0, 12, 25, 18, 25, 28), -1)
                }
            }
        } catch (e: Exception) {
            // Silently catch in case device does not support vibration or is in DND
        }
    }
}

/**
 * Compose modifier extension that attaches a tactile haptic click to any clickable element.
 */
fun Modifier.cassClickable(
    hapticType: CassHapticType = CassHapticType.LIGHT_TICK,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    onClick: () -> Unit
): Modifier = composed {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    this.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        interactionSource = interactionSource,
        indication = androidx.compose.material3.ripple()
    ) {
        CassHaptics.perform(context, hapticType)
        onClick()
    }
}
