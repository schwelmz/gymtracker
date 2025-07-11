package com.example.gymtracker.ui.components // Make sure the package name is correct

import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

@OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerView(
    onBarcodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var hasScanned by remember { mutableStateOf(false) }

    AndroidView(
        factory = {
            val previewView = PreviewView(it)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(it)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8, Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
                    .build()
                val scanner = BarcodeScanning.getClient(options)

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(previewView.width, previewView.height))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                            imageProxy.image?.let { image ->
                                val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
                                scanner.process(inputImage)
                                    .addOnSuccessListener { barcodes ->
                                        if (barcodes.isNotEmpty() && !hasScanned) {
                                            hasScanned = true // Prevent multiple scans from one session
                                            barcodes.first().rawValue?.let(onBarcodeScanned)
                                        }
                                    }
                                    .addOnCompleteListener {
                                        imageProxy.close() // Always close the proxy
                                    }
                            }
                        }
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer)
                } catch (e: Exception) {
                    Log.e("Camera", "Use case binding failed", e)
                }
            }, ContextCompat.getMainExecutor(it))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}