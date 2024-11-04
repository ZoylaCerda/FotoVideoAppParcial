
# 📸 VideoFotoApp

VideoFotoApp es una aplicación de Android desarrollada en Kotlin que permite a los usuarios capturar fotos y grabar videos utilizando la API de `CameraX`. Ofrece una interfaz amigable y controles intuitivos para manejar la cámara frontal y trasera, cambiar el modo de flash y almacenar las capturas en la galería del dispositivo.

## 🛠️ Características

- **Captura de Fotos**: Toma fotos y guárdalas directamente en la galería del dispositivo.
- **Grabación de Video**: Graba videos en alta calidad con la cámara trasera o frontal.
- **Cambio de Cámara**: Alterna entre la cámara frontal y trasera.
- **Control de Flash**: Configura el flash en modo encendido, apagado o automático.
- **Interfaz de Usuario Moderna**: Usa `Jetpack Compose` para una UI fluida e interactiva.
  
## 🚀 Tecnologías y Librerías

- **Kotlin**: Lenguaje de programación principal.
- **Jetpack Compose**: Framework moderno para construir interfaces de usuario declarativas.
- **CameraX**: Biblioteca de Android para la integración de cámaras, proporcionando una configuración simplificada y control avanzado de la cámara.
- **Material Design 3**: Diseño visual coherente y accesible con `MaterialTheme`.

## 📲 Instalación y Uso

### Requisitos Previos

- Android Studio Electric Eel o superior.
- Un dispositivo o emulador con Android 5.0 (Lollipop, API 21) o superior.

### Pasos de Instalación

1. Clona este repositorio en tu máquina local:

   ```bash
   git clone https://github.com/tu_usuario/VideoFotoApp.git
   ```

2. Abre el proyecto en **Android Studio**.
3. Sincroniza el proyecto para descargar todas las dependencias.
4. Conecta un dispositivo físico o ejecuta un emulador.
5. Ejecuta la aplicación.

## 📸 Capturas de Pantalla

**(Incluye capturas de pantalla de la aplicación en acción: una para captura de foto, una para grabación de video y otra mostrando los controles de cámara y flash)**

## ⚙️ Estructura del Proyecto

- **`ui/screens`**: Contiene las pantallas principales de la aplicación (`PhotoScreen` y `VideoScreen`), que manejan la vista previa de la cámara y los controles para captura de fotos y video.
- **`ui/components`**: Contiene componentes reutilizables como `PhotoControls` y `VideoControls`, que ofrecen opciones de control sobre la cámara (cambio de cámara, captura de fotos, activación del flash, etc.).
- **`CameraViewModel`**: Un `ViewModel` que gestiona el estado de la cámara, incluido el modo de flash y el almacenamiento de fotos/videos.



Videodemo: [clic aqui](https://ufgedu-my.sharepoint.com/personal/ia_marvind_ufg_edu_sv/_layouts/15/stream.aspx?id=%2Fpersonal%2Fia%5Fmarvind%5Fufg%5Fedu%5Fsv%2FDocuments%2Ffotovideoapp%2Emp4&nav=eyJyZWZlcnJhbEluZm8iOnsicmVmZXJyYWxBcHAiOiJPbmVEcml2ZUZvckJ1c2luZXNzIiwicmVmZXJyYWxBcHBQbGF0Zm9ybSI6IldlYiIsInJlZmVycmFsTW9kZSI6InZpZXciLCJyZWZlcnJhbFZpZXciOiJNeUZpbGVzTGlua0NvcHkifX0&ga=1&referrer=StreamWebApp%2EWeb&referrerScenario=AddressBarCopied%2Eview%2E032bdf22%2D6ede%2D4dd9%2Dac27%2D086b155a7a94)
