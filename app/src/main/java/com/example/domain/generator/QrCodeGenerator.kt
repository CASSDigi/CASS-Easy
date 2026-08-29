package com.example.domain.generator

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import com.example.domain.model.CenterLogoType
import com.example.domain.model.DotPattern
import com.example.domain.model.EyeStyle
import com.example.domain.model.GradientStyle
import com.example.domain.model.QrDesignConfig
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap
import kotlin.math.min

object QrCodeGenerator {

    fun generateQrBitmap(
        content: String,
        config: QrDesignConfig,
        size: Int = 1024,
        centerLogoBitmap: Bitmap? = null
    ): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.MARGIN] = config.margin
        
        val ecLevel = when (config.errorCorrectionLevel.uppercase()) {
            "L" -> ErrorCorrectionLevel.L
            "Q" -> ErrorCorrectionLevel.Q
            "H" -> ErrorCorrectionLevel.H
            else -> if (config.centerLogo != CenterLogoType.NONE || centerLogoBitmap != null) ErrorCorrectionLevel.H else ErrorCorrectionLevel.M
        }
        hints[EncodeHintType.ERROR_CORRECTION] = ecLevel

        val qrCodeWriter = QRCodeWriter()
        val textToEncode = content.ifEmpty { "https://cass-innovations.com" }
        val bitMatrix = qrCodeWriter.encode(textToEncode, BarcodeFormat.QR_CODE, 0, 0, hints)

        val matrixWidth = bitMatrix.width
        val matrixHeight = bitMatrix.height

        val hasFrame = config.frameText.isNotBlank()
        val framePaddingBottom = if (hasFrame) (size * 0.15f).toInt() else 0
        val totalHeight = size + framePaddingBottom

        val bitmap = Bitmap.createBitmap(size, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        if (config.transparentBackground) {
            bgPaint.color = AndroidColor.TRANSPARENT
        } else {
            bgPaint.color = config.bgColorHex.toInt()
            canvas.drawRect(0f, 0f, size.toFloat(), totalHeight.toFloat(), bgPaint)
        }

        val cellSize = size.toFloat() / matrixWidth

        // Setup Foreground Paint with Gradient or Solid
        val fgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        when (config.gradientStyle) {
            GradientStyle.SOLID -> {
                fgPaint.color = config.fgColorHex.toInt()
            }
            GradientStyle.CASS_GOLD -> {
                fgPaint.shader = LinearGradient(
                    0f, 0f, size.toFloat(), size.toFloat(),
                    intArrayOf(
                        AndroidColor.parseColor("#FFF2B2"),
                        AndroidColor.parseColor("#D4AF37"),
                        AndroidColor.parseColor("#AA820A")
                    ),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            GradientStyle.PLATINUM_SILVER -> {
                fgPaint.shader = LinearGradient(
                    0f, 0f, size.toFloat(), size.toFloat(),
                    intArrayOf(
                        AndroidColor.parseColor("#FFFFFF"),
                        AndroidColor.parseColor("#CBD5E1"),
                        AndroidColor.parseColor("#64748B")
                    ),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            GradientStyle.CYBER_CYAN -> {
                fgPaint.shader = LinearGradient(
                    0f, 0f, size.toFloat(), size.toFloat(),
                    intArrayOf(
                        AndroidColor.parseColor("#22D3EE"),
                        AndroidColor.parseColor("#0284C7"),
                        AndroidColor.parseColor("#3B82F6")
                    ),
                    floatArrayOf(0f, 0.6f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            GradientStyle.SUNSET_LUXE -> {
                fgPaint.shader = LinearGradient(
                    0f, 0f, size.toFloat(), size.toFloat(),
                    intArrayOf(
                        AndroidColor.parseColor("#F59E0B"),
                        AndroidColor.parseColor("#EC4899"),
                        AndroidColor.parseColor("#8B5CF6")
                    ),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            GradientStyle.OBSIDIAN_GOLD -> {
                fgPaint.shader = LinearGradient(
                    0f, 0f, size.toFloat(), size.toFloat(),
                    intArrayOf(
                        AndroidColor.parseColor("#FFDF73"),
                        AndroidColor.parseColor("#D4AF37"),
                        AndroidColor.parseColor("#4A3B00")
                    ),
                    floatArrayOf(0f, 0.7f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
        }

        // Helper to determine if a matrix coordinate belongs to corner eyes (Finder patterns)
        fun isFinderPattern(x: Int, y: Int): Boolean {
            val margin = config.margin
            val rightStart = matrixWidth - margin - 7
            val bottomStart = matrixHeight - margin - 7
            // Top-left
            if (x in margin until margin + 7 && y in margin until margin + 7) return true
            // Top-right
            if (x in rightStart until rightStart + 7 && y in margin until margin + 7) return true
            // Bottom-left
            if (x in margin until margin + 7 && y in bottomStart until bottomStart + 7) return true
            return false
        }

        // Draw body dots
        for (y in 0 until matrixHeight) {
            for (x in 0 until matrixWidth) {
                if (bitMatrix.get(x, y)) {
                    if (isFinderPattern(x, y)) {
                        // Skip finder patterns here to render them custom later
                        continue
                    }
                    val left = x * cellSize
                    val top = y * cellSize
                    val right = left + cellSize
                    val bottom = top + cellSize
                    val padding = cellSize * 0.08f

                    when (config.dotPattern) {
                        DotPattern.SQUARE -> {
                            canvas.drawRect(left, top, right, bottom, fgPaint)
                        }
                        DotPattern.ROUNDED -> {
                            val r = cellSize * 0.35f
                            canvas.drawRoundRect(
                                RectF(left + padding, top + padding, right - padding, bottom - padding),
                                r, r, fgPaint
                            )
                        }
                        DotPattern.CIRCLE -> {
                            val cx = left + cellSize / 2f
                            val cy = top + cellSize / 2f
                            val radius = (cellSize / 2f) - padding
                            canvas.drawCircle(cx, cy, radius, fgPaint)
                        }
                        DotPattern.DIAMOND -> {
                            val path = Path()
                            val cx = left + cellSize / 2f
                            val cy = top + cellSize / 2f
                            path.moveTo(cx, top + padding)
                            path.lineTo(right - padding, cy)
                            path.lineTo(cx, bottom - padding)
                            path.lineTo(left + padding, cy)
                            path.close()
                            canvas.drawPath(path, fgPaint)
                        }
                        DotPattern.CLASSY -> {
                            val r = cellSize * 0.5f
                            canvas.drawRoundRect(
                                RectF(left + padding, top + padding, right - padding, bottom - padding),
                                r, r, fgPaint
                            )
                        }
                    }
                }
            }
        }

        // Draw custom corner eye patterns
        fun drawEye(startX: Int, startY: Int) {
            val eyeLeft = startX * cellSize
            val eyeTop = startY * cellSize
            val eyeSize = 7 * cellSize
            val eyeRight = eyeLeft + eyeSize
            val eyeBottom = eyeTop + eyeSize

            val eyeBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            eyeBgPaint.color = if (config.transparentBackground) AndroidColor.TRANSPARENT else config.bgColorHex.toInt()

            when (config.eyeStyle) {
                EyeStyle.SQUARE -> {
                    // Outer square (7x7)
                    canvas.drawRect(eyeLeft, eyeTop, eyeRight, eyeBottom, fgPaint)
                    // Inner cutout (5x5)
                    canvas.drawRect(
                        eyeLeft + cellSize, eyeTop + cellSize,
                        eyeRight - cellSize, eyeBottom - cellSize,
                        eyeBgPaint
                    )
                    // Center square (3x3)
                    canvas.drawRect(
                        eyeLeft + 2 * cellSize, eyeTop + 2 * cellSize,
                        eyeRight - 2 * cellSize, eyeBottom - 2 * cellSize,
                        fgPaint
                    )
                }
                EyeStyle.ROUNDED -> {
                    val outerRadius = cellSize * 1.8f
                    val innerRadius = cellSize * 1.2f
                    val centerRadius = cellSize * 0.8f

                    canvas.drawRoundRect(RectF(eyeLeft, eyeTop, eyeRight, eyeBottom), outerRadius, outerRadius, fgPaint)
                    canvas.drawRoundRect(
                        RectF(eyeLeft + cellSize, eyeTop + cellSize, eyeRight - cellSize, eyeBottom - cellSize),
                        innerRadius, innerRadius, eyeBgPaint
                    )
                    canvas.drawRoundRect(
                        RectF(eyeLeft + 2 * cellSize, eyeTop + 2 * cellSize, eyeRight - 2 * cellSize, eyeBottom - 2 * cellSize),
                        centerRadius, centerRadius, fgPaint
                    )
                }
                EyeStyle.CIRCLE -> {
                    val cx = eyeLeft + eyeSize / 2f
                    val cy = eyeTop + eyeSize / 2f
                    canvas.drawCircle(cx, cy, eyeSize / 2f, fgPaint)
                    canvas.drawCircle(cx, cy, (5 * cellSize) / 2f, eyeBgPaint)
                    canvas.drawCircle(cx, cy, (3 * cellSize) / 2f, fgPaint)
                }
                EyeStyle.DOUBLE_FRAME -> {
                    val r = cellSize * 1.5f
                    canvas.drawRoundRect(RectF(eyeLeft, eyeTop, eyeRight, eyeBottom), r, r, fgPaint)
                    canvas.drawRoundRect(
                        RectF(eyeLeft + cellSize * 0.8f, eyeTop + cellSize * 0.8f, eyeRight - cellSize * 0.8f, eyeBottom - cellSize * 0.8f),
                        r, r, eyeBgPaint
                    )
                    canvas.drawRoundRect(
                        RectF(eyeLeft + cellSize * 1.5f, eyeTop + cellSize * 1.5f, eyeRight - cellSize * 1.5f, eyeBottom - cellSize * 1.5f),
                        cellSize * 0.8f, cellSize * 0.8f, fgPaint
                    )
                    canvas.drawRoundRect(
                        RectF(eyeLeft + cellSize * 2.2f, eyeTop + cellSize * 2.2f, eyeRight - cellSize * 2.2f, eyeBottom - cellSize * 2.2f),
                        cellSize * 0.5f, cellSize * 0.5f, eyeBgPaint
                    )
                    canvas.drawRoundRect(
                        RectF(eyeLeft + cellSize * 2.7f, eyeTop + cellSize * 2.7f, eyeRight - cellSize * 2.7f, eyeBottom - cellSize * 2.7f),
                        cellSize * 0.4f, cellSize * 0.4f, fgPaint
                    )
                }
                EyeStyle.FUTURISTIC -> {
                    val path = Path()
                    val chamfer = cellSize * 1.5f
                    path.moveTo(eyeLeft + chamfer, eyeTop)
                    path.lineTo(eyeRight - chamfer, eyeTop)
                    path.lineTo(eyeRight, eyeTop + chamfer)
                    path.lineTo(eyeRight, eyeBottom - chamfer)
                    path.lineTo(eyeRight - chamfer, eyeBottom)
                    path.lineTo(eyeLeft + chamfer, eyeBottom)
                    path.lineTo(eyeLeft, eyeBottom - chamfer)
                    path.lineTo(eyeLeft, eyeTop + chamfer)
                    path.close()
                    canvas.drawPath(path, fgPaint)

                    val innerPath = Path()
                    val inLeft = eyeLeft + cellSize
                    val inTop = eyeTop + cellSize
                    val inRight = eyeRight - cellSize
                    val inBottom = eyeBottom - cellSize
                    innerPath.moveTo(inLeft + chamfer * 0.7f, inTop)
                    innerPath.lineTo(inRight - chamfer * 0.7f, inTop)
                    innerPath.lineTo(inRight, inTop + chamfer * 0.7f)
                    innerPath.lineTo(inRight, inBottom - chamfer * 0.7f)
                    innerPath.lineTo(inRight - chamfer * 0.7f, inBottom)
                    innerPath.lineTo(inLeft + chamfer * 0.7f, inBottom)
                    innerPath.lineTo(inLeft, inBottom - chamfer * 0.7f)
                    innerPath.lineTo(inLeft, inTop + chamfer * 0.7f)
                    innerPath.close()
                    canvas.drawPath(innerPath, eyeBgPaint)

                    val centerR = cellSize * 1.2f
                    canvas.drawRoundRect(
                        RectF(eyeLeft + 2 * cellSize, eyeTop + 2 * cellSize, eyeRight - 2 * cellSize, eyeBottom - 2 * cellSize),
                        centerR, centerR, fgPaint
                    )
                }
            }
        }

        val margin = config.margin
        val rightStart = matrixWidth - margin - 7
        val bottomStart = matrixHeight - margin - 7
        drawEye(margin, margin) // Top-Left
        drawEye(rightStart, margin) // Top-Right
        drawEye(margin, bottomStart) // Bottom-Left

        // Center Logo Overlay
        if (centerLogoBitmap != null || config.centerLogo != CenterLogoType.NONE) {
            val logoBoxSize = (size * 0.22f).toInt()
            val logoLeft = (size - logoBoxSize) / 2f
            val logoTop = (size - logoBoxSize) / 2f
            val logoRight = logoLeft + logoBoxSize
            val logoBottom = logoTop + logoBoxSize

            // Background badge for logo
            val logoBadgePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            logoBadgePaint.color = config.bgColorHex.toInt()
            val cornerRadius = logoBoxSize * 0.25f
            canvas.drawRoundRect(RectF(logoLeft, logoTop, logoRight, logoBottom), cornerRadius, cornerRadius, logoBadgePaint)

            // Luxury gold border around badge
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = AndroidColor.parseColor("#D4AF37")
                style = Paint.Style.STROKE
                strokeWidth = logoBoxSize * 0.04f
            }
            canvas.drawRoundRect(RectF(logoLeft, logoTop, logoRight, logoBottom), cornerRadius, cornerRadius, borderPaint)

            if (centerLogoBitmap != null) {
                val logoInnerPadding = (logoBoxSize * 0.15f).toInt()
                val logoInnerSize = logoBoxSize - (logoInnerPadding * 2)
                val scaledLogo = Bitmap.createScaledBitmap(centerLogoBitmap, logoInnerSize, logoInnerSize, true)
                canvas.drawBitmap(scaledLogo, logoLeft + logoInnerPadding, logoTop + logoInnerPadding, null)
            } else {
                // Draw CASS Monogram or symbol if no custom bitmap provided
                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = AndroidColor.parseColor("#D4AF37")
                    textSize = logoBoxSize * 0.32f
                    typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                }
                val label = when (config.centerLogo) {
                    CenterLogoType.CASS_LOGO -> "CASS"
                    CenterLogoType.CUSTOM_IMAGE -> "★"
                    CenterLogoType.WIFI -> "WIFI"
                    CenterLogoType.LINK -> "LINK"
                    CenterLogoType.CONTACT -> "ID"
                    CenterLogoType.LOCK -> "VAULT"
                    CenterLogoType.STAR -> "★"
                    CenterLogoType.NONE -> ""
                }
                val textY = logoTop + (logoBoxSize / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
                canvas.drawText(label, logoLeft + (logoBoxSize / 2f), textY, textPaint)
            }
        }

        // Custom CTA Frame Badge
        if (hasFrame) {
            val frameTop = size.toFloat()
            val frameBottom = totalHeight.toFloat()
            val badgeHeight = framePaddingBottom * 0.7f
            val badgeTop = frameTop + (framePaddingBottom - badgeHeight) / 2f
            val badgeBottom = badgeTop + badgeHeight
            val badgeWidth = size * 0.8f
            val badgeLeft = (size - badgeWidth) / 2f
            val badgeRight = badgeLeft + badgeWidth

            val frameBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    badgeLeft, badgeTop, badgeRight, badgeBottom,
                    intArrayOf(AndroidColor.parseColor("#D4AF37"), AndroidColor.parseColor("#AA820A")),
                    null, Shader.TileMode.CLAMP
                )
            }
            val radius = badgeHeight * 0.5f
            canvas.drawRoundRect(RectF(badgeLeft, badgeTop, badgeRight, badgeBottom), radius, radius, frameBgPaint)

            val frameTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = AndroidColor.parseColor("#07080A")
                textSize = badgeHeight * 0.45f
                typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                letterSpacing = 0.08f
            }
            val textY = badgeTop + (badgeHeight / 2f) - ((frameTextPaint.descent() + frameTextPaint.ascent()) / 2f)
            canvas.drawText(config.frameText.uppercase(), size / 2f, textY, frameTextPaint)
        }

        return bitmap
    }

    fun generateSvg(content: String, config: QrDesignConfig): String {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.MARGIN] = config.margin
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

        val qrCodeWriter = QRCodeWriter()
        val textToEncode = content.ifEmpty { "https://cass-innovations.com" }
        val bitMatrix = qrCodeWriter.encode(textToEncode, BarcodeFormat.QR_CODE, 0, 0, hints)

        val size = 500
        val matrixWidth = bitMatrix.width
        val matrixHeight = bitMatrix.height
        val cellSize = size.toFloat() / matrixWidth

        val fgHex = String.format("#%06X", 0xFFFFFF and config.fgColorHex.toInt())
        val bgHex = if (config.transparentBackground) "none" else String.format("#%06X", 0xFFFFFF and config.bgColorHex.toInt())

        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="utf-8"?>
<svg xmlns="http://www.w3.org/2000/svg" version="1.1" width="$size" height="$size" viewBox="0 0 $size $size">
<rect width="$size" height="$size" fill="$bgHex"/>
<g fill="$fgHex">
""")

        for (y in 0 until matrixHeight) {
            for (x in 0 until matrixWidth) {
                if (bitMatrix.get(x, y)) {
                    val px = x * cellSize
                    val py = y * cellSize
                    sb.append("""  <rect x="${"%.2f".format(px)}" y="${"%.2f".format(py)}" width="${"%.2f".format(cellSize)}" height="${"%.2f".format(cellSize)}" rx="${"%.2f".format(cellSize * 0.2f)}" />
""")
                }
            }
        }

        sb.append("</g>\n</svg>")
        return sb.toString()
    }
}
