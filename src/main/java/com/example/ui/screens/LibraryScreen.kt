package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.CustomQrEntity
import com.example.data.local.entity.ScanRecordEntity
import com.example.data.local.entity.VaultItemEntity
import com.example.domain.model.QrContentType
import com.example.domain.model.QrDesignConfig
import com.example.ui.components.CassGlassCard
import com.example.ui.components.CassTopBar
import com.example.ui.theme.CassBorder
import com.example.ui.theme.CassBorderGold
import com.example.ui.theme.CassCharcoal
import com.example.ui.theme.CassCrimson
import com.example.ui.theme.CassEmerald
import com.example.ui.theme.CassGold
import com.example.ui.theme.CassGoldDark
import com.example.ui.theme.CassGoldLight
import com.example.ui.theme.CassObsidian
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
fun LibraryScreen(
    viewModel: MainViewModel,
    onOpenScanDetail: (ScanRecordEntity) -> Unit
) {
    val scanHistory by viewModel.scanHistory.collectAsStateWithLifecycle()
    val favoriteScans by viewModel.favoriteScans.collectAsStateWithLifecycle()
    val customQrs by viewModel.customQrList.collectAsStateWithLifecycle()
    val vaultItems by viewModel.vaultItems.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: History, 1: Created Studio, 2: Smart Vault, 3: Favorites
    var searchQuery by remember { mutableStateOf("") }
    var showAddVaultDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CassTopBar(
            title = "CASS Vault & Library",
            subtitle = "Encrypted On-Device Database",
            actions = {
                if (selectedTab == 0 && scanHistory.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearAllHistory() },
                        modifier = Modifier.testTag("clear_history_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ClearAll,
                            contentDescription = "Clear All",
                            tint = CassCrimson
                        )
                    }
                } else if (selectedTab == 2) {
                    IconButton(
                        onClick = { showAddVaultDialog = true },
                        modifier = Modifier.testTag("add_vault_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Secret",
                            tint = CassGold
                        )
                    }
                }
            }
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search logs, secrets, titles...", color = CassSilverMuted) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = CassGold) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(14.dp),
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

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = CassGold,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CassGold
                    )
                }
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("History (${scanHistory.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Studio (${customQrs.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Vault (${vaultItems.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Saved (${favoriteScans.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        // Content
        when (selectedTab) {
            0 -> {
                val selectedTypeFilter by viewModel.selectedScanTypeFilter.collectAsStateWithLifecycle()

                val filterOptions = listOf(
                    "ALL" to "All Types",
                    "QR_CODE" to "QR Codes",
                    "BARCODE" to "Barcodes",
                    "URL" to "Websites",
                    "WIFI" to "Wi-Fi",
                    "CONTACT" to "Contacts",
                    "CRYPTO" to "Crypto & Pay"
                )

                Column(modifier = Modifier.fillMaxSize()) {
                    // Scan Type Filter Chips Row
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filterOptions) { (key, label) ->
                            val isSelected = selectedTypeFilter == key
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setScanTypeFilter(key) },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CassGold,
                                    selectedLabelColor = CassObsidian,
                                    containerColor = CassSurface,
                                    labelColor = CassSilverMuted
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) CassGold else CassBorder,
                                    selectedBorderColor = CassGold
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }

                    val filtered = scanHistory.filter { scan ->
                        val matchesSearch = scan.title.contains(searchQuery, ignoreCase = true) ||
                                scan.rawText.contains(searchQuery, ignoreCase = true) ||
                                scan.contentType.contains(searchQuery, ignoreCase = true) ||
                                scan.barcodeFormat.contains(searchQuery, ignoreCase = true)

                        val matchesType = when (selectedTypeFilter) {
                            "ALL" -> true
                            "QR_CODE" -> scan.barcodeFormat.contains("QR", ignoreCase = true)
                            "BARCODE" -> scan.contentType == QrContentType.BARCODE_PRODUCT.name || !scan.barcodeFormat.contains("QR", ignoreCase = true)
                            "URL" -> scan.contentType == QrContentType.URL.name
                            "WIFI" -> scan.contentType == QrContentType.WIFI.name
                            "CONTACT" -> scan.contentType == QrContentType.CONTACT_VCARD.name
                            "CRYPTO" -> scan.contentType == QrContentType.PAYMENT_CRYPTO.name
                            else -> true
                        }

                        matchesSearch && matchesType
                    }

                    if (filtered.isEmpty()) {
                        EmptyLibraryPlaceholder(text = "No scan logs found for the selected filter")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filtered, key = { it.id }) { scan ->
                                ScanHistoryItemCard(
                                    scan = scan,
                                    onClick = { onOpenScanDetail(scan) },
                                    onToggleFavorite = { viewModel.toggleFavoriteScan(scan) },
                                    onDelete = { viewModel.deleteScan(scan) },
                                    onCopy = { viewModel.copyToClipboard(scan.rawText) }
                                )
                            }
                        }
                    }
                }
            }
            1 -> {
                val filtered = customQrs.filter {
                    it.title.contains(searchQuery, ignoreCase = true) || it.payload.contains(searchQuery, ignoreCase = true)
                }
                if (filtered.isEmpty()) {
                    EmptyLibraryPlaceholder(text = "No custom QRs created yet. Head to Studio!")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtered, key = { it.id }) { qr ->
                            CustomQrItemCard(
                                qr = qr,
                                onDelete = { viewModel.deleteCustomQr(qr) },
                                onCopy = { viewModel.copyToClipboard(qr.payload) },
                                onShare = { viewModel.shareText(qr.payload) }
                            )
                        }
                    }
                }
            }
            2 -> {
                val filtered = vaultItems.filter {
                    it.title.contains(searchQuery, ignoreCase = true) || it.category.contains(searchQuery, ignoreCase = true)
                }
                if (filtered.isEmpty()) {
                    EmptyLibraryPlaceholder(text = "Vault is empty. Add encrypted Wi-Fi keys, passcodes, and credentials.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtered, key = { it.id }) { item ->
                            VaultItemCard(
                                item = item,
                                onDelete = { viewModel.deleteVaultItem(item) },
                                onCopy = { viewModel.copyToClipboard(item.secretPayload) }
                            )
                        }
                    }
                }
            }
            3 -> {
                val filtered = favoriteScans.filter {
                    it.title.contains(searchQuery, ignoreCase = true) || it.rawText.contains(searchQuery, ignoreCase = true)
                }
                if (filtered.isEmpty()) {
                    EmptyLibraryPlaceholder(text = "No starred items yet")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filtered, key = { it.id }) { scan ->
                            ScanHistoryItemCard(
                                scan = scan,
                                onClick = { onOpenScanDetail(scan) },
                                onToggleFavorite = { viewModel.toggleFavoriteScan(scan) },
                                onDelete = { viewModel.deleteScan(scan) },
                                onCopy = { viewModel.copyToClipboard(scan.rawText) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddVaultDialog) {
        AddVaultDialog(
            onDismiss = { showAddVaultDialog = false },
            onSave = { title, category, payload, notes ->
                viewModel.saveVaultItem(title, category, payload, notes)
                showAddVaultDialog = false
            }
        )
    }
}

@Composable
fun ScanHistoryItemCard(
    scan: ScanRecordEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    val relativeTime = formatRelativeTimestamp(scan.timestamp)

    val (typeIcon, typeColor, typeBadgeLabel) = when (scan.contentType) {
        QrContentType.URL.name -> Triple(Icons.Filled.Language, Color(0xFF38BDF8), "WEBSITE")
        QrContentType.WIFI.name -> Triple(Icons.Filled.Wifi, CassEmerald, "WI-FI")
        QrContentType.BARCODE_PRODUCT.name -> Triple(Icons.Filled.QrCodeScanner, Color(0xFFFB923C), "PRODUCT")
        QrContentType.CONTACT_VCARD.name -> Triple(Icons.Filled.Person, Color(0xFFA78BFA), "CONTACT")
        QrContentType.PAYMENT_CRYPTO.name -> Triple(Icons.Filled.AccountBalanceWallet, CassGold, "CRYPTO PAY")
        QrContentType.EMAIL.name -> Triple(Icons.Filled.Email, Color(0xFF34D399), "EMAIL")
        QrContentType.PHONE.name -> Triple(Icons.Filled.Call, Color(0xFF60A5FA), "PHONE")
        QrContentType.SMS.name -> Triple(Icons.Filled.Sms, Color(0xFF818CF8), "SMS")
        QrContentType.GEO_LOCATION.name -> Triple(Icons.Filled.LocationOn, Color(0xFFF43F5E), "LOCATION")
        QrContentType.CALENDAR_EVENT.name -> Triple(Icons.Filled.Event, Color(0xFFFBBF24), "EVENT")
        else -> Triple(Icons.Filled.QrCode, CassGold, scan.contentType.replace("_", " "))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CassSurface)
            .border(1.dp, if (scan.isFavorite) CassGold.copy(alpha = 0.6f) else CassBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                            .background(CassCharcoal)
                            .border(1.dp, typeColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = typeIcon,
                            contentDescription = null,
                            tint = typeColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = scan.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CassSilverLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = scan.subtitle.ifBlank { scan.rawText },
                            style = MaterialTheme.typography.bodySmall,
                            color = CassSilverMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy Content",
                            tint = CassSilverMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onToggleFavorite, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (scan.isFavorite) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (scan.isFavorite) CassGold else CassSilverMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = CassCrimson.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Type Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(typeColor.copy(alpha = 0.12f))
                        .border(1.dp, typeColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = typeBadgeLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                }

                // Barcode Format Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CassCharcoal)
                        .border(1.dp, CassBorder, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = scan.barcodeFormat,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = CassSilver
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Timestamp
                Text(
                    text = relativeTime,
                    fontSize = 11.sp,
                    color = CassSilverMuted
                )
            }
        }
    }
}

private fun formatRelativeTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        diff < 60_000 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days == 1L -> "Yesterday"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}

@Composable
fun CustomQrItemCard(
    qr: CustomQrEntity,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CassSurface)
            .border(1.dp, CassBorderGold, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = qr.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CassSilverLight
                    )
                    Text(
                        text = "${qr.contentType} • ${qr.gradientStyle.replace("_", " ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CassGold
                    )
                }

                Row {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = CassSilverMuted, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Share, contentDescription = "Share", tint = CassSilverMuted, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = CassCrimson, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = qr.payload,
                style = MaterialTheme.typography.bodySmall,
                color = CassSilverMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun VaultItemCard(
    item: VaultItemEntity,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CassSurface)
            .border(1.dp, CassBorderGold, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CassEmerald.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = CassEmerald, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = CassSilverLight
                        )
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = CassEmerald
                        )
                    }
                }

                Row {
                    IconButton(onClick = { revealed = !revealed }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (revealed) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle",
                            tint = CassSilverMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = CassSilverMuted, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = CassCrimson, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (revealed) item.secretPayload else "••••••••••••••••••••••••••••••••",
                style = MaterialTheme.typography.bodySmall,
                color = if (revealed) CassGoldLight else CassSilverMuted,
                fontWeight = FontWeight.Medium
            )
            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = CassSilverMuted.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun AddVaultDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, payload: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Wi-Fi Keys") }
    var payload by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CassCharcoal,
        title = {
            Text(
                text = "Lock Item in Smart Vault",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = CassGoldLight
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CassTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Title / Label",
                    placeholder = "e.g. Fiber Wi-Fi Key"
                )

                CassTextField(
                    value = payload,
                    onValueChange = { payload = it },
                    label = "Secret Payload / Passcode",
                    placeholder = "Password, token, or secret URI"
                )

                CassTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Security Note (Optional)",
                    placeholder = "Additional instructions"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && payload.isNotBlank()) {
                        onSave(title, category, payload, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CassGold),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Lock in Vault", fontWeight = FontWeight.Bold, color = CassObsidian)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CassSurfaceElevated),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel", color = CassSilverMuted)
            }
        }
    )
}

@Composable
fun EmptyLibraryPlaceholder(text: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Folder,
            contentDescription = null,
            tint = CassSilverMuted,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = CassSilverMuted
        )
    }
}
