package com.example.ui.screens

import android.graphics.Bitmap
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.entity.BusinessCardEntity
import com.example.domain.generator.QrCodeGenerator
import com.example.domain.model.CenterLogoType
import com.example.domain.model.DotPattern
import com.example.domain.model.EyeStyle
import com.example.domain.model.GradientStyle
import com.example.domain.model.QrDesignConfig
import com.example.ui.components.CassGlassCard
import com.example.ui.components.CassTopBar
import com.example.ui.theme.CassBorder
import com.example.ui.theme.CassBorderGold
import com.example.ui.theme.CassCharcoal
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
fun DigitalBusinessCardScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val cardData by viewModel.primaryBusinessCard.collectAsStateWithLifecycle()

    var isEditing by remember { mutableStateOf(false) }

    var name by remember(cardData) { mutableStateOf(cardData?.fullName ?: "Alexander Vance") }
    var title by remember(cardData) { mutableStateOf(cardData?.jobTitle ?: "Managing Director") }
    var company by remember(cardData) { mutableStateOf(cardData?.company ?: "CASS Global Innovations") }
    var phone by remember(cardData) { mutableStateOf(cardData?.phone ?: "+1 (415) 890-2341") }
    var email by remember(cardData) { mutableStateOf(cardData?.email ?: "alexander@cass.com") }
    var website by remember(cardData) { mutableStateOf(cardData?.website ?: "https://cass-innovations.com") }
    var bio by remember(cardData) { mutableStateOf(cardData?.bio ?: "Executive technologist leading luxury digital experiences.") }

    // Generate live vCard string
    val vcardPayload = """BEGIN:VCARD
VERSION:3.0
FN:$name
TITLE:$title
ORG:$company
TEL:$phone
EMAIL:$email
URL:$website
NOTE:$bio
END:VCARD"""

    val cardQrBitmap = remember(vcardPayload) {
        QrCodeGenerator.generateQrBitmap(
            content = vcardPayload,
            config = QrDesignConfig(
                fgColorHex = 0xFFD4AF37,
                bgColorHex = 0xFF0A0B0E,
                gradientStyle = GradientStyle.CASS_GOLD,
                dotPattern = DotPattern.ROUNDED,
                eyeStyle = EyeStyle.ROUNDED,
                centerLogo = CenterLogoType.CASS_LOGO
            ),
            size = 512
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CassTopBar(
            title = "Digital Identity",
            subtitle = "Executive Smart vCard & NFC",
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = CassSilverLight)
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        if (isEditing) {
                            cardData?.let {
                                viewModel.updateBusinessCard(
                                    it.copy(
                                        fullName = name,
                                        jobTitle = title,
                                        company = company,
                                        phone = phone,
                                        email = email,
                                        website = website,
                                        bio = bio
                                    )
                                )
                            }
                        }
                        isEditing = !isEditing
                    },
                    modifier = Modifier.testTag("edit_card_btn")
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Filled.QrCode else Icons.Filled.Edit,
                        contentDescription = "Edit Card",
                        tint = CassGold
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Metallic Gold Executive Smart Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF262115),
                                Color(0xFF161410),
                                Color(0xFF0D0C0A)
                            )
                        )
                    )
                    .border(1.5.dp, CassBorderGold, RoundedCornerShape(24.dp))
                    .shadow(24.dp, RoundedCornerShape(24.dp), spotColor = CassGold)
                    .padding(22.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.cass_logo),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "CASS EXECUTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                color = CassGold
                            )
                        }

                        Text(
                            text = "SMART NFC / QR",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = CassSilverMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CassSilverLight
                    )

                    Text(
                        text = "$title • $company",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = CassGoldLight
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // QR Code and Info Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = CassSilverMuted
                            )
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodySmall,
                                color = CassSilverMuted
                            )
                            Text(
                                text = website,
                                style = MaterialTheme.typography.bodySmall,
                                color = CassGoldLight
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CassObsidian)
                                .border(1.dp, CassBorderGold, RoundedCornerShape(12.dp))
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = cardQrBitmap.asImageBitmap(),
                                contentDescription = "vCard QR",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.shareText(vcardPayload, "Share vCard") },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CassGold)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, tint = CassObsidian)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share vCard", fontWeight = FontWeight.Bold, color = CassObsidian)
                }

                Button(
                    onClick = { viewModel.copyToClipboard(vcardPayload, "vCard Data") },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .border(1.dp, CassBorderGold, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CassSurfaceElevated)
                ) {
                    Icon(Icons.Filled.QrCode, contentDescription = null, tint = CassGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy Data", fontWeight = FontWeight.Bold, color = CassSilverLight)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Edit Profile Form if in edit mode
            if (isEditing) {
                CassGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "EDIT PROFILE ATTRIBUTES",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = CassGold
                        )
                        CassTextField(value = name, onValueChange = { name = it }, label = "Full Name", placeholder = "Alexander Vance")
                        CassTextField(value = title, onValueChange = { title = it }, label = "Job Title", placeholder = "Managing Director")
                        CassTextField(value = company, onValueChange = { company = it }, label = "Company", placeholder = "CASS Innovations")
                        CassTextField(value = phone, onValueChange = { phone = it }, label = "Phone", placeholder = "+1 (415) 890-2341")
                        CassTextField(value = email, onValueChange = { email = it }, label = "Email", placeholder = "alex@cass.com")
                        CassTextField(value = website, onValueChange = { website = it }, label = "Website", placeholder = "https://cass.com")
                        CassTextField(value = bio, onValueChange = { bio = it }, label = "Bio Note", placeholder = "Executive summary")
                    }
                }
            }
        }
    }
}
