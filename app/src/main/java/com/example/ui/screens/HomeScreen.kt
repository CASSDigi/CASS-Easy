package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.entity.ScanRecordEntity
import com.example.domain.model.QrTemplatePreset
import com.example.domain.model.SecurityRating
import com.example.ui.components.CassGlassCard
import com.example.ui.components.CassNavDestination
import com.example.ui.components.CassTopBar
import com.example.ui.components.SecurityBadge
import com.example.ui.theme.CassAmber
import com.example.ui.theme.CassBorder
import com.example.ui.theme.CassBorderGold
import com.example.ui.theme.CassCharcoal
import com.example.ui.theme.CassEmerald
import com.example.ui.theme.CassGold
import com.example.ui.theme.CassGoldDark
import com.example.ui.theme.CassGoldGradient
import com.example.ui.theme.CassGoldLight
import com.example.ui.theme.CassObsidian
import com.example.ui.theme.CassBorderSubtle
import com.example.ui.theme.CassGoldDeep
import com.example.ui.theme.CassGoldGlow
import com.example.ui.theme.CassSilver
import com.example.ui.theme.CassSilverLight
import com.example.ui.theme.CassSilverMuted
import com.example.ui.theme.CassSurface
import com.example.ui.theme.CassSurfaceElevated
import com.example.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigate: (CassNavDestination) -> Unit,
    onOpenOcr: () -> Unit,
    onOpenBusinessCard: () -> Unit,
    onOpenSecurityCenter: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenScanDetail: (ScanRecordEntity) -> Unit
) {
    val scanHistory by viewModel.scanHistory.collectAsStateWithLifecycle()
    val vaultItems by viewModel.vaultItems.collectAsStateWithLifecycle()
    val templates = viewModel.templates

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CassTopBar(
            title = "CASS EASY",
            subtitle = "Intelligent QR & Utility Engine",
            showBrandLogo = true,
            actions = {
                IconButton(
                    onClick = onOpenSecurityCenter,
                    modifier = Modifier.testTag("home_security_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = "Security Center",
                        tint = CassEmerald
                    )
                }
                IconButton(
                    onClick = onOpenAnalytics,
                    modifier = Modifier.testTag("home_analytics_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Analytics",
                        tint = CassGold
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 110.dp)
        ) {
            // 1. Luxury Hero Banner Card (Professional Polish rounded-3xl)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF161616),
                                    Color(0xFF0C0C0C)
                                )
                            )
                        )
                        .border(1.dp, CassBorder, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(CassGold)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "EXECUTIVE SUITE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.sp,
                                        color = CassGold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Intelligent QR Command",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CassSilverLight
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x22D4AF37))
                                    .border(1.dp, CassBorderGold, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "${scanHistory.size} Vaulted",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = CassGoldLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Dual Primary Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { onNavigate(CassNavDestination.SCAN) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = CassGold)
                                    .testTag("hero_quick_scan_btn"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CassGold
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QrCodeScanner,
                                    contentDescription = null,
                                    tint = CassObsidian,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Quick Scan",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = CassObsidian
                                )
                            }

                            Button(
                                onClick = { onNavigate(CassNavDestination.CREATE) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .border(1.dp, CassBorder, RoundedCornerShape(16.dp))
                                    .testTag("hero_create_qr_btn"),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A1A))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = CassGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Create QR",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = CassSilverLight
                                )
                            }
                        }
                    }
                }
            }

            // 2. Smart Everyday Utilities Grid
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = "SMART UTILITIES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = CassSilverMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SmartShortcutCard(
                            title = "Digital Card",
                            subtitle = "NFC & vCard Suite",
                            icon = Icons.Filled.Badge,
                            accentColor = CassGold,
                            modifier = Modifier.weight(1f),
                            onClick = onOpenBusinessCard
                        )

                        SmartShortcutCard(
                            title = "Extract Text",
                            subtitle = "ML Kit OCR Studio",
                            icon = Icons.Filled.DocumentScanner,
                            accentColor = Color(0xFF06B6D4),
                            modifier = Modifier.weight(1f),
                            onClick = onOpenOcr
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SmartShortcutCard(
                            title = "Smart Vault",
                            subtitle = "${vaultItems.size} Stored Keys",
                            icon = Icons.Filled.Lock,
                            accentColor = CassEmerald,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigate(CassNavDestination.LIBRARY) }
                        )

                        SmartShortcutCard(
                            title = "Security Hub",
                            subtitle = "Real-Time URL Check",
                            icon = Icons.Filled.Security,
                            accentColor = CassAmber,
                            modifier = Modifier.weight(1f),
                            onClick = onOpenSecurityCenter
                        )
                    }
                }
            }

            // 3. Curated Templates Gallery
            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PRESET TEMPLATES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = CassSilverMuted
                        )
                        Text(
                            text = "Studio →",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CassGold,
                            modifier = Modifier.clickable { onNavigate(CassNavDestination.CREATE) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(templates) { preset ->
                            TemplatePreviewChip(
                                preset = preset,
                                onClick = {
                                    viewModel.applyTemplate(preset)
                                    onNavigate(CassNavDestination.CREATE)
                                }
                            )
                        }
                    }
                }
            }

            // 4. Recent Activity / Scan History
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT ACTIVITY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = CassSilverMuted
                        )
                        if (scanHistory.isNotEmpty()) {
                            Text(
                                text = "View All (${scanHistory.size})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CassGold,
                                modifier = Modifier.clickable { onNavigate(CassNavDestination.LIBRARY) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (scanHistory.isEmpty()) {
                        CassGlassCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QrCodeScanner,
                                    contentDescription = null,
                                    tint = CassSilverMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "No Scans Recorded Yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CassSilverLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Point camera at any QR or Barcode for instant detection.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CassSilverMuted
                                )
                            }
                        }
                    } else {
                        scanHistory.take(5).forEach { scan ->
                            RecentScanCard(
                                scan = scan,
                                onClick = { onOpenScanDetail(scan) },
                                onCopy = { viewModel.copyToClipboard(scan.rawText) },
                                onShare = { viewModel.shareText(scan.rawText) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SmartShortcutCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF121212))
            .border(1.dp, CassBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = CassSilverLight
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = CassSilverMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TemplatePreviewChip(
    preset: QrTemplatePreset,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF121212))
            .border(1.dp, CassBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = preset.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = CassGold
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(preset.config.fgColorHex))
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = preset.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = CassSilverLight,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = preset.description,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = CassSilverMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RecentScanCard(
    scan: ScanRecordEntity,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(scan.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF141414),
                        Color(0xFF0F0F0F)
                    )
                )
            )
            .border(1.dp, CassBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1A1A))
                        .border(1.dp, CassBorderGold, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (scan.contentType) {
                            "WIFI" -> Icons.Filled.Wifi
                            "CONTACT_VCARD" -> Icons.Filled.Badge
                            "DOCUMENT_NOTE" -> Icons.Filled.DocumentScanner
                            else -> Icons.Filled.QrCode
                        },
                        contentDescription = null,
                        tint = CassGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = scan.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = CassSilverLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${scan.contentType.replace("_", " ")} • $dateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = CassSilverMuted
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCopy, modifier = Modifier.size(34.dp)) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_edit),
                        contentDescription = "Copy",
                        tint = CassSilverMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onShare, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = CassSilverMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
