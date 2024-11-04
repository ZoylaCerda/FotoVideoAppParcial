package com.example.VideoFotoApp.ui.screens

import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.VideoFotoApp.CameraViewModel
import com.example.VideoFotoApp.ui.components.PhotoControls

@Composable
fun PhotoScreen(viewModel: CameraViewModel, navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }

    // Observa el modo de flash para mostrar un Toast cuando está encendido
    val flashMode by viewModel.flashMode.collectAsState()
    LaunchedEffect(flashMode) {
        if (flashMode == ImageCapture.FLASH_MODE_ON) {
            Toast.makeText(context, "Flash activo", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para configurar la cámara con el selector actual
    fun bindCamera(cameraProvider: ProcessCameraProvider, previewView: PreviewView) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        imageCapture = ImageCapture.Builder()
            .setFlashMode(flashMode) // Aplica el modo de flash aquí
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e("PhotoScreen", "Error al vincular la cámara", exc)
            ContextCompat.getMainExecutor(context).execute {
                Toast.makeText(context, "Error al vincular la cámara", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box {
        // Vista previa de la cámara
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }
                val cameraProvider = cameraProviderFuture.get()
                bindCamera(cameraProvider, previewView)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Controles de foto posicionados en la parte inferior usando Column
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PhotoControls(
                viewModel = viewModel,
                onCapturePhoto = {
                    val photoFile = viewModel.createPhotoFile()
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    imageCapture?.flashMode = flashMode
                    imageCapture?.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                viewModel.addPhotoToGallery(context, photoFile)
                                Toast.makeText(context, "Foto guardada en la galería", Toast.LENGTH_SHORT).show()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Log.e("PhotoScreen", "Error al capturar foto: ${exception.message}", exception)
                                Toast.makeText(context, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },
                onSwitchCamera = {
                    cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }
                    val cameraProvider = cameraProviderFuture.get()
                    val previewView = PreviewView(context).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    }
                    bindCamera(cameraProvider, previewView)
                    Toast.makeText(context, "Cambio de cámara", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}
