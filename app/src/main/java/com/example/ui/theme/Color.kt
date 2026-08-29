package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// CASS Primary Luxury Gold Palette (Professional Polish)
val CassGold = Color(0xFFD4AF37)
val CassGoldLight = Color(0xFFF5E6B3)
val CassGoldBright = Color(0xFFFFDF73)
val CassGoldDark = Color(0xFFAA8B24)
val CassGoldDeep = Color(0xFF8A6D15)
val CassGoldMuted = Color(0xFFC5A059)
val CassGoldGlow = Color(0x4DD4AF37)

// CASS Metallic Silver & Platinum Palette
val CassSilver = Color(0xFFE2E8F0)
val CassSilverLight = Color(0xFFF8FAFC)
val CassSilverMuted = Color(0xFF94A3B8)
val CassSilverDark = Color(0xFF64748B)

// Professional Polish Midnight Canvas & Surface Hierarchy
val CassObsidian = Color(0xFF050505)
val CassCharcoal = Color(0xFF0A0A0A)
val CassSurface = Color(0xFF121212)
val CassSurfaceElevated = Color(0xFF181818)
val CassSurfaceHighlight = Color(0xFF222222)
val CassBorder = Color(0x1AFFFFFF)
val CassBorderSubtle = Color(0x0DFFFFFF)
val CassBorderGold = Color(0x59D4AF37)
val CassBorderGoldActive = Color(0xFFD4AF37)

// Light Theme Palette
val CassLightBg = Color(0xFFF6F7FA)
val CassLightSurface = Color(0xFFFFFFFF)
val CassLightSurfaceElevated = Color(0xFFF0F2F7)
val CassLightBorder = Color(0xFFE2E5EC)
val CassLightText = Color(0xFF0F172A)
val CassLightTextSecondary = Color(0xFF64748B)

// Accent & Security Status Colors
val CassEmerald = Color(0xFF10B981)
val CassEmeraldGlow = Color(0x3310B981)
val CassAmber = Color(0xFFF59E0B)
val CassAmberGlow = Color(0x33F59E0B)
val CassCrimson = Color(0xFFEF4444)
val CassCrimsonGlow = Color(0x33EF4444)
val CassCyan = Color(0xFF06B6D4)
val CassPurple = Color(0xFF8B5CF6)

// Gradients
val CassGoldGradient = Brush.linearGradient(
    colors = listOf(CassGold, CassGoldDark, CassGoldDeep)
)
val CassGoldButtonGradient = Brush.horizontalGradient(
    colors = listOf(CassGold, CassGoldDark)
)
val CassDarkCardGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF181818), Color(0xFF0F0F0F), Color(0xFF080808))
)
val CassGoldCardGradient = Brush.linearGradient(
    colors = listOf(Color(0x26D4AF37), Color(0x08D4AF37))
)
val CassSilverGradient = Brush.linearGradient(
    colors = listOf(CassSilverLight, CassSilver, CassSilverDark)
)
val CassObsidianGradient = Brush.verticalGradient(
    colors = listOf(CassCharcoal, CassObsidian)
)

