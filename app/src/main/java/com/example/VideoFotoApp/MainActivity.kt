package com.example.VideoFotoApp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.VideoFotoApp.ui.screens.VideoScreen
import com.example.VideoFotoApp.ui.screens.HomeScreen
import com.example.VideoFotoApp.ui.screens.PhotoScreen
import com.example.VideoFotoApp.ui.theme.VideoFotoAppTheme

class MainActivity : ComponentActivity() {
    private var hasCameraPermission by mutableStateOf(false) // Variable para manejar el estado del permiso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val cameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            hasCameraPermission = isGranted // Actualiza el estado del permiso
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            VideoFotoAppTheme {
                AppNavigation(
                    onOpenPhotoGallery = { openPhotoGallery() }
                )
            }
        }
    }

    private fun openPhotoGallery() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }
        startActivity(intent)
    }

    private fun openVideoGallery() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*")
        }
        startActivity(intent)
    }
}

@Composable
fun AppNavigation(
    onOpenPhotoGallery: () -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    var permissionsGranted by remember { mutableStateOf(false) }

    val permissionsToRequest = mutableListOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO).apply {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }.toTypedArray()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsGranted = permissions[Manifest.permission.CAMERA] == true &&
                permissions[Manifest.permission.RECORD_AUDIO] == true &&
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ||
                        (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
                                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true))

        Log.d("AppNavigation", "Permisos actualizados: $permissionsGranted")
    }

    LaunchedEffect(Unit) {
        val cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        val recordAudioPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        val writeStoragePermission = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) else PackageManager.PERMISSION_GRANTED
        val readStoragePermission = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) else PackageManager.PERMISSION_GRANTED

        Log.d("AppNavigation", "Verificación de permisos al iniciar:")
        Log.d("AppNavigation", "CAMERA: ${cameraPermission == PackageManager.PERMISSION_GRANTED}")
        Log.d("AppNavigation", "RECORD_AUDIO: ${recordAudioPermission == PackageManager.PERMISSION_GRANTED}")
        Log.d("AppNavigation", "WRITE_EXTERNAL_STORAGE: ${writeStoragePermission == PackageManager.PERMISSION_GRANTED}")
        Log.d("AppNavigation", "READ_EXTERNAL_STORAGE: ${readStoragePermission == PackageManager.PERMISSION_GRANTED}")

        if (cameraPermission == PackageManager.PERMISSION_GRANTED &&
            recordAudioPermission == PackageManager.PERMISSION_GRANTED &&
            writeStoragePermission == PackageManager.PERMISSION_GRANTED &&
            readStoragePermission == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted = true
            Log.d("AppNavigation", "Permisos concedidos inicialmente.")
        } else {
            Log.d("AppNavigation", "Solicitando permisos...")
            launcher.launch(permissionsToRequest)
        }
    }

    val viewModel: CameraViewModel = viewModel(factory = CameraViewModelFactory(context))

    if (permissionsGranted) {
        Log.d("AppNavigation", "Mostrando navegación.")
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    navController = navController,
                    onOpenPhotoGallery = onOpenPhotoGallery
                )
            }
            composable("photo") {
                PhotoScreen(viewModel = viewModel, navController = navController)
            }
            composable("video") {
                VideoScreen(viewModel = viewModel, navController = navController)
            }
            composable("view_photos") { /* Pantalla para ver las fotos guardadas */ }
            composable("view_videos") { /* Pantalla para ver los videos guardados */ }
        }
    } else {
        Text("Se requieren permisos para continuar...")
        Log.d("AppNavigation", "Esperando permisos.")
    }

}