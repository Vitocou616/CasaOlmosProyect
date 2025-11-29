# TiendaApp — Entrega Parcial 2

Proyecto Android (Kotlin + Jetpack Compose)

Características implementadas:
- Diseño visual con Material 3 (Compose Material3 theme)
- Formularios validados (pantalla de Checkout)
- Navegación funcional (Navigation Compose: home -> product -> cart -> checkout)
- Gestión de estado (ViewModel + StateFlow)
- Almacenamiento local (Room para items del carrito)
- Uso de recursos nativos (vibración y compartir intent al finalizar compra)
- Animaciones sencillas (animateContentSize en tarjetas)
- Base de datos SQLite/Room pre-poblada con productos y un usuario demo
- Registro de usuarios (pantalla de registro y persistencia en Room)
- Búsqueda/filtrado de productos en la pantalla Home
- Asset vectorial incluido (`res/drawable/ic_shop.xml`)
- Login y sesiones: pantalla de login, sesión persistente en SharedPreferences
- Almacenamiento seguro de contraseñas: PBKDF2 (Password stored as salted hash using PBKDF2WithHmacSHA256)

Usuario demo pre-creado:
- Email: demo@example.com
- Password: demo123

Cómo abrir:
1. Abre Android Studio.
2. Selecciona "Open" y elige esta carpeta: C:\\Users\\cachu\\Desktop\\Duoc\\App Moviles TwT
3. Android Studio puede pedir descargar Gradle/SDK; acepta para sincronizar.

Archivos importantes:
- `app/build.gradle` — configuración del módulo de la app.
- `app/src/main/java/com/example/tiendaapp` — código fuente (Activity, ViewModel, data, UI).
- `app/src/main/res` — recursos mínimos.
