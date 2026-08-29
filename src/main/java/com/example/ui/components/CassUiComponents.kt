package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import kotlinx.coroutines.launch
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.SecurityRating
import com.example.ui.theme.CassBorder
import com.example.ui.theme.CassBorderGold
import com.example.ui.theme.CassCharcoal
import com.example.ui.theme.CassCrimson
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
import com.example.util.CassHapticType
import com.example.util.CassHaptics
import com.example.util.cassClickable

@Composable
fun CassTopBar(
    title: String,
    subtitle: String? = null,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    showBrandLogo: Boolean = true
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 0.8.dp, color = CassBorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (navigationIcon != null) {
                    navigationIcon()
                    Spacer(modifier = Modifier.width(8.dp))
                } else if (showBrandLogo) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF222222), Color(0xFF0F0F0F))
                                )
                            )
                            .border(1.dp, CassBorderGold, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cass_logo),
                            contentDescription = "CASS Logo",
                            modifier = Modifier.size(32.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = CassSilverMuted
                        )
                    }
                }
            }

            if (actions != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    actions()
                }
            }
        }
    }
}

enum class CassNavDestination(
    val title: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    SCAN("Scan", Icons.Filled.QrCodeScanner, Icons.Outlined.QrCodeScanner),
    CREATE("Create", Icons.Filled.QrCode, Icons.Outlined.QrCode),
    LIBRARY("Library", Icons.Filled.Folder, Icons.Outlined.Folder),
    SETTINGS("Profile", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun CassBottomNavigation(
    currentDestination: CassNavDestination,
    onNavigate: (CassNavDestination) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Surface(
            color = Color(0xF2121212),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CassBorder,
                    shape = RoundedCornerShape(32.dp)
                )
                .shadow(elevation = 20.dp, shape = RoundedCornerShape(32.dp), ambientColor = Color.Black)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home
                CassNavItem(
                    destination = CassNavDestination.HOME,
                    isSelected = currentDestination == CassNavDestination.HOME,
                    onClick = { onNavigate(CassNavDestination.HOME) }
                )

                // Create
                CassNavItem(
                    destination = CassNavDestination.CREATE,
                    isSelected = currentDestination == CassNavDestination.CREATE,
                    onClick = { onNavigate(CassNavDestination.CREATE) }
                )

                // Center Scan Floating Button with radiant gold border ring
                Box(
                    modifier = Modifier
                        .offset(y = (-14).dp)
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    CassGold,
                                    CassGoldDark,
                                    CassGoldDeep,
                                    CassGold
                                )
                            )
                        )
                        .shadow(16.dp, CircleShape, spotColor = CassGold, ambientColor = CassGold)
                        .cassClickable(hapticType = CassHapticType.BUTTON_CLICK) { onNavigate(CassNavDestination.SCAN) }
                        .testTag("center_scan_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF080808))
                            .border(1.dp, CassGold.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCodeScanner,
                            contentDescription = "Scan QR",
                            tint = CassGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Library
                CassNavItem(
                    destination = CassNavDestination.LIBRARY,
                    isSelected = currentDestination == CassNavDestination.LIBRARY,
                    onClick = { onNavigate(CassNavDestination.LIBRARY) }
                )

                // Profile / Settings
                CassNavItem(
                    destination = CassNavDestination.SETTINGS,
                    isSelected = currentDestination == CassNavDestination.SETTINGS,
                    onClick = { onNavigate(CassNavDestination.SETTINGS) }
                )
            }
        }
    }
}

@Composable
private fun CassNavItem(
    destination: CassNavDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .cassClickable(hapticType = CassHapticType.LIGHT_TICK, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("nav_${destination.name.lowercase()}")
    ) {
        Icon(
            imageVector = if (isSelected) destination.filledIcon else destination.outlinedIcon,
            contentDescription = destination.title,
            tint = if (isSelected) CassGold else CassSilverMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = destination.title,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) CassGold else CassSilverMuted
        )
    }
}

