package com.example.VideoFotoApp.ui.screens

import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.VideoFotoApp.CameraViewModel
import com.example.VideoFotoApp.ui.components.VideoControls

@Composable
fun VideoScreen(viewModel: CameraViewModel = viewModel(), navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var videoCapture by remember { mutableStateOf<VideoCapture<Recorder>?>(null) }
    var recording: Recording? by remember { mutableStateOf(null) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var isFlashOn by remember { mutableStateOf(false) }

    fun bindCamera(cameraProvider: ProcessCameraProvider, previewView: PreviewView) {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HD))
            .build()

        videoCapture = VideoCapture.withOutput(recorder)

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                videoCapture
            )
            camera.cameraControl.enableTorch(isFlashOn)
        } catch (exc: Exception) {
            Log.e("VideoScreen", "Error al vincular la cámara", exc)
            ContextCompat.getMainExecutor(context).execute {
                Toast.makeText(context, "Error al vincular la cámara", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            val cameraProvider = cameraProviderFuture.get()
            bindCamera(cameraProvider, previewView)

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )

    // Controles de video personalizados
    VideoControls(
        onRecordVideo = { isRecording ->
            if (isRecording) {
                val videoFile = viewModel.createVideoFile()
                val outputOptions = FileOutputOptions.Builder(videoFile).build()

                recording = videoCapture?.output
                    ?.prepareRecording(context, outputOptions)
                    ?.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                        if (recordEvent is VideoRecordEvent.Finalize) {
                            if (!recordEvent.hasError()) {
                                viewModel.addVideoToGallery(context, videoFile)
                                Toast.makeText(context, "Grabación finalizada", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e("CameraScreen", "Error en la grabación de video")
                            }
                        }
                    }
            } else {
                recording?.stop()
                recording = null
            }
        },
        onSwitchCamera = {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                Toast.makeText(context, "Cambio a cámara frontal", Toast.LENGTH_SHORT).show()
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                Toast.makeText(context, "Cambio a cámara trasera", Toast.LENGTH_SHORT).show()
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            val cameraProvider = cameraProviderFuture.get()
            bindCamera(cameraProvider, PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            })
        },
        onToggleFlash = { flashOn ->
            isFlashOn = flashOn
            val cameraProvider = cameraProviderFuture.get()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector
            )
            camera.cameraControl.enableTorch(isFlashOn)
            val message = if (isFlashOn) "Flash activo" else "Flash desactivado"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    )
}

@Composable
fun VideoControls(
    onRecordVideo: (Boolean) -> Unit,
    onSwitchCamera: () -> Unit,
    onToggleFlash: (Boolean) -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    var isFlashOn by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Botón para cambiar de cámara
            IconButton(
                onClick = onSwitchCamera,
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Cameraswitch,
                    contentDescription = "Cambiar Cámara",
                    tint = Color.White
                )
            }

            // Botón para grabar video
            IconButton(
                onClick = {
                    isRecording = !isRecording
                    onRecordVideo(isRecording)
                },
                modifier = Modifier
                    .size(80.dp)
                    .background(if (isRecording) Color.Red else MaterialTheme.colorScheme.secondary, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Videocam,
                    contentDescription = if (isRecording) "Detener Grabación" else "Iniciar Grabación",
                    tint = Color.White
                )
            }

            // Botón para activar/desactivar flash
            IconButton(
                onClick = {
                    isFlashOn = !isFlashOn
                    onToggleFlash(isFlashOn)
                },
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(
                    imageVector = if (isFlashOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                    contentDescription = "Flash",
                    tint = Color.White
                )
            }
        }
    }
}
