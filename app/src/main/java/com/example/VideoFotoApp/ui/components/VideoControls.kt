package com.example.VideoFotoApp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.MaterialTheme

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
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            // Botón de cambio de cámara
            FloatingActionButton(
                onClick = onSwitchCamera,
                modifier = Modifier.size(72.dp),
                backgroundColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Cameraswitch,
                    contentDescription = "Cambiar cámara",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Botón de grabación/parada
            FloatingActionButton(
                onClick = {
                    isRecording = !isRecording
                    onRecordVideo(isRecording)
                },
                modifier = Modifier.size(80.dp),
                backgroundColor = if (isRecording) Color.Red else MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Filled.FiberManualRecord else Icons.Filled.PlayArrow,
                    contentDescription = if (isRecording) "Detener grabación" else "Iniciar grabación",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Botón de flash
            FloatingActionButton(
                onClick = {
                    isFlashOn = !isFlashOn
                    onToggleFlash(isFlashOn)
                },
                modifier = Modifier.size(72.dp),
                backgroundColor = if (isFlashOn) Color.Yellow else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    imageVector = if (isFlashOn) Icons.Filled.FlashlightOn else Icons.Filled.FlashlightOff,
                    contentDescription = "Alternar flash",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        // Texto de instrucción para grabación
        if (!isRecording) {
            Text(
                text = "Toque para grabar",
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .padding(bottom = 120.dp)
            )
        } else {
            Text(
                text = "Grabando...",
                fontSize = 16.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.7f), shape = MaterialTheme.shapes.small)
                    .padding(8.dp)
                    .padding(top = 16.dp)
            )
        }
    }
}
