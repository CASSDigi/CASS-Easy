package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.domain.model.ParsedQrContent
import com.example.domain.model.QrContentType
import com.example.domain.model.SecurityRating
import com.example.ui.components.SecurityBadge
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultBottomSheet(
    parsed: ParsedQrContent,
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPassword by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CassCharcoal,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(CassGoldDark)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header: Icon + Content Type + Security Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CassSurfaceElevated)
                            .border(1.dp, CassBorderGold, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (parsed.type) {
                                QrContentType.URL -> Icons.Filled.OpenInBrowser
                                QrContentType.WIFI -> Icons.Filled.Wifi
                                QrContentType.CONTACT_VCARD -> Icons.Filled.PersonAdd
                                QrContentType.PHONE -> Icons.Filled.Call
                                QrContentType.EMAIL -> Icons.Filled.Email
                                QrContentType.GEO_LOCATION -> Icons.Filled.Map
                                else -> Icons.Filled.QrCode
                            },
                            contentDescription = null,
                            tint = CassGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = parsed.type.displayName.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = CassGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CassObsidian)
                                    .border(1.dp, CassBorder, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = parsed.barcodeFormat,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = CassSilver
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = parsed.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = CassSilverLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                parsed.securityResult?.let { sec ->
                    SecurityBadge(rating = sec.rating, score = sec.score)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Timestamp and Format Pill
            val scanTimeStr = java.text.SimpleDateFormat("MMM dd, yyyy • hh:mm:ss a", java.util.Locale.getDefault()).format(java.util.Date(parsed.timestamp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Scanned: $scanTimeStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = CassSilverMuted
                )
                Text(
                    text = "Saved in CASS Room DB",
                    style = MaterialTheme.typography.labelSmall,
                    color = CassEmerald
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Raw Text Preview Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CassSurface)
                    .border(1.dp, CassBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = parsed.rawText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CassSilverLight,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Parsed Details Breakdown
            if (parsed.details.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CassSurface)
                        .border(1.dp, CassBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "EXTRACTED ATTRIBUTES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = CassSilverMuted
                        )

                        parsed.details.forEach { (key, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CassSilverMuted
                                )

                                if (key.equals("Password", ignoreCase = true)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (showPassword) value else "••••••••••••",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CassGoldLight
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = { showPassword = !showPassword },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                                contentDescription = "Toggle",
                                                tint = CassSilverMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = value,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CassSilverLight,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // QR Security Center Report
            parsed.securityResult?.let { sec ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (sec.score < 60) Color(0x25EF4444) else CassSurface)
                        .border(1.dp, if (sec.score < 60) CassCrimson else CassBorderGold, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Security,
                                    contentDescription = null,
                                    tint = if (sec.score < 60) CassCrimson else CassGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CASS SECURITY AUDIT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = if (sec.score < 60) CassCrimson else CassGold
                                )
                            }
                            Text(
                                text = "Trust Score: ${sec.score}/100",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CassSilverLight
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = sec.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = CassSilverLight
                        )

                        if (sec.riskFactors.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            sec.riskFactors.forEach { factor ->
                                Text(
                                    text = "• $factor",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (sec.score < 60) CassCrimson else Color(0xFFF59E0B)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Primary Smart Action Button
            when (parsed.type) {
                QrContentType.URL -> {
                    Button(
                        onClick = {
                            parsed.actionUrl?.let { viewModel.openUrl(it) }
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("action_open_url"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CassGold)
                    ) {
                        Icon(Icons.Filled.OpenInBrowser, contentDescription = null, tint = CassObsidian)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Secure Website", fontWeight = FontWeight.Bold, color = CassObsidian)
                    }
                }
                QrContentType.WIFI -> {
                    Button(
                        onClick = {
                            val pass = parsed.details["Password"] ?: ""
                            viewModel.copyToClipboard(pass, "Wi-Fi Password")
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("action_copy_wifi"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CassGold)
                    ) {
                        Icon(Icons.Filled.Wifi, contentDescription = null, tint = CassObsidian)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Wi-Fi Password", fontWeight = FontWeight.Bold, color = CassObsidian)
                    }
                }
                QrContentType.PHONE -> {
                    Button(
                        onClick = {
                            parsed.actionUrl?.let { viewModel.dialPhone(it) }
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("action_call_phone"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CassGold)
                    ) {
                        Icon(Icons.Filled.Call, contentDescription = null, tint = CassObsidian)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Call Phone Number", fontWeight = FontWeight.Bold, color = CassObsidian)
                    }
                }
                QrContentType.EMAIL -> {
                    Button(
                        onClick = {
                            val email = parsed.details["Email"] ?: parsed.title
                            viewModel.sendEmail(email)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("action_send_email"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CassGold)
                    ) {
                        Icon(Icons.Filled.Email, contentDescription = null, tint = CassObsidian)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Email", fontWeight = FontWeight.Bold, color = CassObsidian)
                    }
                }
                else -> {
                    Button(
                        onClick = {
                            viewModel.copyToClipboard(parsed.rawText)
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("action_copy_data"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CassGold)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = CassObsidian)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Copy Raw Content", fontWeight = FontWeight.Bold, color = CassObsidian)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Secondary Quick Actions Row (Copy, Share, Save to Vault)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.copyToClipboard(parsed.rawText)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CassSurfaceElevated)
                ) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = CassSilverLight, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy", color = CassSilverLight)
                }

                Button(
                    onClick = {
                        viewModel.shareText(parsed.rawText)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CassSurfaceElevated)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, tint = CassSilverLight, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", color = CassSilverLight)
                }

                Button(
                    onClick = {
                        viewModel.saveVaultItem(
                            title = parsed.title,
                            category = when (parsed.type) {
                                QrContentType.WIFI -> "Wi-Fi Keys"
                                QrContentType.CONTACT_VCARD -> "Digital Cards"
                                else -> "Important QR"
                            },
                            payload = parsed.rawText,
                            notes = "Scanned on CASS Easy (${parsed.type.displayName})"
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CassSurfaceElevated)
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = CassEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Vault", color = CassEmerald)
                }
            }
        }
    }
}
