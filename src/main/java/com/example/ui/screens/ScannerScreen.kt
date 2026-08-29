package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.model.ParsedQrContent
import com.example.ui.components.ScannerLaserReticle
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
import com.example.util.CassHapticType
import com.example.util.CassHaptics
import com.example.util.cassClickable
import com.example.viewmodel.MainViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
@Composable
fun ScannerScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenOcr: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isTorchOn by viewModel.isTorchOn.collectAsStateWithLifecycle()
    val isFrontCamera by viewModel.isFrontCamera.collectAsStateWithLifecycle()
    val isBatchMode by viewModel.isBatchMode.collectAsStateWithLifecycle()
    val isContinuousScan by viewModel.isContinuousScan.collectAsStateWithLifecycle()
    val batchList by viewModel.batchScanList.collectAsStateWithLifecycle()
    val lastDetectionTimestamp by viewModel.lastDetectionTimestamp.collectAsStateWithLifecycle()

    var cameraInstance by remember { mutableStateOf<Camera?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

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

    // Gallery Image Picker for QR detection
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
                    val scanner = BarcodeScanning.getClient()
                    scanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            if (barcodes.isNotEmpty()) {
                                val firstBarcode = barcodes[0]
                                val raw = firstBarcode.rawValue ?: firstBarcode.displayValue
                                if (!raw.isNullOrBlank()) {
                                    val formatName = getBarcodeFormatName(firstBarcode.format)
                                    CassHaptics.perform(context, CassHapticType.SCAN_SUCCESS)
                                    viewModel.onBarcodeDetected(raw, formatName)
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CassObsidian)
    ) {
        if (hasCameraPermission) {
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

                        val options = BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(
                                Barcode.FORMAT_ALL_FORMATS
                            )
                            .build()
                        val barcodeScanner = BarcodeScanning.getClient(options)

                        var lastScanTimestamp = 0L

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val currentTime = System.currentTimeMillis()
                                        // Throttle scanning to avoid hyper-spamming
                                        if (currentTime - lastScanTimestamp > 700) {
                                            val image = InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees
                                            )
                                             barcodeScanner.process(image)
                                                .addOnSuccessListener { barcodes ->
                                                    if (barcodes.isNotEmpty()) {
                                                        val firstBarcode = barcodes[0]
                                                        val raw = firstBarcode.rawValue ?: firstBarcode.displayValue
                                                        if (!raw.isNullOrBlank()) {
                                                            val formatName = getBarcodeFormatName(firstBarcode.format)
                                                            lastScanTimestamp = currentTime
                                                            CassHaptics.perform(ctx, CassHapticType.SCAN_SUCCESS)
                                                            viewModel.onBarcodeDetected(raw, formatName)
                                                        }
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

                        val cameraSelector = if (isFrontCamera) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }

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
                            // Camera binding exception
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize(),
                update = {
                    cameraInstance?.cameraControl?.enableTorch(isTorchOn)
                }
            )
        } else {
            // Permission request placeholder
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCode,
                    contentDescription = null,
                    tint = CassGold,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Camera Access Required",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = CassSilverLight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CASS Easy requires camera access to scan QR codes and barcodes instantaneously with on-device privacy.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CassSilverMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = CassGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Grant Permission", fontWeight = FontWeight.Bold, color = CassObsidian)
                }
            }
        }

        // Viewfinder Center Overlay
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Live Status Beacon Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xCC080808))
                        .border(1.dp, CassBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SCANNING LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = CassSilverLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(290.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0x20000000))
                        .border(1.dp, CassBorder, RoundedCornerShape(32.dp))
                ) {
                    ScannerLaserReticle(
                        modifier = Modifier.fillMaxSize(),
                        detectionTimestamp = lastDetectionTimestamp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xDD080808))
                        .border(1.dp, CassBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (isBatchMode) "Batch Scanning (${batchList.size} items)" else "Align QR code or Barcode inside frame",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = if (isBatchMode) CassEmerald else CassSilverMuted
                    )
                }
            }
        }

        // Top Navigation & Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xB3121212))
                    .border(1.dp, CassBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CassSilverLight
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Torch Toggle
                IconButton(
                    onClick = { viewModel.toggleTorch() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isTorchOn) CassGold else Color(0xB3121212))
                        .border(1.dp, if (isTorchOn) CassGoldLight else CassBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isTorchOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                        contentDescription = "Flashlight",
                        tint = if (isTorchOn) CassObsidian else CassSilverLight
                    )
                }

                // Camera Switch
                IconButton(
                    onClick = { viewModel.toggleCameraFacing() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xB3121212))
                        .border(1.dp, CassBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = CassSilverLight
                    )
                }
            }
        }

        // Bottom Action Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xF2121212))
                    .border(1.dp, CassBorder, RoundedCornerShape(32.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery Scan
                ScannerBottomAction(
                    icon = Icons.Filled.Image,
                    label = "Gallery",
                    isSelected = false,
                    onClick = { galleryLauncher.launch("image/*") }
                )

                // Batch Mode
                ScannerBottomAction(
                    icon = Icons.Filled.Layers,
                    label = "Batch Mode",
                    isSelected = isBatchMode,
                    onClick = { viewModel.toggleBatchMode() }
                )

                // Extract Text Utility
                ScannerBottomAction(
                    icon = Icons.Filled.DocumentScanner,
                    label = "Extract Text",
                    isSelected = false,
                    onClick = onOpenOcr
                )
            }
        }
    }
}

@Composable
private fun ScannerBottomAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .cassClickable(hapticType = CassHapticType.BUTTON_CLICK, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) CassGold else CassSilverMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) CassGold else CassSilverMuted
        )
    }
}

private fun triggerHaptic(context: Context) {
    CassHaptics.perform(context, CassHapticType.SCAN_SUCCESS)
}

private fun getBarcodeFormatName(format: Int): String {
    return when (format) {
        Barcode.FORMAT_QR_CODE -> "QR Code"
        Barcode.FORMAT_AZTEC -> "Aztec"
        Barcode.FORMAT_CODABAR -> "Codabar"
        Barcode.FORMAT_CODE_39 -> "Code 39"
        Barcode.FORMAT_CODE_93 -> "Code 93"
        Barcode.FORMAT_CODE_128 -> "Code 128"
        Barcode.FORMAT_DATA_MATRIX -> "Data Matrix"
        Barcode.FORMAT_EAN_8 -> "EAN-8"
        Barcode.FORMAT_EAN_13 -> "EAN-13"
        Barcode.FORMAT_ITF -> "ITF"
        Barcode.FORMAT_PDF417 -> "PDF417"
        Barcode.FORMAT_UPC_A -> "UPC-A"
        Barcode.FORMAT_UPC_E -> "UPC-E"
        else -> "Barcode"
    }
}
