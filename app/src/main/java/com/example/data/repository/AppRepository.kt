package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.BusinessCardEntity
import com.example.data.local.entity.CustomQrEntity
import com.example.data.local.entity.ScanRecordEntity
import com.example.data.local.entity.VaultItemEntity
import com.example.domain.model.DotPattern
import com.example.domain.model.EyeStyle
import com.example.domain.model.GradientStyle
import com.example.domain.model.QrContentType
import com.example.domain.model.QrDesignConfig
import com.example.domain.model.QrTemplatePreset
import kotlinx.coroutines.flow.Flow

class AppRepository(private val database: AppDatabase) {

    private val scanDao = database.scanDao()
    private val customQrDao = database.customQrDao()
    private val vaultDao = database.vaultDao()
    private val cardDao = database.businessCardDao()

    // Scan History
    val allScans: Flow<List<ScanRecordEntity>> = scanDao.getAllScans()
    val favoriteScans: Flow<List<ScanRecordEntity>> = scanDao.getFavoriteScans()
    val scanCount: Flow<Int> = scanDao.getScanCount()

    fun getScansByType(contentType: String): Flow<List<ScanRecordEntity>> = scanDao.getScansByType(contentType)
    fun getScansByBarcodeFormat(barcodeFormat: String): Flow<List<ScanRecordEntity>> = scanDao.getScansByBarcodeFormat(barcodeFormat)
    fun getScanById(id: Long): Flow<ScanRecordEntity?> = scanDao.getScanById(id)

    suspend fun saveScan(scan: ScanRecordEntity): Long = scanDao.insertScan(scan)
    suspend fun updateScan(scan: ScanRecordEntity) = scanDao.updateScan(scan)
    suspend fun deleteScan(scan: ScanRecordEntity) = scanDao.deleteScan(scan)
    suspend fun deleteScanById(id: Long) = scanDao.deleteScanById(id)
    suspend fun clearAllScans() = scanDao.clearAllScans()
    fun searchScans(query: String): Flow<List<ScanRecordEntity>> = scanDao.searchScans(query)

    // Custom QRs
    val allCustomQrs: Flow<List<CustomQrEntity>> = customQrDao.getAllCustomQrs()
    val favoriteCustomQrs: Flow<List<CustomQrEntity>> = customQrDao.getFavoriteQrs()

    suspend fun saveCustomQr(qr: CustomQrEntity): Long = customQrDao.insertCustomQr(qr)
    suspend fun updateCustomQr(qr: CustomQrEntity) = customQrDao.updateCustomQr(qr)
    suspend fun deleteCustomQr(qr: CustomQrEntity) = customQrDao.deleteCustomQr(qr)
    suspend fun deleteCustomQrById(id: Long) = customQrDao.deleteCustomQrById(id)

    // Vault
    val allVaultItems: Flow<List<VaultItemEntity>> = vaultDao.getAllVaultItems()
    fun getVaultItemsByCategory(category: String): Flow<List<VaultItemEntity>> = vaultDao.getVaultItemsByCategory(category)
    suspend fun saveVaultItem(item: VaultItemEntity): Long = vaultDao.insertVaultItem(item)
    suspend fun deleteVaultItem(item: VaultItemEntity) = vaultDao.deleteVaultItem(item)

    // Business Card
    val allCards: Flow<List<BusinessCardEntity>> = cardDao.getAllCards()
    val primaryCard: Flow<BusinessCardEntity?> = cardDao.getPrimaryCard()
    suspend fun saveCard(card: BusinessCardEntity): Long = cardDao.insertCard(card)
    suspend fun updateCard(card: BusinessCardEntity) = cardDao.updateCard(card)

    // Curated Templates
    fun getCuratedTemplates(): List<QrTemplatePreset> = listOf(
        QrTemplatePreset(
            id = "cass_executive",
            name = "CASS Executive",
            category = "Business",
            description = "Signature dark luxury with brushed warm gold accents and rounded modules.",
            config = QrDesignConfig(
                fgColorHex = 0xFFD4AF37,
                bgColorHex = 0xFF0A0B0E,
                gradientStyle = GradientStyle.CASS_GOLD,
                dotPattern = DotPattern.ROUNDED,
                eyeStyle = EyeStyle.ROUNDED
            )
        ),
        QrTemplatePreset(
            id = "platinum_minimal",
            name = "Platinum Minimal",
            category = "Minimal",
            description = "High-contrast platinum silver modules with sharp geometric lines.",
            config = QrDesignConfig(
                fgColorHex = 0xFFE2E8F0,
                bgColorHex = 0xFF000000,
                gradientStyle = GradientStyle.PLATINUM_SILVER,
                dotPattern = DotPattern.SQUARE,
                eyeStyle = EyeStyle.SQUARE
            )
        ),
        QrTemplatePreset(
            id = "cyber_future",
            name = "Cyber Future",
            category = "Technology",
            description = "Neon cyan gradients with futuristic bevel eyes.",
            config = QrDesignConfig(
                fgColorHex = 0xFF06B6D4,
                bgColorHex = 0xFF090D16,
                gradientStyle = GradientStyle.CYBER_CYAN,
                dotPattern = DotPattern.CIRCLE,
                eyeStyle = EyeStyle.FUTURISTIC
            )
        ),
        QrTemplatePreset(
            id = "vip_event",
            name = "VIP Champagne",
            category = "Events",
            description = "Rich champagne gold dots with double metal ring eyes.",
            config = QrDesignConfig(
                fgColorHex = 0xFFF3E5AB,
                bgColorHex = 0xFF14120C,
                gradientStyle = GradientStyle.CASS_GOLD,
                dotPattern = DotPattern.DIAMOND,
                eyeStyle = EyeStyle.DOUBLE_FRAME
            )
        ),
        QrTemplatePreset(
            id = "emerald_secure",
            name = "Emerald Safe",
            category = "Wi-Fi & Security",
            description = "Vibrant emerald security tones for instant Wi-Fi connectivity.",
            config = QrDesignConfig(
                fgColorHex = 0xFF10B981,
                bgColorHex = 0xFF07120E,
                gradientStyle = GradientStyle.SOLID,
                dotPattern = DotPattern.ROUNDED,
                eyeStyle = EyeStyle.CIRCLE
            )
        ),
        QrTemplatePreset(
            id = "sunset_creator",
            name = "Social Luxe",
            category = "Social Media",
            description = "Warm sunset luxury gradient designed for social profiles.",
            config = QrDesignConfig(
                fgColorHex = 0xFFF59E0B,
                bgColorHex = 0xFF180A1A,
                gradientStyle = GradientStyle.SUNSET_LUXE,
                dotPattern = DotPattern.CLASSY,
                eyeStyle = EyeStyle.ROUNDED
            )
        ),
        QrTemplatePreset(
            id = "light_luxury",
            name = "Pearl & Gold",
            category = "Luxury Light",
            description = "Clean white pearl canvas with deep metallic gold modules.",
            config = QrDesignConfig(
                fgColorHex = 0xFF997A15,
                bgColorHex = 0xFFFFFFFF,
                gradientStyle = GradientStyle.SOLID,
                dotPattern = DotPattern.ROUNDED,
                eyeStyle = EyeStyle.ROUNDED
            )
        )
    )

    suspend fun seedInitialDataIfEmpty() {
        // We will seed default business card and quick vault samples if none exist
    }
}