@Composable
fun CassGlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CassBorder,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(CassSurface)
            .border(1.dp, borderColor, shape)
            .then(if (onClick != null) Modifier.cassClickable(hapticType = CassHapticType.LIGHT_TICK, onClick = onClick) else Modifier)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun SecurityBadge(
    rating: SecurityRating,
    score: Int? = null,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (rating) {
        SecurityRating.VERIFIED_SAFE -> Triple(Color(0x2010B981), CassEmerald, Icons.Filled.Shield)
        SecurityRating.SAFE -> Triple(Color(0x2010B981), CassEmerald, Icons.Filled.Shield)
        SecurityRating.CAUTION -> Triple(Color(0x20F59E0B), Color(0xFFF59E0B), Icons.Filled.Security)
        SecurityRating.SUSPICIOUS -> Triple(Color(0x20EF4444), CassCrimson, Icons.Filled.Security)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = rating.label + (if (score != null) " ($score%)" else ""),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
fun ScannerLaserReticle(
    modifier: Modifier = Modifier,
    isDetected: Boolean = false,
    detectionTimestamp: Long = 0L
) {
    val laserTransition = rememberInfiniteTransition(label = "scanner_laser")
    
    // Smooth scanning line sweep with easing
    val laserProgress by laserTransition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.94f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    // Ambient breathing glow for the corner brackets
    val ambientPulse by laserTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_bracket_glow"
    )

    // Detection wave and pulse animation
    val pulseAnim = remember { Animatable(0f) }
    val flashAnim = remember { Animatable(0f) }

    LaunchedEffect(detectionTimestamp) {
        if (detectionTimestamp > 0L) {
            flashAnim.snapTo(1f)
            pulseAnim.snapTo(0f)
            
            // Launch pulse expansion and flash fadeout in parallel
            kotlinx.coroutines.coroutineScope {
                launch {
                    flashAnim.animateTo(0f, animationSpec = tween(500, easing = FastOutSlowInEasing))
                }
                launch {
                    pulseAnim.animateTo(1f, animationSpec = tween(650, easing = FastOutSlowInEasing))
                }
            }
        }
    }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w / 2f, h / 2f)

            // 1. Subtle Center Target Crosshair and registration marks
            val crosshairLen = 14.dp.toPx()
            val crosshairColor = CassGold.copy(alpha = 0.25f)
            val strokeW = 1.2.dp.toPx()

            // Center + mark
            drawLine(crosshairColor, Offset(center.x - crosshairLen, center.y), Offset(center.x + crosshairLen, center.y), strokeW)
            drawLine(crosshairColor, Offset(center.x, center.y - crosshairLen), Offset(center.x, center.y + crosshairLen), strokeW)
            drawCircle(color = CassGold.copy(alpha = 0.15f), radius = 24.dp.toPx(), center = center, style = Stroke(width = 1.dp.toPx()))

            // 2. Trailing Beam Light Cone behind the Laser line
            val laserY = h * laserProgress
            val laserThickness = 2.5.dp.toPx()
            val trailHeight = 32.dp.toPx()

            // Laser beam soft vertical trailing glow
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CassGold.copy(alpha = 0.0f),
                        CassGold.copy(alpha = 0.15f * ambientPulse),
                        CassGoldLight.copy(alpha = 0.35f * ambientPulse),
                        CassGold.copy(alpha = 0.0f)
                    ),
                    startY = laserY - trailHeight,
                    endY = laserY + trailHeight
                ),
                topLeft = Offset(12.dp.toPx(), laserY - trailHeight),
                size = Size(w - 24.dp.toPx(), trailHeight * 2)
            )

            // Outer soft glow line
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        CassGold.copy(alpha = 0.35f * ambientPulse),
                        CassGold.copy(alpha = 0.85f * ambientPulse),
                        CassGold.copy(alpha = 0.35f * ambientPulse),
                        Color.Transparent
                    )
                ),
                start = Offset(4.dp.toPx(), laserY),
                end = Offset(w - 4.dp.toPx(), laserY),
                strokeWidth = laserThickness * 2.8f
            )

            // Center sharp laser beam with pure radiant gold core
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        CassGoldLight.copy(alpha = 0.75f),
                        Color.White,
                        CassGoldLight.copy(alpha = 0.75f),
                        Color.Transparent
                    )
                ),
                start = Offset(6.dp.toPx(), laserY),
                end = Offset(w - 6.dp.toPx(), laserY),
                strokeWidth = laserThickness,
                cap = StrokeCap.Round
            )

            // Small laser endpoint beacons
            drawCircle(
                color = CassGoldLight,
                radius = 2.5.dp.toPx(),
                center = Offset(w * 0.15f, laserY)
            )
            drawCircle(
                color = CassGoldLight,
                radius = 2.5.dp.toPx(),
                center = Offset(w * 0.85f, laserY)
            )

            // 3. Code Detection Pulse Waves (Triggered upon scan detection)
            if (pulseAnim.value > 0f && pulseAnim.value < 1f) {
                val p = pulseAnim.value
                // Expanding primary radiant ring
                val maxRadius = w * 0.65f
                val ringRadius = maxRadius * p
                val ringAlpha = (1f - p).coerceIn(0f, 1f) * 0.85f

                drawCircle(
                    color = CassGold.copy(alpha = ringAlpha),
                    radius = ringRadius,
                    center = center,
                    style = Stroke(width = (4.dp * (1f - p * 0.5f)).toPx())
                )

                // Secondary inner echoing pulse wave
                if (p > 0.2f) {
                    val p2 = (p - 0.2f) / 0.8f
                    val ringRadius2 = maxRadius * 0.8f * p2
                    val ringAlpha2 = (1f - p2).coerceIn(0f, 1f) * 0.55f
                    drawCircle(
                        color = Color(0xFF34D399).copy(alpha = ringAlpha2), // Emerald confirmation tint
                        radius = ringRadius2,
                        center = center,
                        style = Stroke(width = (2.5.dp * (1f - p2)).toPx())
                    )
                }

                // Center burst diamond lock
                val burstSize = 28.dp.toPx() * (1f + p * 0.5f)
                val burstAlpha = (1f - p).coerceIn(0f, 1f) * 0.9f
                drawCircle(
                    color = CassGoldLight.copy(alpha = burstAlpha * 0.4f),
                    radius = burstSize,
                    center = center
                )
            }

            // 4. Luxury Corner Brackets with Detection Flash & Dynamic Precision
            val cornerLen = 38.dp.toPx()
            val bracketStroke = (3.5.dp + (1.5.dp * flashAnim.value)).toPx()
            val bracketColor = if (flashAnim.value > 0.05f) {
                // Flash transition from pure emerald/white to CassGold
                CassGold.copy(alpha = 1f)
            } else {
                CassGold.copy(alpha = (0.75f + 0.25f * ambientPulse).coerceIn(0f, 1f))
            }

            // Top-Left Corner
            drawLine(bracketColor, Offset(0f, 0f), Offset(cornerLen, 0f), bracketStroke, cap = StrokeCap.Round)
            drawLine(bracketColor, Offset(0f, 0f), Offset(0f, cornerLen), bracketStroke, cap = StrokeCap.Round)
            // Accent tick
            drawCircle(CassGoldLight, 2.dp.toPx(), Offset(6.dp.toPx(), 6.dp.toPx()))

            // Top-Right Corner
            drawLine(bracketColor, Offset(w, 0f), Offset(w - cornerLen, 0f), bracketStroke, cap = StrokeCap.Round)
            drawLine(bracketColor, Offset(w, 0f), Offset(w, cornerLen), bracketStroke, cap = StrokeCap.Round)
            // Accent tick
            drawCircle(CassGoldLight, 2.dp.toPx(), Offset(w - 6.dp.toPx(), 6.dp.toPx()))

            // Bottom-Left Corner
            drawLine(bracketColor, Offset(0f, h), Offset(cornerLen, h), bracketStroke, cap = StrokeCap.Round)
            drawLine(bracketColor, Offset(0f, h), Offset(0f, h - cornerLen), bracketStroke, cap = StrokeCap.Round)
            // Accent tick
            drawCircle(CassGoldLight, 2.dp.toPx(), Offset(6.dp.toPx(), h - 6.dp.toPx()))

            // Bottom-Right Corner
            drawLine(bracketColor, Offset(w, h), Offset(w - cornerLen, h), bracketStroke, cap = StrokeCap.Round)
            drawLine(bracketColor, Offset(w, h), Offset(w, h - cornerLen), bracketStroke, cap = StrokeCap.Round)
            // Accent tick
            drawCircle(CassGoldLight, 2.dp.toPx(), Offset(w - 6.dp.toPx(), h - 6.dp.toPx()))
        }
    }
}
