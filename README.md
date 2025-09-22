# DistribuidoraApp - Semana 6

## 📖 Descripción
Aplicación Android desarrollada como parte de la asignatura **PRO303 - Programación de Aplicaciones Móviles (AIEP)**.  
El proyecto corresponde a la **actividad de la semana 6**, donde se integran diferentes funcionalidades en una app móvil para simular el proceso de cálculo de despacho de productos con control de cadena de frío.

---

## 🚀 Funcionalidades implementadas
- **Login y Registro de Usuarios** con Firebase Authentication (correo y contraseña).
- **Obtención de ubicación GPS** mediante `LocationManager`.
- **Cálculo de distancia** entre el cliente y la bodega usando la **fórmula de Haversine**.
- **Cálculo del costo de despacho** en base al monto de compra y la distancia.
- **Control de temperatura simulado**, que verifica si se mantiene la cadena de frío.
- **Interfaz gráfica en XML** para login y cálculo de despacho.
- **Mensajes de depuración en Logcat** para validar cálculos y ubicación.

---

## 🛠️ Tecnologías utilizadas
- **Lenguaje**: Java
- **Entorno**: Android Studio
- **Servicios**: Firebase Authentication
- **SDK objetivo**: Android 12+
- **Diseño UI**: XML Layouts, Material Components

---

## 📷 Evidencias de funcionamiento
- Pantalla de Login con opciones de registro e inicio de sesión.
- Registro exitoso de usuario en Firebase.
- Inicio de sesión correcto y acceso a `MainActivity`.
- Pantalla de cálculo de despacho mostrando:
  - Monto de compra
  - Coordenadas del cliente
  - Distancia calculada
  - Costo de despacho
  - Estado de cadena de frío
- Registros en Logcat confirmando cálculos y ubicación.

*(Las capturas de pantalla se adjuntan en el informe entregado en paralelo a este repositorio).*

---

## 📂 Estructura del proyecto
```
DistribuidoraApp/
├── app/
│   ├── src/main/java/com/tuempresa/distribuidora/
│   │   ├── LoginActivity.java
│   │   ├── MainActivity.java
│   │   ├── Haversine.java
│   │   ├── CalculoDespacho.java
│   │   └── ControlTemperatura.java
│   ├── res/
│   │   ├── layout/activity_login.xml
│   │   ├── layout/activity_main.xml
│   │   └── values/colors.xml
│   └── AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 👨‍🎓 Autor
**Leonardo Sepúlveda Veas**  
Alumno de AIEP - Asignatura PRO303  

---

## 📚 Referencias
- AIEP (2025). Apuntes de la semana 6.  
- Android Developers. (s.f.). [LocationManager](https://developer.android.com/reference/android/location/LocationManager).  
- Firebase Authentication. [Documentación oficial](https://firebase.google.com/docs/auth).  
- OpenStreetMap. (s.f.). [Geographic coordinates](https://www.openstreetmap.org/).  
