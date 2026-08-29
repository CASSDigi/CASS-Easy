package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.CenterLogoType
import com.example.domain.model.QrDesignConfig
import com.example.domain.model.QrTemplateGallery
import com.example.domain.model.QrTemplatePreset
import com.example.ui.theme.CassBorder
import com.example.ui.theme.CassBorderGold
import com.example.ui.theme.CassCharcoal
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

/**
 * Fast 1-tap horizontal carousel for instantly applying popular presets from Business, Events & Social Media.
 */
@Composable
fun QuickTemplateCarousel(
    currentConfig: QrDesignConfig,
    onApplyTemplate: (QrTemplatePreset) -> Unit,
    onOpenFullGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickPresets = remember {
        listOf(
            QrTemplateGallery.TEMPLATES.first { it.id == "biz_exec_gold" },
            QrTemplateGallery.TEMPLATES.first { it.id == "social_creator_neon" },
            QrTemplateGallery.TEMPLATES.first { it.id == "event_vip_gala" },
            QrTemplateGallery.TEMPLATES.first { it.id == "biz_tech_founder" },
            QrTemplateGallery.TEMPLATES.first { it.id == "social_sunset_glow" },
            QrTemplateGallery.TEMPLATES.first { it.id == "retail_luxury_menu" }
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = CassGold,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "1-TAP QUICK TEMPLATES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = CassSilverMuted
                )
            }

            Text(
                text = "View All →",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = CassGold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onOpenFullGallery() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(quickPresets) { preset ->
                val isSelected = isConfigMatchingPreset(currentConfig, preset.config)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) CassSurfaceElevated else CassSurface)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) CassGold else CassBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onApplyTemplate(preset) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("quick_template_${preset.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Swatch dot
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(preset.primaryColorHex),
                                            Color(preset.secondaryColorHex)
                                        )
                                    )
                                )
                                .border(0.8.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        )

                        Column {
                            Text(
                                text = preset.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CassGoldLight else CassSilverLight,
                                fontSize = 12.sp
                            )
                            Text(
                                text = preset.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = CassSilverMuted,
                                fontSize = 10.sp
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Active",
                                tint = CassGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full Template Gallery section for the QR Creator Studio with category filtering and interactive styling cards.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TemplateGallerySection(
    currentConfig: QrDesignConfig,
    onApplyTemplate: (QrTemplatePreset) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredTemplates = remember(selectedCategory) {
        if (selectedCategory == "All") {
            QrTemplateGallery.TEMPLATES
        } else {
            QrTemplateGallery.TEMPLATES.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DESIGN TEMPLATE GALLERY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = CassSilverMuted
                )
                Text(
                    text = "Pre-designed styling for Business, Events & Social Media",
                    style = MaterialTheme.typography.bodySmall,
                    color = CassSilverLight,
                    fontSize = 12.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(CassGold.copy(alpha = 0.15f))
                    .border(0.8.dp, CassGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${filteredTemplates.size} Styles",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = CassGold,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(QrTemplateGallery.CATEGORIES) { cat ->
                val isCatSelected = selectedCategory == cat
                val icon = getCategoryIcon(cat)

                FilterChip(
                    selected = isCatSelected,
                    onClick = { selectedCategory = cat },
                    leadingIcon = if (icon != null) {
                        {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isCatSelected) CassObsidian else CassGold
                            )
                        }
                    } else null,
                    label = {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CassGold,
                        selectedLabelColor = CassObsidian,
                        containerColor = CassSurface,
                        labelColor = CassSilverLight
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isCatSelected,
                        borderColor = if (isCatSelected) CassGold else CassBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Template Cards
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            filteredTemplates.forEach { preset ->
                TemplateCard(
                    preset = preset,
                    isApplied = isConfigMatchingPreset(currentConfig, preset.config),
                    onApply = { onApplyTemplate(preset) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TemplateCard(
    preset: QrTemplatePreset,
    isApplied: Boolean,
    onApply: () -> Unit
) {
    val gradientBrush = remember(preset) {
        Brush.linearGradient(
            colors = listOf(
                Color(preset.primaryColorHex),
                Color(preset.secondaryColorHex)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (isApplied) CassCharcoal else CassSurface)
            .border(
                width = if (isApplied) 1.5.dp else 1.dp,
                color = if (isApplied) CassBorderGold else CassBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onApply() }
            .padding(16.dp)
            .testTag("template_card_${preset.id}")
    ) {
        Column {
            // Header Row: Color Swatch + Title + Tag Pill + Applied State
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Visual Multi-Color Swatch Orb
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(gradientBrush)
                            .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                            .shadow(8.dp, CircleShape, spotColor = Color(preset.primaryColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(preset.category) ?: Icons.Filled.Style,
                            contentDescription = null,
                            tint = Color(0xFF0F0F0F),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = preset.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isApplied) CassGoldLight else CassSilverLight,
                                fontSize = 15.sp
                            )

                            // Tag Badge (e.g. Featured, Viral, Pro)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(preset.primaryColorHex).copy(alpha = 0.18f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = preset.tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(preset.primaryColorHex),
                                    fontSize = 9.sp
                                )
                            }
                        }

                        Text(
                            text = "${preset.category} Template",
                            style = MaterialTheme.typography.labelSmall,
                            color = CassSilverMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Active Applied Status or Apply Button
                if (isApplied) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(CassEmerald.copy(alpha = 0.18f))
                            .border(1.dp, CassEmerald.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Active",
                                tint = CassEmerald,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Applied",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CassEmerald,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onApply,
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("apply_template_btn_${preset.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CassSurfaceElevated),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "Apply",
                            fontWeight = FontWeight.Bold,
                            color = CassGold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = preset.description,
                style = MaterialTheme.typography.bodySmall,
                color = CassSilverLight.copy(alpha = 0.85f),
                lineHeight = 16.sp,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Design specs chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SpecBadge(label = preset.config.gradientStyle.displayName)
                SpecBadge(label = "Dots: ${preset.config.dotPattern.displayName}")
                SpecBadge(label = "Eyes: ${preset.config.eyeStyle.displayName}")
                if (preset.config.centerLogo != CenterLogoType.NONE) {
                    SpecBadge(label = "Logo: ${preset.config.centerLogo.displayName}")
                }
                if (preset.config.frameText.isNotBlank()) {
                    SpecBadge(label = "CTA: \"${preset.config.frameText}\"")
                }
            }
        }
    }
}

@Composable
private fun SpecBadge(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(CassObsidian)
            .border(0.6.dp, CassBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = CassSilverMuted,
            fontSize = 10.sp
        )
    }
}

private fun getCategoryIcon(category: String): ImageVector? {
    return when (category.lowercase()) {
        "business" -> Icons.Filled.BusinessCenter
        "events" -> Icons.Filled.Celebration
        "social media" -> Icons.Filled.Public
        "retail & dining" -> Icons.Filled.Storefront
        else -> null
    }
}

private fun isConfigMatchingPreset(current: QrDesignConfig, preset: QrDesignConfig): Boolean {
    return current.gradientStyle == preset.gradientStyle &&
            current.dotPattern == preset.dotPattern &&
            current.eyeStyle == preset.eyeStyle &&
            current.centerLogo == preset.centerLogo &&
            (preset.frameText.isEmpty() || current.frameText == preset.frameText)
}
