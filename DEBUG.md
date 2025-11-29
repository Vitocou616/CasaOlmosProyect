# DEBUG - Resumen de soluciones aplicadas

## Problemas encontrados y solucionados

### 1. ❌ La app se detiene inesperadamente (CRASH)

**Causas identificadas:**

#### A) Falta permiso de Internet
```xml
<!-- Faltaba en AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
```

#### B) Conflicto de esquema Room Database
- Añadimos campos a `Product` pero BD seguía en versión 1
- Necesaria migración de v1 a v2 con `ALTER TABLE`

#### C) Constructor antiguo en pre-populate
- `AppDatabase.kt` insertaba `Product(1, "Cafetera", 49.99)` 
- Debería ser: `Product(1, "Cafetera", ..., 49.99, 0, "https://...")`

---

## Soluciones aplicadas ✅

### Cambio 1: Permiso INTERNET
**Archivo:** `app/src/main/AndroidManifest.xml`
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.tiendaapp">

    <!-- ✅ Añadido -->
    <uses-permission android:name="android.permission.INTERNET" />

    <application>...</application>
</manifest>
```

### Cambio 2: Migración Room Database
**Archivo:** `app/src/main/java/com/example/tiendaapp/data/AppDatabase.kt`

```kotlin
@Database(entities = [CartItem::class, Product::class, User::class], version = 2)  // ✅ v1→v2
abstract class AppDatabase : RoomDatabase() {
    
    companion object {
        // ✅ Migración: añade columnas imageRes e imageUrl
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE products ADD COLUMN imageRes INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE products ADD COLUMN imageUrl TEXT")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(...)
                    .addMigrations(MIGRATION_1_2)  // ✅ Registrar migración
                    .build()
                // ...
                // Pre-populate con URLs
                val sample = listOf(
                    Product(1, "Cafetera", "Cafetera de goteo compacta", 49.99, 0, 
                        "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=400&h=300&fit=crop"),
                    // ...
                )
                dao.insertAll(sample)
            }
        }
    }
}
```

---

## Estado de compilación

```
BUILD SUCCESSFUL in 32s
35 actionable tasks: 35 executed
```

✅ Sin errores.

---

## Cómo instalar y probar ahora

### Opción 1: Desde Android Studio
1. Sync Gradle (File → Sync Now)
2. Run → Run 'app' (o presiona Shift+F10)
3. Selecciona dispositivo/emulador
4. La app debería abrir sin crashes

### Opción 2: Desde PowerShell

```powershell
# Ir a la raíz del proyecto
Set-Location -LiteralPath 'C:\Users\cachu\Desktop\Duoc\App Moviles TwT'

# Limpiar caché de BD anterior (importante!)
adb shell pm clear com.example.tiendaapp

# Compilar e instalar
.\gradlew.bat :app:installDebug

# Ver logs en tiempo real
adb logcat
```

---

## Si aún hay crashes

### Ver logs detallados
```powershell
adb logcat | findstr "tiendaapp"
```

Busca líneas con `Exception` o `Error`.

### Causas comunes
- **"Permission denied"** → Falta permiso (revisar `AndroidManifest.xml`)
- **"Cannot access database file"** → Reinstalar limpio: `adb uninstall com.example.tiendaapp`
- **"NullPointerException en Coil"** → Verificar que haya conexión Internet en el dispositivo

---

## Verificación rápida

✅ Permiso INTERNET agregado  
✅ Migración Room v1→v2 implementada  
✅ Pre-populate con URLs actualizado  
✅ Compilación exitosa  

**Próximo paso:** Instalar en dispositivo y verificar que la app abra sin crashes.
