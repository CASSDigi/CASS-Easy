package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Polymer
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.CenterLogoType
import com.example.domain.model.DotPattern
import com.example.domain.model.EyeStyle
import com.example.domain.model.GradientStyle
import com.example.domain.model.QrContentType
import com.example.domain.model.QrDesignConfig
import com.example.ui.components.CassGlassCard
import com.example.ui.components.CassTopBar
import com.example.ui.components.QuickTemplateCarousel
import com.example.ui.components.TemplateGallerySection
import com.example.ui.theme.CassBorder
import com.example.ui.theme.CassBorderGold
import com.example.ui.theme.CassCharcoal
import com.example.ui.theme.CassEmerald
import com.example.ui.theme.CassGold
import com.example.ui.theme.CassGoldDark
import com.example.ui.theme.CassGoldGradient
import com.example.ui.theme.CassGoldLight
import com.example.ui.theme.CassObsidian
import com.example.ui.theme.CassSilver
import com.example.ui.theme.CassSilverLight
import com.example.ui.theme.CassSilverMuted
import com.example.ui.theme.CassSurface
import com.example.ui.theme.CassSurfaceElevated
import com.example.viewmodel.MainViewModel

@Composable
fun CreateQrScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val config by viewModel.qrDesignConfig.collectAsStateWithLifecycle()
    val previewBitmap by viewModel.previewBitmap.collectAsStateWithLifecycle()
    val customLogoBitmap by viewModel.customLogoBitmap.collectAsStateWithLifecycle()
    val selectedType by viewModel.creatorType.collectAsStateWithLifecycle()
    val title by viewModel.creatorTitle.collectAsStateWithLifecycle()
    val payload by viewModel.creatorPayload.collectAsStateWithLifecycle()

    val customImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    viewModel.setCustomLogo(bitmap)
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Content, 1: Gradients & Colors, 2: Shapes & Eyes, 3: Logo & Frame

    // Form states for structured types
    var urlInput by remember { mutableStateOf("https://cass-innovations.com") }
    var wifiSsid by remember { mutableStateOf("CASS_VIP_LOUNGE") }
    var wifiPass by remember { mutableStateOf("CassSecure2026") }
    var wifiSec by remember { mutableStateOf("WPA") }
    var contactName by remember { mutableStateOf("Alexander Vance") }
    var contactPhone by remember { mutableStateOf("+1 (415) 890-2341") }
    var contactEmail by remember { mutableStateOf("alexander@cass.com") }
    var contactOrg by remember { mutableStateOf("CASS Innovations") }
    var rawTextInput by remember { mutableStateOf("CASS Easy Luxury QR Code") }
    var emailRecipient by remember { mutableStateOf("concierge@cass.com") }
    var emailSubject by remember { mutableStateOf("Executive Inquiry") }
    var phoneInput by remember { mutableStateOf("+14158902341") }

    fun syncPayload() {
        when (selectedType) {
            QrContentType.URL -> viewModel.updateCreatorData("Website Link", urlInput, QrContentType.URL)
            QrContentType.WIFI -> {
                val wifiStr = "WIFI:T:$wifiSec;S:$wifiSsid;P:$wifiPass;;"
                viewModel.updateCreatorData("Wi-Fi ($wifiSsid)", wifiStr, QrContentType.WIFI)
            }
            QrContentType.CONTACT_VCARD -> {
                val vcard = """BEGIN:VCARD
VERSION:3.0
FN:$contactName
TEL:$contactPhone
EMAIL:$contactEmail
ORG:$contactOrg
END:VCARD"""
                viewModel.updateCreatorData(contactName, vcard, QrContentType.CONTACT_VCARD)
            }
            QrContentType.EMAIL -> {
                val mailto = "mailto:$emailRecipient?subject=$emailSubject"
                viewModel.updateCreatorData("Email ($emailRecipient)", mailto, QrContentType.EMAIL)
            }
            QrContentType.PHONE -> {
                val tel = "tel:$phoneInput"
                viewModel.updateCreatorData("Phone ($phoneInput)", tel, QrContentType.PHONE)
            }
            else -> viewModel.updateCreatorData("Custom Text", rawTextInput, QrContentType.PLAIN_TEXT)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CassTopBar(
            title = "Studio Creator",
            subtitle = "Custom Aesthetic QR Generator",
            actions = {
                IconButton(
                    onClick = { viewModel.saveCreatedQrToLibrary() },
                    modifier = Modifier.testTag("studio_save_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = "Save",
                        tint = CassGold
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Live Interactive QR Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0F0F0F))
                        .border(1.dp, CassBorder, RoundedCornerShape(24.dp))
                        .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = CassGold)
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (previewBitmap != null) {
                        Image(
                            bitmap = previewBitmap!!.asImageBitmap(),
                            contentDescription = "Live QR Code",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            // Quick Export Bar (PNG, SVG, Share)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.saveQrImageToDevice { file ->
                            viewModel.shareExportedFile(file, "image/png")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = CassGold)
                        .testTag("export_png_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CassGold)
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null, tint = CassObsidian, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export PNG", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CassObsidian)
                }

                Button(
                    onClick = {
                        viewModel.exportSvgFile { file ->
                            viewModel.shareExportedFile(file, "image/svg+xml")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .border(1.dp, CassBorder, RoundedCornerShape(14.dp))
                        .testTag("export_svg_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF161616))
                ) {
                    Icon(Icons.Filled.Polymer, contentDescription = null, tint = CassGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Vector SVG", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CassSilverLight)
                }
            }

            // Quick 1-Tap Template Switcher
            QuickTemplateCarousel(
                currentConfig = config,
                onApplyTemplate = { preset -> viewModel.applyTemplate(preset) },
                onOpenFullGallery = { selectedTab = 0 },
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Studio Tabs: Templates | Content | Colors | Patterns | Logo
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = CassGold,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CassGold
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("✨ Templates", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("1. Content", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("2. Luxury Colors", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("3. Patterns & Eyes", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    text = { Text("4. Logo & Frame", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab 0: ✨ Template Gallery
            if (selectedTab == 0) {
                TemplateGallerySection(
                    currentConfig = config,
                    onApplyTemplate = { preset -> viewModel.applyTemplate(preset) }
                )
            }

            // Tab 1: Content Type & Fields
            if (selectedTab == 1) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "SELECT DATA TYPE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = CassSilverMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val types = listOf(
                            QrContentType.URL to "Website URL",
                            QrContentType.WIFI to "Wi-Fi Network",
                            QrContentType.CONTACT_VCARD to "vCard Contact",
                            QrContentType.EMAIL to "Email",
                            QrContentType.PHONE to "Phone Call",
                            QrContentType.PLAIN_TEXT to "Plain Text"
                        )
                        items(types) { (type, label) ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = {
                                    viewModel.updateCreatorData(title, payload, type)
                                    syncPayload()
                                },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CassGold,
                                    selectedLabelColor = CassObsidian,
                                    containerColor = CassSurface,
                                    labelColor = CassSilverMuted
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (selectedType) {
                        QrContentType.URL -> {
                            CassTextField(
                                value = urlInput,
                                onValueChange = { urlInput = it; syncPayload() },
                                label = "Website URL",
                                placeholder = "https://example.com"
                            )
                        }
                        QrContentType.WIFI -> {
                            CassTextField(
                                value = wifiSsid,
                                onValueChange = { wifiSsid = it; syncPayload() },
                                label = "Network Name (SSID)",
                                placeholder = "Office_5G"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            CassTextField(
                                value = wifiPass,
                                onValueChange = { wifiPass = it; syncPayload() },
                                label = "Wi-Fi Password",
                                placeholder = "Password"
                            )
                        }
                        QrContentType.CONTACT_VCARD -> {
                            CassTextField(
                                value = contactName,
                                onValueChange = { contactName = it; syncPayload() },
                                label = "Full Name",
                                placeholder = "Jane Doe"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            CassTextField(
                                value = contactPhone,
                                onValueChange = { contactPhone = it; syncPayload() },
                                label = "Phone Number",
                                placeholder = "+1 555-0199"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            CassTextField(
                                value = contactEmail,
                                onValueChange = { contactEmail = it; syncPayload() },
                                label = "Email Address",
                                placeholder = "jane@company.com"
                            )
                        }
                        QrContentType.EMAIL -> {
                            CassTextField(
                                value = emailRecipient,
                                onValueChange = { emailRecipient = it; syncPayload() },
                                label = "Recipient Email",
                                placeholder = "hello@world.com"
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            CassTextField(
                                value = emailSubject,
                                onValueChange = { emailSubject = it; syncPayload() },
                                label = "Default Subject",
                                placeholder = "Hello from QR"
                            )
                        }
                        QrContentType.PHONE -> {
                            CassTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it; syncPayload() },
                                label = "Phone Number",
                                placeholder = "+14155550123"
                            )
                        }
                        else -> {
                            CassTextField(
                                value = rawTextInput,
                                onValueChange = { rawTextInput = it; syncPayload() },
                                label = "Text Content",
                                placeholder = "Enter any text or note..."
                            )
                        }
                    }
                }
            }

            // Tab 2: Luxury Gradients & Background
            if (selectedTab == 2) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "LUXURY METALLIC GRADIENTS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = CassSilverMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val gradients = listOf(
                        GradientStyle.CASS_GOLD to "Signature CASS Gold",
                        GradientStyle.PLATINUM_SILVER to "Platinum Silver",
                        GradientStyle.CYBER_CYAN to "Cyber Neon Cyan",
                        GradientStyle.SUNSET_LUXE to "Sunset Violet Luxe",
                        GradientStyle.OBSIDIAN_GOLD to "Obsidian Deep Gold",
                        GradientStyle.SOLID to "Solid Pure Gold"
                    )

                    gradients.forEach { (grad, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (config.gradientStyle == grad) CassSurfaceElevated else CassSurface)
                                .border(1.dp, if (config.gradientStyle == grad) CassGold else CassBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.updateDesignConfig(config.copy(gradientStyle = grad))
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (config.gradientStyle == grad) CassGoldLight else CassSilverLight
                            )
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (grad) {
                                            GradientStyle.CASS_GOLD -> CassGold
                                            GradientStyle.PLATINUM_SILVER -> CassSilver
                                            GradientStyle.CYBER_CYAN -> Color(0xFF06B6D4)
                                            GradientStyle.SUNSET_LUXE -> Color(0xFFEC4899)
                                            GradientStyle.OBSIDIAN_GOLD -> CassGoldDark
                                            GradientStyle.SOLID -> CassGold
                                        }
                                    )
                            )
                        }
                    }
                }
            }

            // Tab 3: Dot Patterns & Corner Eyes
            if (selectedTab == 3) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "DOT / MODULE SHAPE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = CassSilverMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(DotPattern.values()) { dot ->
                            FilterChip(
                                selected = config.dotPattern == dot,
                                onClick = { viewModel.updateDesignConfig(config.copy(dotPattern = dot)) },
                                label = { Text(dot.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CassGold,
                                    selectedLabelColor = CassObsidian,
                                    containerColor = CassSurface,
                                    labelColor = CassSilverMuted
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "CORNER EYE STYLES (FINDERS)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = CassSilverMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(EyeStyle.values()) { eye ->
                            FilterChip(
                                selected = config.eyeStyle == eye,
                                onClick = { viewModel.updateDesignConfig(config.copy(eyeStyle = eye)) },
                                label = { Text(eye.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CassGold,
                                    selectedLabelColor = CassObsidian,
                                    containerColor = CassSurface,
                                    labelColor = CassSilverMuted
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Tab 4: Center Logo & CTA Frame
            if (selectedTab == 4) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = "CENTER EMBLEM / CUSTOM LOGO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = CassSilverMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(CenterLogoType.values()) { logoType ->
                            FilterChip(
                                selected = config.centerLogo == logoType,
                                onClick = {
                                    if (logoType == CenterLogoType.CUSTOM_IMAGE) {
                                        if (customLogoBitmap == null) {
                                            customImagePicker.launch("image/*")
                                        } else {
                                            viewModel.updateDesignConfig(config.copy(centerLogo = logoType))
                                        }
                                    } else {
                                        viewModel.updateDesignConfig(config.copy(centerLogo = logoType))
                                    }
                                },
                                label = { Text(logoType.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CassGold,
                                    selectedLabelColor = CassObsidian,
                                    containerColor = CassSurface,
                                    labelColor = CassSilverMuted
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Custom Gallery Photo Integration Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CassSurface)
                            .border(1.dp, if (config.centerLogo == CenterLogoType.CUSTOM_IMAGE && customLogoBitmap != null) CassBorderGold else CassBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = CassGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Custom Gallery Photo",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CassSilverLight
                                    )
                                }

                                if (customLogoBitmap != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(CassEmerald.copy(alpha = 0.2f))
                                            .border(1.dp, CassEmerald.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "Active in QR",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = CassEmerald,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Embed your personal photo, company badge, or custom icon directly in the center of the QR code matrix.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CassSilverMuted,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            if (customLogoBitmap != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(CassObsidian)
                                            .border(1.5.dp, CassBorderGold, RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            bitmap = customLogoBitmap!!.asImageBitmap(),
                                            contentDescription = "Selected Logo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { customImagePicker.launch("image/*") },
                                                modifier = Modifier.height(36.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = CassGold)
                                            ) {
                                                Icon(Icons.Filled.Refresh, contentDescription = null, tint = CassObsidian, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Change", color = CassObsidian, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            }

                                            OutlinedButton(
                                                onClick = { viewModel.setCustomLogo(null) },
                                                modifier = Modifier.height(36.dp),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Icon(Icons.Filled.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Remove", color = Color(0xFFEF4444), fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            } else {
                                Button(
                                    onClick = { customImagePicker.launch("image/*") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp)
                                        .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = CassGold)
                                        .testTag("pick_custom_qr_logo_btn"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CassGold)
                                ) {
                                    Icon(Icons.Filled.Image, contentDescription = null, tint = CassObsidian, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pick Photo from Gallery", fontWeight = FontWeight.Bold, color = CassObsidian, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "CTA CALLOUT FRAME BADGE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = CassSilverMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CassTextField(
                        value = config.frameText,
                        onValueChange = { viewModel.updateDesignConfig(config.copy(frameText = it)) },
                        label = "Bottom Frame Text (Leave empty for none)",
                        placeholder = "e.g. SCAN ME, CONNECT WI-FI, CASS VIP"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val quickFrames = listOf("", "SCAN ME", "VISIT WEBSITE", "CONNECT WI-FI", "CASS VIP", "SCAN FOR MENU")
                        items(quickFrames) { frame ->
                            FilterChip(
                                selected = config.frameText == frame,
                                onClick = { viewModel.updateDesignConfig(config.copy(frameText = frame)) },
                                label = { Text(if (frame.isEmpty()) "None" else frame, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CassGold,
                                    selectedLabelColor = CassObsidian,
                                    containerColor = CassSurface,
                                    labelColor = CassSilverMuted
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = CassSilverMuted) },
        placeholder = { Text(placeholder, color = CassSilverMuted.copy(alpha = 0.6f)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CassGold,
            unfocusedBorderColor = CassBorder,
            focusedTextColor = CassSilverLight,
            unfocusedTextColor = CassSilverLight,
            focusedContainerColor = CassSurface,
            unfocusedContainerColor = CassSurface
        ),
        singleLine = true
    )
}
