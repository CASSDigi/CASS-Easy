package com.example.domain.model

import androidx.compose.ui.graphics.Color
import java.io.Serializable

enum class QrContentType(val displayName: String) {
    URL("Website URL"),
    WIFI("Wi-Fi Network"),
    CONTACT_VCARD("Contact / vCard"),
    PHONE("Phone Number"),
    SMS("SMS Message"),
    EMAIL("Email Address"),
    GEO_LOCATION("Map Location"),
    CALENDAR_EVENT("Calendar Event"),
    PAYMENT_CRYPTO("Payment & Crypto"),
    SOCIAL_LINK("Social Profile"),
    APP_LINK("App Deep Link"),
    PLAIN_TEXT("Plain Text"),
    BARCODE_PRODUCT("Product Barcode"),
    DOCUMENT_NOTE("Document / Note")
}

data class ParsedQrContent(
    val rawText: String,
    val type: QrContentType,
    val title: String,
    val subtitle: String,
    val details: Map<String, String> = emptyMap(),
    val actionUrl: String? = null,
    val isSecure: Boolean = true,
    val securityResult: SecurityAnalysisResult? = null,
    val barcodeFormat: String = "QR Code",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

enum class SecurityRating(val label: String, val colorHex: Long) {
    VERIFIED_SAFE("Verified Safe", 0xFF10B981),
    SAFE("Safe", 0xFF10B981),
    CAUTION("Proceed with Caution", 0xFFF59E0B),
    SUSPICIOUS("Suspicious / High Risk", 0xFFEF4444)
}

data class SecurityAnalysisResult(
    val rating: SecurityRating,
    val score: Int, // 0 to 100
    val riskFactors: List<String>,
    val domain: String?,
    val isHttps: Boolean,
    val explanation: String
) : Serializable

enum class DotPattern(val displayName: String) {
    SQUARE("Square"),
    ROUNDED("Smooth Rounded"),
    CIRCLE("Modern Dots"),
    DIAMOND("Diamond Luxe"),
    CLASSY("Classy Modules")
}

enum class EyeStyle(val displayName: String) {
    SQUARE("Standard Square"),
    ROUNDED("Executive Rounded"),
    CIRCLE("Concentric Circle"),
    DOUBLE_FRAME("Double Metal Ring"),
    FUTURISTIC("Futuristic Bevel")
}

enum class GradientStyle(val displayName: String) {
    SOLID("Solid Color"),
    CASS_GOLD("CASS Royal Gold"),
    PLATINUM_SILVER("Platinum Silver"),
    CYBER_CYAN("Neon Cyber"),
    SUNSET_LUXE("Sunset Luxury"),
    OBSIDIAN_GOLD("Obsidian Gold")
}

enum class CenterLogoType(val displayName: String) {
    NONE("None"),
    CUSTOM_IMAGE("Custom Photo"),
    CASS_LOGO("CASS Emblem"),
    LINK("Web Link"),
    WIFI("Wi-Fi"),
    CONTACT("Contact"),
    LOCK("Secure Vault"),
    STAR("VIP Star")
}

data class QrDesignConfig(
    val fgColorHex: Long = 0xFFD4AF37, // CassGold
    val bgColorHex: Long = 0xFF0A0B0E, // Dark Charcoal
    val gradientStyle: GradientStyle = GradientStyle.CASS_GOLD,
    val dotPattern: DotPattern = DotPattern.ROUNDED,
    val eyeStyle: EyeStyle = EyeStyle.ROUNDED,
    val centerLogo: CenterLogoType = CenterLogoType.CASS_LOGO,
    val frameText: String = "",
    val errorCorrectionLevel: String = "M", // L, M, Q, H
    val transparentBackground: Boolean = false,
    val margin: Int = 2
) : Serializable

data class QrTemplatePreset(
    val id: String,
    val name: String,
    val category: String, // "Business", "Events", "Social Media", "Retail & Dining"
    val description: String,
    val config: QrDesignConfig,
    val tag: String = "Popular",
    val primaryColorHex: Long = 0xFFD4AF37,
    val secondaryColorHex: Long = 0xFFF3E5AB
) : Serializable

object QrTemplateGallery {
    val CATEGORIES = listOf("All", "Business", "Events", "Social Media", "Retail & Dining")

    val TEMPLATES = listOf(
        // Business Category
        QrTemplatePreset(
            id = "biz_exec_gold",
            name = "Executive Gold",
            category = "Business",
            description = "High-end royal gold gradient with executive rounded eyes and CASS emblem.",
            config = QrDesignConfig(
                fgColorHex = 0xFFD4AF37,
                bgColorHex = 0xFF0A0B0E,
                gradientStyle = GradientStyle.CASS_GOLD,
                dotPattern = DotPattern.ROUNDED,
                eyeStyle = EyeStyle.ROUNDED,
                centerLogo = CenterLogoType.CASS_LOGO,
                frameText = "OFFICIAL CONTACT"
            ),
            tag = "Featured",
            primaryColorHex = 0xFFD4AF37,
            secondaryColorHex = 0xFFF3E5AB
        ),
        QrTemplatePreset(
            id = "biz_corp_platinum",
            name = "Corporate Platinum",
            category = "Business",
            description = "Sleek platinum silver aesthetic with classy modules and double metal ring eyes.",
            config = QrDesignConfig(
                fgColorHex = 0xFFE2E8F0,
                bgColorHex = 0xFF0B0F19,
                gradientStyle = GradientStyle.PLATINUM_SILVER,
                dotPattern = DotPattern.CLASSY,
                eyeStyle = EyeStyle.DOUBLE_FRAME,
                centerLogo = CenterLogoType.CONTACT,
                frameText = "EXECUTIVE VCARD"
            ),
            tag = "Pro",
            primaryColorHex = 0xFFE2E8F0,
            secondaryColorHex = 0xFF94A3B8
        ),
        QrTemplatePreset(
            id = "biz_tech_founder",
            name = "Tech Founder",
            category = "Business",
            description = "Obsidian gold gradient with diamond luxe modules and futuristic bevel eyes.",
            config = QrDesignConfig(
                fgColorHex = 0xFFD4AF37,
                bgColorHex = 0xFF08080A,
                gradientStyle = GradientStyle.OBSIDIAN_GOLD,
                dotPattern = DotPattern.DIAMOND,
                eyeStyle = EyeStyle.FUTURISTIC,
                centerLogo = CenterLogoType.LINK,
                frameText = "CONNECT ON LINKEDIN"
            ),
            tag = "Modern",
            primaryColorHex = 0xFFEAB308,
            secondaryColorHex = 0xFFCA8A04
        ),
        QrTemplatePreset(
            id = "biz_clean_minimal",
            name = "Clean Minimalist",
            category = "Business",
            description = "High-contrast solid platinum on obsidian for flawless professional scanning.",
            config = QrDesignConfig(
                fgColorHex = 0xFFF8FAFC,
                bgColorHex = 0xFF050507,
                gradientStyle = GradientStyle.SOLID,
                dotPattern = DotPattern.ROUNDED,
                eyeStyle = EyeStyle.ROUNDED,
                centerLogo = CenterLogoType.NONE,
                frameText = "SCAN TO CONNECT"
            ),
            tag = "Clean",
            primaryColorHex = 0xFFF8FAFC,
            secondaryColorHex = 0xFF64748B
        ),

        // Events Category
        QrTemplatePreset(
            id = "event_vip_gala",
            name = "VIP Gala Pass",
            category = "Events",
            description = "Warm luxury sunset gradient with diamond modules and VIP Star emblem.",
            config = QrDesignConfig(
                fgColorHex = 0xFFF59E0B,
                bgColorHex = 0xFF120A05,
                gradientStyle = GradientStyle.SUNSET_LUXE,
                dotPattern = DotPattern.DIAMOND,
                eyeStyle = EyeStyle.CIRCLE,
                centerLogo = CenterLogoType.STAR,
                frameText = "VIP ACCESS PASS"
            ),
            tag = "Luxury",
            primaryColorHex = 0xFFF59E0B,
            secondaryColorHex = 0xFFEF4444
        ),
        QrTemplatePreset(
            id = "event_tech_summit",
            name = "Tech Summit",
            category = "Events",
            description = "Cyber cyan neon styling with circular modules and futuristic bevel markers.",
            config = QrDesignConfig(
                fgColorHex = 0xFF06B6D4,
                bgColorHex = 0xFF040D14,
                gradientStyle = GradientStyle.CYBER_CYAN,
                dotPattern = DotPattern.CIRCLE,
                eyeStyle = EyeStyle.FUTURISTIC,
                centerLogo = CenterLogoType.LINK,
                frameText = "SUMMIT BADGE"
            ),
            tag = "Neon",
            primaryColorHex = 0xFF06B6D4,
            secondaryColorHex = 0xFF3B82F6
        ),
        QrTemplatePreset(
            id = "event_luxury_invite",
            name = "Luxury Invite",
            category = "Events",
            description = "Romantic sunset luxury with smooth rounded dots and double frame eyes.",
            config = QrDesignConfig(
                fgColorHex = 0xFFF472B6,
                bgColorHex = 0xFF140810,
                gradientStyle = GradientStyle.SUNSET_LUXE,
                dotPattern = DotPattern.ROUNDED,
                eyeStyle = EyeStyle.DOUBLE_FRAME,
                centerLogo = CenterLogoType.STAR,
                frameText = "SAVE THE DATE"
            ),
            tag = "Special",
            primaryColorHex = 0xFFEC4899,
            secondaryColorHex = 0xFFF43F5E
        ),
        QrTemplatePreset(
            id = "event_art_exhibition",
            name = "Art Exhibition",
            category = "Events",
            description = "Royal gold on deep obsidian with classy modules and concentric circle eyes.",
            config = QrDesignConfig(
                fgColorHex = 0xFFD4AF37,
                bgColorHex = 0xFF080808,
                gradientStyle = GradientStyle.CASS_GOLD,
                dotPattern = DotPattern.CLASSY,
                eyeStyle = EyeStyle.CIRCLE,
                centerLogo = CenterLogoType.STAR,
                frameText = "AUDIO GUIDE"
            ),
            tag = "Artistic",
            primaryColorHex = 0xFFD4AF37,
            secondaryColorHex = 0xFFE2E8F0
        ),

        // Social Media Category
        QrTemplatePreset(
            id = "social_creator_neon",
            name = "Creator Neon",
            category = "Social Media",
            description = "Electric cyber neon gradient with modern dots and concentric circle eyes.",
            config = QrDesignConfig(
                fgColorHex = 0xFF00F2FE,
                bgColorHex = 0xFF050B14,
                gradientStyle = GradientStyle.CYBER_CYAN,
                dotPattern = DotPattern.CIRCLE,
                eyeStyle = EyeStyle.CIRCLE,
                centerLogo = CenterLogoType.LINK,
                frameText = "FOLLOW @PROFILE"
            ),
            tag = "Trending",
            primaryColorHex = 0xFF00F2FE,
            secondaryColorHex = 0xFF4FACFE
        ),
        QrTemplatePreset(
            id = "social_sunset_glow",
            name = "Sunset Glow",
            category = "Social Media",
            description = "Vibrant sunset gradient inspired by social feeds with smooth rounded dots.",
            config = QrDesignConfig(
                fgColorHex = 0xFFFB7185,
                bgColorHex = 0xFF12050B,
                gradientStyle = GradientStyle.SUNSET_LUXE,
                dotPattern = DotPattern.ROUNDED,
                eyeStyle = EyeStyle.DOUBLE_FRAME,
                centerLogo = CenterLogoType.LINK,
                frameText = "WATCH REELS"
            ),
            tag = "Viral",
            primaryColorHex = 0xFFFB7185,
            secondaryColorHex = 0xFFF43F5E
        ),
        QrTemplatePreset(
            id = "social_streamer",
            name = "Live Streamer",
            category = "Social Media",
            description = "High-energy cyber cyan with diamond luxe modules and futuristic eyes.",
            config = QrDesignConfig(
                fgColorHex = 0xFF38BDF8,
                bgColorHex = 0xFF050E17,
                gradientStyle = GradientStyle.CYBER_CYAN,
                dotPattern = DotPattern.DIAMOND,
                eyeStyle = EyeStyle.FUTURISTIC,
                centerLogo = CenterLogoType.STAR,
                frameText = "SUBSCRIBE LIVE"
            ),
            tag = "Gaming",
            primaryColorHex = 0xFF38BDF8,
            secondaryColorHex = 0xFF818CF8
        ),
        QrTemplatePreset(
            id = "social_link_hub",
            name = "Link-in-Bio Hub",
            category = "Social Media",
            description = "Obsidian gold gradient with modern dots and clean executive frame.",
            config = QrDesignConfig(
                fgColorHex = 0xFFEAB308,
                bgColorHex = 0xFF0C0A05,
                gradientStyle = GradientStyle.OBSIDIAN_GOLD,
                dotPattern = DotPattern.CIRCLE,
                eyeStyle = EyeStyle.ROUNDED,
                centerLogo = CenterLogoType.LINK,
                frameText = "TAP FOR LINKS"
            ),
            tag = "Popular",
            primaryColorHex = 0xFFEAB308,
            secondaryColorHex = 0xFFF59E0B
        ),

        // Retail & Dining Category
        QrTemplatePreset(
            id = "retail_luxury_menu",
            name = "Boutique Menu",
            category = "Retail & Dining",
            description = "Royal gold gradient with smooth rounded modules and official emblem.",
            config = QrDesignConfig(
                fgColorHex = 0xFFD4AF37,
                bgColorHex = 0xFF0A0905,
                gradientStyle = GradientStyle.CASS_GOLD,
                dotPattern = DotPattern.ROUNDED,
                eyeStyle = EyeStyle.ROUNDED,
                centerLogo = CenterLogoType.CASS_LOGO,
                frameText = "SCAN FOR MENU"
            ),
            tag = "Dining",
            primaryColorHex = 0xFFD4AF37,
            secondaryColorHex = 0xFFF59E0B
        ),
        QrTemplatePreset(
            id = "retail_wifi_lounge",
            name = "Lounge Wi-Fi",
            category = "Retail & Dining",
            description = "Platinum silver styling with Wi-Fi center logo and friendly scan prompt.",
            config = QrDesignConfig(
                fgColorHex = 0xFF94A3B8,
                bgColorHex = 0xFF0A0E17,
                gradientStyle = GradientStyle.PLATINUM_SILVER,
                dotPattern = DotPattern.CIRCLE,
                eyeStyle = EyeStyle.CIRCLE,
                centerLogo = CenterLogoType.WIFI,
                frameText = "FREE GUEST WI-FI"
            ),
            tag = "Wi-Fi",
            primaryColorHex = 0xFF94A3B8,
            secondaryColorHex = 0xFF38BDF8
        ),
        QrTemplatePreset(
            id = "retail_special_promo",
            name = "Special Promo 20%",
            category = "Retail & Dining",
            description = "Sunset luxury gradient with diamond luxe modules for store displays.",
            config = QrDesignConfig(
                fgColorHex = 0xFFF97316,
                bgColorHex = 0xFF140803,
                gradientStyle = GradientStyle.SUNSET_LUXE,
                dotPattern = DotPattern.DIAMOND,
                eyeStyle = EyeStyle.DOUBLE_FRAME,
                centerLogo = CenterLogoType.STAR,
                frameText = "GET 20% DISCOUNT"
            ),
            tag = "Promo",
            primaryColorHex = 0xFFF97316,
            secondaryColorHex = 0xFFEF4444
        )
    )
}

data class BusinessCardData(
    val fullName: String = "Alexander Vance",
    val jobTitle: String = "Managing Director & Tech Partner",
    val company: String = "CASS Global Innovations",
    val phone: String = "+1 (555) 019-2834",
    val email: String = "alexander.vance@cass-innovations.com",
    val website: String = "https://cass-innovations.com",
    val address: String = "Silicon Valley & Zurich",
    val bio: String = "Innovating next-generation intelligent mobile utility and security technologies.",
    val linkedin: String = "linkedin.com/in/alexandervance",
    val twitter: String = "@alexvance_cass"
)
