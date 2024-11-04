package com.example.VideoFotoApp.ui.components

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.VideoFotoApp.CameraViewModel

@Composable
fun PhotoControls(
    viewModel: CameraViewModel,
    onCapturePhoto: () -> Unit,
    onSwitchCamera: () -> Unit
) {
    val flashMode = viewModel.flashMode.collectAsState().value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón de cambio de cámara
        FloatingActionButton(
            onClick = onSwitchCamera,
            backgroundColor = Color(0xFF4CAF50),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "Cambiar a la cámara ${if (flashMode == ImageCapture.FLASH_MODE_ON) "trasera" else "frontal"}"
            )
        }

        // Botón de captura de foto
        FloatingActionButton(
            onClick = onCapturePhoto,
            modifier = Modifier.size(72.dp),
            backgroundColor = Color(0xFFF44336),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = "Capturar foto",
                modifier = Modifier.size(36.dp)
            )
        }

        // Botón de control de flash
        FloatingActionButton(
            onClick = viewModel::toggleFlashMode,
            backgroundColor = Color(0xFFFFC107),
            contentColor = Color.Black
        ) {
            val icon = when (flashMode) {
                ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashlightOn
                ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto
                else -> Icons.Default.FlashlightOff
            }
            Icon(
                imageVector = icon,
                contentDescription = when (flashMode) {
                    ImageCapture.FLASH_MODE_ON -> "Flash encendido"
                    ImageCapture.FLASH_MODE_AUTO -> "Flash en modo automático"
                    else -> "Flash apagado"
                }
            )
        }
    }
}
