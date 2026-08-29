package com.example.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.entity.BusinessCardEntity
import com.example.data.local.entity.CustomQrEntity
import com.example.data.local.entity.ScanRecordEntity
import com.example.data.local.entity.VaultItemEntity
import com.example.data.repository.AppRepository
import com.example.domain.generator.QrCodeGenerator
import com.example.domain.model.DotPattern
import com.example.domain.model.EyeStyle
import com.example.domain.model.GradientStyle
import com.example.domain.model.ParsedQrContent
import com.example.domain.model.QrContentType
import com.example.domain.model.QrDesignConfig
import com.example.domain.model.QrTemplatePreset
import com.example.domain.parser.QrContentParser
import com.example.domain.security.QrSecurityEngine
import com.example.ui.theme.CassThemeMode
import com.example.util.CassHapticType
import com.example.util.CassHaptics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(AppDatabase.getDatabase(application))

    // Theme Mode & Haptic Settings
    private val _themeMode = MutableStateFlow(CassThemeMode.SIGNATURE_DARK)
    val themeMode: StateFlow<CassThemeMode> = _themeMode.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    fun setHapticsEnabled(enabled: Boolean) {
        _hapticsEnabled.value = enabled
        CassHaptics.setHapticsEnabled(enabled)
        if (enabled) {
            CassHaptics.perform(getApplication(), CassHapticType.BUTTON_CLICK)
        }
    }

    // History and Items
    val scanHistory = repository.allScans.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val favoriteScans = repository.favoriteScans.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val customQrList = repository.allCustomQrs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val vaultItems = repository.allVaultItems.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val primaryBusinessCard = repository.primaryCard.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val templates: List<QrTemplatePreset> = repository.getCuratedTemplates()

    // Active Scanner State
    private val _activeScanResult = MutableStateFlow<ParsedQrContent?>(null)
    val activeScanResult: StateFlow<ParsedQrContent?> = _activeScanResult.asStateFlow()

    private val _isTorchOn = MutableStateFlow(false)
    val isTorchOn: StateFlow<Boolean> = _isTorchOn.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(false)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    private val _isBatchMode = MutableStateFlow(false)
    val isBatchMode: StateFlow<Boolean> = _isBatchMode.asStateFlow()

    private val _isContinuousScan = MutableStateFlow(false)
    val isContinuousScan: StateFlow<Boolean> = _isContinuousScan.asStateFlow()

    private val _batchScanList = MutableStateFlow<List<ParsedQrContent>>(emptyList())
    val batchScanList: StateFlow<List<ParsedQrContent>> = _batchScanList.asStateFlow()

    // Creator Studio State
    private val _qrDesignConfig = MutableStateFlow(QrDesignConfig())
    val qrDesignConfig: StateFlow<QrDesignConfig> = _qrDesignConfig.asStateFlow()

    private val _creatorPayload = MutableStateFlow("https://cass-innovations.com")
    val creatorPayload: StateFlow<String> = _creatorPayload.asStateFlow()

    private val _creatorTitle = MutableStateFlow("CASS Official Website")
    val creatorTitle: StateFlow<String> = _creatorTitle.asStateFlow()

    private val _creatorType = MutableStateFlow(QrContentType.URL)
    val creatorType: StateFlow<QrContentType> = _creatorType.asStateFlow()

    private val _previewBitmap = MutableStateFlow<Bitmap?>(null)
    val previewBitmap: StateFlow<Bitmap?> = _previewBitmap.asStateFlow()

    private val _customLogoBitmap = MutableStateFlow<Bitmap?>(null)
    val customLogoBitmap: StateFlow<Bitmap?> = _customLogoBitmap.asStateFlow()

    // OCR Document Utility State
    private val _ocrDetectedText = MutableStateFlow("")
    val ocrDetectedText: StateFlow<String> = _ocrDetectedText.asStateFlow()

    private val _isOcrScanning = MutableStateFlow(false)
    val isOcrScanning: StateFlow<Boolean> = _isOcrScanning.asStateFlow()

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Active Scan Filter in Library
    private val _selectedScanTypeFilter = MutableStateFlow("ALL")
    val selectedScanTypeFilter: StateFlow<String> = _selectedScanTypeFilter.asStateFlow()

    // Last detection pulse timestamp for scanner overlay animation
    private val _lastDetectionTimestamp = MutableStateFlow(0L)
    val lastDetectionTimestamp: StateFlow<Long> = _lastDetectionTimestamp.asStateFlow()

    init {
        generatePreview()
        seedInitialSamples()
    }

    private fun seedInitialSamples() {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()

            // Seed default scan history records with various scan types and timestamps
            repository.saveScan(
                ScanRecordEntity(
                    id = 1,
                    rawText = "https://cass-innovations.com/quantum-security",
                    title = "CASS Quantum Security Portal",
                    subtitle = "https://cass-innovations.com/quantum-security",
                    contentType = QrContentType.URL.name,
                    barcodeFormat = "QR Code",
                    timestamp = now - 1000L * 60 * 15, // 15 mins ago
                    isFavorite = true,
                    scanCount = 3,
                    securityScore = 98,
                    securityRating = "VERIFIED_SAFE"
                )
            )

            repository.saveScan(
                ScanRecordEntity(
                    id = 2,
                    rawText = "WIFI:T:WPA;S:CASS_VIP_GUEST;P:CassExecutive2026!;H:false;;",
                    title = "CASS_VIP_GUEST",
                    subtitle = "Wi-Fi Network (WPA/WPA2)",
                    contentType = QrContentType.WIFI.name,
                    barcodeFormat = "QR Code",
                    timestamp = now - 1000L * 60 * 90, // 1.5 hours ago
                    isFavorite = true,
                    scanCount = 1,
                    securityScore = 100,
                    securityRating = "SAFE"
                )
            )

            repository.saveScan(
                ScanRecordEntity(
                    id = 3,
                    rawText = "793573189204",
                    title = "EAN-13 Barcode (793573189204)",
                    subtitle = "EAN / UPC / GTIN Standard Barcode",
                    contentType = QrContentType.BARCODE_PRODUCT.name,
                    barcodeFormat = "EAN-13",
                    timestamp = now - 1000L * 3600 * 5, // 5 hours ago
                    isFavorite = false,
                    scanCount = 2,
                    securityScore = 100,
                    securityRating = "SAFE"
                )
            )

            repository.saveScan(
                ScanRecordEntity(
                    id = 4,
                    rawText = "BEGIN:VCARD\nVERSION:3.0\nFN:Elena Rostova\nTITLE:Chief Security Architect\nORG:CASS Tech Labs\nTEL:+1 (555) 349-8812\nEMAIL:elena.r@cass-innovations.com\nURL:https://cass-innovations.com\nEND:VCARD",
                    title = "Elena Rostova",
                    subtitle = "CASS Tech Labs • Chief Security Architect",
                    contentType = QrContentType.CONTACT_VCARD.name,
                    barcodeFormat = "QR Code",
                    timestamp = now - 1000L * 3600 * 22, // Yesterday
                    isFavorite = false,
                    scanCount = 1,
                    securityScore = 100,
                    securityRating = "SAFE"
                )
            )

            repository.saveScan(
                ScanRecordEntity(
                    id = 5,
                    rawText = "bitcoin:bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq?amount=0.045",
                    title = "Bitcoin (BTC)",
                    subtitle = "bc1qar0srrr7xfkv...",
                    contentType = QrContentType.PAYMENT_CRYPTO.name,
                    barcodeFormat = "QR Code",
                    timestamp = now - 1000L * 3600 * 48, // 2 days ago
                    isFavorite = false,
                    scanCount = 1,
                    securityScore = 90,
                    securityRating = "SAFE"
                )
            )

            repository.saveScan(
                ScanRecordEntity(
                    id = 6,
                    rawText = "CASS-LOGISTICS-TRACK-89240019",
                    title = "Code 128 (CASS-LOGISTICS-TRACK-89240019)",
                    subtitle = "Code 128 Standard Barcode",
                    contentType = QrContentType.BARCODE_PRODUCT.name,
                    barcodeFormat = "Code 128",
                    timestamp = now - 1000L * 3600 * 72, // 3 days ago
                    isFavorite = false,
                    scanCount = 1,
                    securityScore = 100,
                    securityRating = "SAFE"
                )
            )

            // Seed a default CASS Executive Business Card if empty
            repository.saveCard(
                BusinessCardEntity(
                    id = 1,
                    fullName = "Alexander Vance",
                    jobTitle = "Managing Director & Tech Lead",
                    company = "CASS Global Innovations",
                    phone = "+1 (415) 890-2341",
                    email = "alexander.vance@cass-innovations.com",
                    website = "https://cass-innovations.com",
                    address = "100 Innovation Way, Silicon Valley, CA",
                    bio = "Pioneering premium mobile intelligence, cybersecurity, and smart utility experiences.",
                    linkedin = "linkedin.com/in/alexandervance",
                    twitter = "@alexvance_cass",
                    isPrimary = true
                )
            )

            // Seed sample vault entries
            repository.saveVaultItem(
                VaultItemEntity(
                    id = 1,
                    title = "Executive Guest Wi-Fi",
                    category = "Wi-Fi Keys",
                    secretPayload = "WIFI:T:WPA;S:CASS_VIP_GUEST;P:CassExecutive2026!;;",
                    notes = "High-speed encrypted fiber network for board meetings."
                )
            )
            repository.saveVaultItem(
                VaultItemEntity(
                    id = 2,
                    title = "Corporate Portal SSO",
                    category = "Frequent Links",
                    secretPayload = "https://sso.cass-innovations.com/auth",
                    notes = "Internal developer & security administration access."
                )
            )
        }
    }

    fun setScanTypeFilter(filter: String) {
        _selectedScanTypeFilter.value = filter
    }

    fun setThemeMode(mode: CassThemeMode) {
        _themeMode.value = mode
    }

    fun toggleTorch() {
        _isTorchOn.value = !_isTorchOn.value
        CassHaptics.perform(getApplication(), CassHapticType.TOGGLE_POP)
    }

    fun toggleCameraFacing() {
        _isFrontCamera.value = !_isFrontCamera.value
        CassHaptics.perform(getApplication(), CassHapticType.BUTTON_CLICK)
    }

    fun toggleBatchMode() {
        _isBatchMode.value = !_isBatchMode.value
        CassHaptics.perform(getApplication(), CassHapticType.TOGGLE_POP)
        if (!_isBatchMode.value) {
            _batchScanList.value = emptyList()
        }
    }

    fun toggleContinuousScan() {
        _isContinuousScan.value = !_isContinuousScan.value
        CassHaptics.perform(getApplication(), CassHapticType.TOGGLE_POP)
    }

    fun onBarcodeDetected(rawText: String, barcodeFormat: String = "QR Code") {
        if (rawText.isBlank()) return
        val parsed = QrContentParser.parse(rawText, barcodeFormat)
        _lastDetectionTimestamp.value = System.currentTimeMillis()

        if (_isBatchMode.value) {
            // Avoid duplicate continuous hits in batch list
            if (_batchScanList.value.none { it.rawText == rawText }) {
                _batchScanList.value = _batchScanList.value + parsed
                saveScanToDb(parsed)
            }
        } else {
            _activeScanResult.value = parsed
            saveScanToDb(parsed)
        }
    }

    fun clearActiveScan() {
        _activeScanResult.value = null
    }

    fun clearBatchList() {
        _batchScanList.value = emptyList()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun saveScanToDb(parsed: ParsedQrContent) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveScan(
                ScanRecordEntity(
                    rawText = parsed.rawText,
                    title = parsed.title,
                    subtitle = parsed.subtitle,
                    contentType = parsed.type.name,
                    barcodeFormat = parsed.barcodeFormat,
                    timestamp = parsed.timestamp,
                    securityScore = parsed.securityResult?.score ?: 100,
                    securityRating = parsed.securityResult?.rating?.name ?: "SAFE"
                )
            )
        }
    }

    fun toggleFavoriteScan(scan: ScanRecordEntity) {
        CassHaptics.perform(getApplication(), CassHapticType.TOGGLE_POP)
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateScan(scan.copy(isFavorite = !scan.isFavorite))
        }
    }

    fun deleteScan(scan: ScanRecordEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteScan(scan)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllScans()
        }
    }

    // QR Studio Updates
    fun updateCreatorData(title: String, payload: String, type: QrContentType) {
        _creatorTitle.value = title
        _creatorPayload.value = payload
        _creatorType.value = type
        generatePreview()
    }

    fun updateDesignConfig(config: QrDesignConfig) {
        _qrDesignConfig.value = config
        generatePreview()
    }

    fun setCustomLogo(bitmap: Bitmap?) {
        _customLogoBitmap.value = bitmap
        if (bitmap != null) {
            _qrDesignConfig.value = _qrDesignConfig.value.copy(
                centerLogo = com.example.domain.model.CenterLogoType.CUSTOM_IMAGE
            )
        } else if (_qrDesignConfig.value.centerLogo == com.example.domain.model.CenterLogoType.CUSTOM_IMAGE) {
            _qrDesignConfig.value = _qrDesignConfig.value.copy(
                centerLogo = com.example.domain.model.CenterLogoType.NONE
            )
        }
        generatePreview()
    }

    fun applyTemplate(preset: QrTemplatePreset) {
        _qrDesignConfig.value = preset.config
        generatePreview()
    }

    fun populateCreatorFromExtractedText(text: String) {
        _creatorTitle.value = "Extracted Text (${text.take(20).trim()}...)"
        _creatorPayload.value = text
        _creatorType.value = QrContentType.PLAIN_TEXT
        generatePreview()
    }

    fun generatePreview() {
        viewModelScope.launch(Dispatchers.Default) {
            val config = _qrDesignConfig.value
            val centerBm = when (config.centerLogo) {
                com.example.domain.model.CenterLogoType.CASS_LOGO -> {
                    try {
                        BitmapFactory.decodeResource(getApplication<Application>().resources, R.drawable.cass_logo)
                    } catch (e: Exception) { null }
                }
                com.example.domain.model.CenterLogoType.CUSTOM_IMAGE -> _customLogoBitmap.value
                else -> null
            }

            val bitmap = QrCodeGenerator.generateQrBitmap(
                content = _creatorPayload.value,
                config = config,
                size = 720,
                centerLogoBitmap = centerBm
            )
            _previewBitmap.value = bitmap
        }
    }

    fun saveCreatedQrToLibrary() {
        viewModelScope.launch(Dispatchers.IO) {
            val config = _qrDesignConfig.value
            repository.saveCustomQr(
                CustomQrEntity(
                    title = _creatorTitle.value,
                    payload = _creatorPayload.value,
                    contentType = _creatorType.value.name,
                    fgColorHex = config.fgColorHex,
                    bgColorHex = config.bgColorHex,
                    gradientStyle = config.gradientStyle.name,
                    dotPattern = config.dotPattern.name,
                    eyeStyle = config.eyeStyle.name,
                    centerLogo = config.centerLogo.name,
                    frameText = config.frameText,
                    transparentBg = config.transparentBackground
                )
            )
            withContext(Dispatchers.Main) {
                CassHaptics.perform(getApplication(), CassHapticType.SUCCESS_CELEBRATE)
                Toast.makeText(getApplication(), "Saved to CASS Library!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteCustomQr(qr: CustomQrEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCustomQr(qr)
        }
    }

    // OCR Document Utility
    fun setOcrText(text: String) {
        _ocrDetectedText.value = text
    }

    fun setOcrScanning(scanning: Boolean) {
        _isOcrScanning.value = scanning
    }

    // Smart Actions
    fun copyToClipboard(text: String, label: String = "CASS Data") {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        CassHaptics.perform(getApplication(), CassHapticType.SUCCESS_CELEBRATE)
        Toast.makeText(getApplication(), "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun shareText(text: String, title: String = "Share via CASS Easy") {
        CassHaptics.perform(getApplication(), CassHapticType.BUTTON_CLICK)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        getApplication<Application>().startActivity(chooser)
    }

    fun openUrl(url: String) {
        try {
            CassHaptics.perform(getApplication(), CassHapticType.BUTTON_CLICK)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "Unable to open link", Toast.LENGTH_SHORT).show()
        }
    }

    fun dialPhone(phone: String) {
        try {
            CassHaptics.perform(getApplication(), CassHapticType.BUTTON_CLICK)
            val clean = if (phone.startsWith("tel:")) phone else "tel:$phone"
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(clean)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "Unable to dial phone", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendEmail(email: String, subject: String = "", body: String = "") {
        try {
            CassHaptics.perform(getApplication(), CassHapticType.BUTTON_CLICK)
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email")
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(getApplication(), "No email client found", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveQrImageToDevice(onSuccess: (File) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val config = _qrDesignConfig.value
                val centerBm = when (config.centerLogo) {
                    com.example.domain.model.CenterLogoType.CASS_LOGO -> {
                        try {
                            BitmapFactory.decodeResource(getApplication<Application>().resources, R.drawable.cass_logo)
                        } catch (e: Exception) { null }
                    }
                    com.example.domain.model.CenterLogoType.CUSTOM_IMAGE -> _customLogoBitmap.value
                    else -> null
                }

                val fullBitmap = QrCodeGenerator.generateQrBitmap(
                    content = _creatorPayload.value,
                    config = config,
                    size = 1440, // High-res export
                    centerLogoBitmap = centerBm
                )

                val file = File(getApplication<Application>().cacheDir, "CASS_QR_${System.currentTimeMillis()}.png")
                val fos = FileOutputStream(file)
                fullBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()

                withContext(Dispatchers.Main) {
                    CassHaptics.perform(getApplication(), CassHapticType.SUCCESS_CELEBRATE)
                    Toast.makeText(getApplication(), "High-Res QR Exported (PNG)", Toast.LENGTH_SHORT).show()
                    onSuccess(file)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun exportSvgFile(onSuccess: (File) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val svgContent = QrCodeGenerator.generateSvg(_creatorPayload.value, _qrDesignConfig.value)
                val file = File(getApplication<Application>().cacheDir, "CASS_QR_${System.currentTimeMillis()}.svg")
                file.writeText(svgContent)

                withContext(Dispatchers.Main) {
                    CassHaptics.perform(getApplication(), CassHapticType.SUCCESS_CELEBRATE)
                    Toast.makeText(getApplication(), "Vector SVG Exported!", Toast.LENGTH_SHORT).show()
                    onSuccess(file)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "SVG export failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun shareExportedFile(file: File, mimeType: String = "image/png") {
        try {
            CassHaptics.perform(getApplication(), CassHapticType.BUTTON_CLICK)
            val uri = FileProvider.getUriForFile(
                getApplication(),
                "${getApplication<Application>().packageName}.provider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(intent, "Share QR Code").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(chooser)
        } catch (e: Exception) {
            // Fallback
            shareText(_creatorPayload.value)
        }
    }

    // Vault & Card Actions
    fun saveVaultItem(title: String, category: String, payload: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveVaultItem(
                VaultItemEntity(
                    title = title,
                    category = category,
                    secretPayload = payload,
                    notes = notes
                )
            )
            withContext(Dispatchers.Main) {
                CassHaptics.perform(getApplication(), CassHapticType.SUCCESS_CELEBRATE)
                Toast.makeText(getApplication(), "Item locked in CASS Vault!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteVaultItem(item: VaultItemEntity) {
        CassHaptics.perform(getApplication(), CassHapticType.TOGGLE_POP)
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteVaultItem(item)
        }
    }

    fun updateBusinessCard(card: BusinessCardEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCard(card)
            withContext(Dispatchers.Main) {
                CassHaptics.perform(getApplication(), CassHapticType.SUCCESS_CELEBRATE)
                Toast.makeText(getApplication(), "Digital Business Card Updated!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
