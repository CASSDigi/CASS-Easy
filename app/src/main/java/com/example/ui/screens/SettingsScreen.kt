package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
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
import com.example.ui.theme.CassThemeMode
import com.example.viewmodel.MainViewModel

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onOpenBusinessCard: () -> Unit,
    onOpenSecurityCenter: () -> Unit
) {
    val currentTheme by viewModel.themeMode.collectAsStateWithLifecycle()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsStateWithLifecycle()
    var soundEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CassTopBar(
            title = "Preferences & Brand",
            subtitle = "Theme & Scanner Controls"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 80.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Brand Executive Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CassCharcoal)
                    .border(1.2.dp, CassBorderGold, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(CassObsidian)
                            .border(1.5.dp, CassGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cass_logo),
                            contentDescription = null,
                            modifier = Modifier.size(42.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "CASS EASY PRO",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            color = CassGoldLight
                        )
                        Text(
                            text = "Version 1.0.0 • Executive Edition",
                            style = MaterialTheme.typography.bodySmall,
                            color = CassSilverMuted
                        )
                        Text(
                            text = "Crafted for speed, aesthetic luxury & security.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = CassSilverMuted
                        )
                    }
                }
            }

            // Theme Mode Selector
            Text(
                text = "VISUAL ATMOSPHERE & LUXURY THEME",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = CassSilverMuted
            )

            CassGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val themes = listOf(
                        CassThemeMode.SIGNATURE_DARK to "CASS Signature Dark (Obsidian & Gold)",
                        CassThemeMode.AMOLED_BLACK to "AMOLED Pure Black (Zero Light)",
                        CassThemeMode.LUXURY_LIGHT to "Luxury Light (Champagne & Pearl)",
                        CassThemeMode.SYSTEM to "Follow System Environment"
                    )

                    themes.forEach { (mode, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setThemeMode(mode) }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (currentTheme == mode) FontWeight.Bold else FontWeight.Normal,
                                color = if (currentTheme == mode) CassGoldLight else CassSilverLight
                            )
                            RadioButton(
                                selected = currentTheme == mode,
                                onClick = { viewModel.setThemeMode(mode) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = CassGold,
                                    unselectedColor = CassSilverMuted
                                )
                            )
                        }
                    }
                }
            }

            // Scanner Settings
            Text(
                text = "SCANNER BEHAVIOR",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = CassSilverMuted
            )

            CassGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Haptic Tactile Feedback", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = CassSilverLight)
                            Text(text = "Vibrate on barcode lock", style = MaterialTheme.typography.bodySmall, color = CassSilverMuted)
                        }
                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = { viewModel.setHapticsEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = CassGold, checkedTrackColor = CassGoldDark)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Audio Acoustic Feedback", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = CassSilverLight)
                            Text(text = "Soft chime on detection", style = MaterialTheme.typography.bodySmall, color = CassSilverMuted)
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = { soundEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CassGold, checkedTrackColor = CassGoldDark)
                        )
                    }
                }
            }

            // Shortcuts to Tools
            Text(
                text = "EXECUTIVE UTILITIES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = CassSilverMuted
            )

            CassGlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenBusinessCard
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Badge, contentDescription = null, tint = CassGold)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Edit Digital Business Card", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = CassSilverLight)
                    }
                    Text(text = "Configure", style = MaterialTheme.typography.labelSmall, color = CassGold)
                }
            }

            CassGlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = onOpenSecurityCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Shield, contentDescription = null, tint = CassEmerald)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Security Center & Threat Radar", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = CassSilverLight)
                    }
                    Text(text = "Audit", style = MaterialTheme.typography.labelSmall, color = CassEmerald)
                }
            }

            // Data Privacy & Clearing
            Text(
                text = "DATA MANAGEMENT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = CassSilverMuted
            )

            Button(
                onClick = { viewModel.clearAllHistory() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("settings_clear_data_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CassCharcoal)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null, tint = CassCrimson)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Erase Scan History", color = CassCrimson, fontWeight = FontWeight.Bold)
            }
        }
    }
}
