package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ScannerLaserReticle
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
import com.example.viewmodel.MainViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class, ExperimentalLayoutApi::class)
@Composable
fun OcrScannerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToCreateQr: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val detectedText by viewModel.ocrDetectedText.collectAsStateWithLifecycle()
    var isTorchOn by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var showLinePicker by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var cameraInstance by remember { mutableStateOf<Camera?>(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var cameraProviderRef by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                cameraProviderRef?.unbindAll()
                cameraExecutor.shutdown()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    val inputImage = InputImage.fromBitmap(bitmap, 0)
                    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    recognizer.process(inputImage)
                        .addOnSuccessListener { visionText ->
                            if (visionText.text.isNotBlank()) {
                                viewModel.setOcrText(visionText.text)
                                isPaused = true // Freeze so user can review the photo's extracted text
                            }
                        }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    // Process lines and words for statistics and line picker
    val lines = remember(detectedText) {
        detectedText.lines().map { it.trim() }.filter { it.isNotBlank() }
    }
    val wordCount = remember(detectedText) {
        if (detectedText.isBlank()) 0 else detectedText.split("\\s+".toRegex()).count { it.isNotBlank() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CassObsidian)
    ) {
        // Camera Stream
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    cameraProviderRef = cameraProvider
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    var lastProcessed = 0L

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                val mediaImage = imageProxy.image
                                if (mediaImage != null && !isPaused) {
                                    val now = System.currentTimeMillis()
                                    if (now - lastProcessed > 800) {
                                        val image = InputImage.fromMediaImage(
                                            mediaImage,
                                            imageProxy.imageInfo.rotationDegrees
                                        )
                                        textRecognizer.process(image)
                                            .addOnSuccessListener { result ->
                                                if (result.text.isNotBlank() && !isPaused) {
                                                    lastProcessed = now
                                                    viewModel.setOcrText(result.text)
                                                }
                                            }
                                            .addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                    } else {
                                        imageProxy.close()
                                    }
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        val cam = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                        cameraInstance = cam
                        cam.cameraControl.enableTorch(isTorchOn)
                    } catch (exc: Exception) {
                        // ignore
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxSize(),
            update = {
                cameraInstance?.cameraControl?.enableTorch(isTorchOn)
            }
        )

        // Viewfinder overlay
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 95.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 320.dp, height = 180.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.Black.copy(alpha = 0.25f))
                        .border(1.2.dp, if (isPaused) CassEmerald else CassBorderGold.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                ) {
                    if (!isPaused) {
                        ScannerLaserReticle(modifier = Modifier.fillMaxSize())
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(CassObsidian.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SCAN PAUSED (READY TO SELECT)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CassEmerald,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(CassObsidian.copy(alpha = 0.85f))
                            .border(1.dp, CassBorderGold, RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (isPaused) "Frame Locked" else "Point camera at document, receipt or book",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isPaused) CassEmerald else CassGoldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Top Navigation & Controls Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CassObsidian.copy(alpha = 0.8f))
                    .border(1.dp, CassBorder, CircleShape)
                    .testTag("ocr_back_btn")
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = CassSilverLight)
            }

            Text(
                text = "EXTRACT TEXT STUDIO",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                color = CassGold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Pause / Lock toggle
                IconButton(
                    onClick = { isPaused = !isPaused },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isPaused) CassEmerald else CassObsidian.copy(alpha = 0.8f))
                        .border(1.dp, if (isPaused) CassEmerald else CassBorder, CircleShape)
                        .testTag("ocr_pause_btn")
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        tint = if (isPaused) CassObsidian else CassSilverLight
                    )
                }

                // Gallery Image Picker
                IconButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CassObsidian.copy(alpha = 0.8f))
                        .border(1.dp, CassBorder, CircleShape)
                        .testTag("ocr_gallery_btn")
                ) {
                    Icon(Icons.Filled.Image, contentDescription = "Pick Document Image", tint = CassGold)
                }

                // Flashlight
                IconButton(
                    onClick = { isTorchOn = !isTorchOn },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isTorchOn) CassGold else CassObsidian.copy(alpha = 0.8f))
                        .border(1.dp, if (isTorchOn) CassGoldLight else CassBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isTorchOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                        contentDescription = "Flashlight",
                        tint = if (isTorchOn) CassObsidian else CassSilverLight
                    )
                }
            }
        }

        // Bottom Result Drawer with Interactive Selection & Line Picker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(14.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CassCharcoal.copy(alpha = 0.96f))
                .border(1.2.dp, CassBorderGold, RoundedCornerShape(24.dp))
                .padding(14.dp)
        ) {
            Column {
                // Header with live stats and mode toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.DocumentScanner, contentDescription = null, tint = CassGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EXTRACTED TEXT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = CassGold
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (detectedText.isNotBlank()) {
                            Text(
                                text = "${detectedText.length} chars • $wordCount words",
                                style = MaterialTheme.typography.labelSmall,
                                color = CassSilverMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Toggle Line Picker Mode
                        FilterChip(
                            selected = showLinePicker,
                            onClick = { showLinePicker = !showLinePicker },
                            label = { Text(if (showLinePicker) "Lines View" else "Full Text", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CassGold,
                                selectedLabelColor = CassObsidian,
                                containerColor = CassSurface,
                                labelColor = CassSilverMuted
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Interactive Content Area
                if (showLinePicker && lines.isNotEmpty()) {
                    // Line-by-line interactive picker
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CassSurface)
                            .border(1.dp, CassBorder, RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(lines) { line ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CassObsidian)
                                        .border(0.8.dp, CassBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.copyToClipboard(line)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = line,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = CassSilverLight,
                                        modifier = Modifier.weight(1f),
                                        fontSize = 12.sp
                                    )
                                    Icon(
                                        Icons.Filled.ContentCopy,
                                        contentDescription = "Copy Line",
                                        tint = CassGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Standard Selection Container allowing touch-selection handles and drag copying
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CassSurface)
                            .border(1.dp, CassBorder, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (detectedText.isNotBlank()) {
                            SelectionContainer {
                                Text(
                                    text = detectedText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CassSilverLight,
                                    lineHeight = 18.sp
                                )
                            }
                        } else {
                            Text(
                                text = "Point camera at text or choose a document from gallery. Live text extraction will appear here with selectable touch handles.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CassSilverMuted.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Quick Action Toolbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Copy All
                    Button(
                        onClick = {
                            if (detectedText.isNotBlank()) viewModel.copyToClipboard(detectedText)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("ocr_copy_all_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CassGold),
                        enabled = detectedText.isNotBlank(),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = CassObsidian, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy All", fontWeight = FontWeight.Bold, color = CassObsidian, fontSize = 11.sp)
                    }

                    // Create QR from Extracted Text
                    Button(
                        onClick = {
                            if (detectedText.isNotBlank()) {
                                viewModel.populateCreatorFromExtractedText(detectedText)
                                onNavigateToCreateQr?.invoke()
                            }
                        },
                        modifier = Modifier
                            .weight(1.1f)
                            .height(40.dp)
                            .testTag("ocr_create_qr_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CassSurfaceElevated),
                        enabled = detectedText.isNotBlank(),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Filled.QrCode, contentDescription = null, tint = CassGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Make QR", color = CassGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    // Share
                    Button(
                        onClick = {
                            if (detectedText.isNotBlank()) viewModel.shareText(detectedText, "Share Extracted Text")
                        },
                        modifier = Modifier
                            .weight(0.9f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CassSurfaceElevated),
                        enabled = detectedText.isNotBlank(),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, tint = CassSilverLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", color = CassSilverLight, fontSize = 11.sp)
                    }

                    // Vault
                    Button(
                        onClick = {
                            if (detectedText.isNotBlank()) {
                                viewModel.saveVaultItem(
                                    title = "Extracted Note (${detectedText.take(15).trim()}...)",
                                    category = "OCR Notes",
                                    payload = detectedText,
                                    notes = "Extracted via CASS ML Kit OCR Engine"
                                )
                            }
                        },
                        modifier = Modifier
                            .weight(0.9f)
                            .height(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CassSurfaceElevated),
                        enabled = detectedText.isNotBlank(),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = CassEmerald, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Vault", color = CassEmerald, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
