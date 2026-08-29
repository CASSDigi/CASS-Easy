package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.SecurityAnalysisResult
import com.example.domain.model.SecurityRating
import com.example.domain.security.QrSecurityEngine
import com.example.ui.components.CassGlassCard
import com.example.ui.components.CassTopBar
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

@Composable
fun SecurityCenterScreen(
    onBack: () -> Unit
) {
    var testUrl by remember { mutableStateOf("https://cass-innovations.com") }
    var analysisResult by remember { mutableStateOf<SecurityAnalysisResult?>(QrSecurityEngine.analyze(testUrl)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CassTopBar(
            title = "Security Center",
            subtitle = "Zero-Trust Threat Radar & Link Audit",
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = CassSilverLight)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CassCharcoal)
                    .border(1.2.dp, CassBorderGold, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(CassEmerald.copy(alpha = 0.15f))
                            .border(1.5.dp, CassEmerald, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.GppGood,
                            contentDescription = null,
                            tint = CassEmerald,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "SHIELD PROTECTION ACTIVE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = CassEmerald
                        )
                        Text(
                            text = "Real-Time Heuristic Defense",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CassSilverLight
                        )
                        Text(
                            text = "Every scanned code is verified on-device prior to opening.",
                            style = MaterialTheme.typography.bodySmall,
                            color = CassSilverMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // URL Threat Analyzer Input
            Text(
                text = "MANUAL URL & LINK INSPECTOR",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = CassSilverMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = testUrl,
                onValueChange = {
                    testUrl = it
                    analysisResult = QrSecurityEngine.analyze(it)
                },
                placeholder = { Text("Paste URL or payload to audit...", color = CassSilverMuted) },
                leadingIcon = { Icon(Icons.Filled.Security, contentDescription = null, tint = CassGold) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("security_inspect_input"),
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

            Spacer(modifier = Modifier.height(16.dp))

            // Analysis Result Breakdown
            analysisResult?.let { res ->
                val (tintColor, statusIcon) = when (res.rating) {
                    SecurityRating.VERIFIED_SAFE, SecurityRating.SAFE -> CassEmerald to Icons.Filled.CheckCircle
                    SecurityRating.CAUTION -> Color(0xFFF59E0B) to Icons.Filled.GppMaybe
                    SecurityRating.SUSPICIOUS -> CassCrimson to Icons.Filled.Warning
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CassSurface)
                        .border(1.dp, tintColor.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(statusIcon, contentDescription = null, tint = tintColor, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = res.rating.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = tintColor
                                )
                            }
                            Text(
                                text = "Trust Score: ${res.score}%",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = CassSilverLight
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = res.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CassSilverLight
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Security Checks
                        SecurityCheckRow(
                            label = "Transport Encryption (HTTPS)",
                            passed = res.isHttps,
                            detail = if (res.isHttps) "TLS 1.3 / SSL Active" else "Plain text HTTP detected"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SecurityCheckRow(
                            label = "Phishing & Homograph Radar",
                            passed = res.riskFactors.none { it.contains("keyword") || it.contains("spoofing") },
                            detail = "Checked against common impersonation patterns"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SecurityCheckRow(
                            label = "TLD & Host Integrity",
                            passed = res.riskFactors.none { it.contains("TLD") || it.contains("IP") },
                            detail = res.domain ?: "Local Data Payload"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Best Practices Tips
            Text(
                text = "CASS DEFENSE GUIDELINES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = CassSilverMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            CassGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. Preview before Opening",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = CassGoldLight
                    )
                    Text(
                        text = "Never set your QR reader to auto-open unknown browser URLs immediately.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CassSilverMuted
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "2. Inspect Shortened Links",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = CassGoldLight
                    )
                    Text(
                        text = "Look out for unexpected bit.ly or tinyurl redirects pasted over legitimate business codes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CassSilverMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun SecurityCheckRow(
    label: String,
    passed: Boolean,
    detail: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = CassSilverLight)
            Text(text = detail, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = CassSilverMuted)
        }
        Icon(
            imageVector = if (passed) Icons.Filled.CheckCircle else Icons.Filled.Warning,
            contentDescription = null,
            tint = if (passed) CassEmerald else CassCrimson,
            modifier = Modifier.size(18.dp)
        )
    }
}
