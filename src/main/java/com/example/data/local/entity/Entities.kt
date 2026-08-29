package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.QrContentType

@Entity(tableName = "scan_history")
data class ScanRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rawText: String,
    val title: String,
    val subtitle: String,
    val contentType: String = QrContentType.URL.name,
    val barcodeFormat: String = "QR_CODE",
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val scanCount: Int = 1,
    val securityScore: Int = 100,
    val securityRating: String = "SAFE",
    val categoryFolder: String = "General",
    val notes: String = ""
)

@Entity(tableName = "custom_qr_codes")
data class CustomQrEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val payload: String,
    val contentType: String = QrContentType.URL.name,
    val templateName: String = "Custom",
    val fgColorHex: Long = 0xFFD4AF37,
    val bgColorHex: Long = 0xFF0A0B0E,
    val gradientStyle: String = "CASS_GOLD",
    val dotPattern: String = "ROUNDED",
    val eyeStyle: String = "ROUNDED",
    val centerLogo: String = "CASS_LOGO",
    val frameText: String = "",
    val transparentBg: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(tableName = "vault_items")
data class VaultItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Wi-Fi Keys", "Digital Cards", "Frequent Links", "Security Credentials", "VIP Passes"
    val secretPayload: String,
    val isEncrypted: Boolean = true,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val iconName: String = "lock"
)

@Entity(tableName = "business_cards")
data class BusinessCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val jobTitle: String,
    val company: String,
    val phone: String,
    val email: String,
    val website: String,
    val address: String = "",
    val bio: String = "",
    val linkedin: String = "",
    val twitter: String = "",
    val themeStyle: String = "CASS_GOLD_LUXE",
    val isPrimary: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
