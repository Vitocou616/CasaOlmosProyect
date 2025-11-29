package com.example.tiendaapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [CartItem::class, Product::class, User::class, Order::class, OrderItem::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migración de v1 a v2: añadir columnas imageRes e imageUrl
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Añadir columna imageRes con valor por defecto 0
                database.execSQL("ALTER TABLE products ADD COLUMN imageRes INTEGER NOT NULL DEFAULT 0")
                // Añadir columna imageUrl con valor por defecto null
                database.execSQL("ALTER TABLE products ADD COLUMN imageUrl TEXT")
            }
        }

        // Migración de v2 a v3: actualizar precios
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Actualizar precios a los nuevos valores
                database.execSQL("UPDATE products SET price = 49.99 WHERE id = 1")
                database.execSQL("UPDATE products SET price = 79.99 WHERE id = 2")
                database.execSQL("UPDATE products SET price = 39.99 WHERE id = 3")
                database.execSQL("UPDATE products SET price = 12.99 WHERE id = 4")
            }
        }

        // Migración de v3 a v4: actualizar URLs de imágenes
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Actualizar URLs de imágenes para cada producto
                database.execSQL("UPDATE products SET imageUrl = 'https://http2.mlstatic.com/D_NQ_NP_975534-MLC91531679389_092025-O-cafetera-moka-italiana-manual-generica-3-tazas-negra.webp' WHERE id = 1")
                database.execSQL("UPDATE products SET imageUrl = 'https://m.media-amazon.com/images/I/51Fl+3ERtDL._AC_SL1200_.jpg' WHERE id = 2")
                database.execSQL("UPDATE products SET imageUrl = 'https://http2.mlstatic.com/D_NQ_NP_638962-MLC81893450089_012025-O.webp' WHERE id = 3")
                database.execSQL("UPDATE products SET imageUrl = 'https://i.ebayimg.com/images/g/5AUAAOSwmCFjF58k/s-l1600.webp' WHERE id = 4")
            }
        }

        // Migración de v4 a v5: crear tablas de órdenes
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS orders (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, userId INTEGER NOT NULL, total REAL NOT NULL, createdAt INTEGER NOT NULL)")
                database.execSQL("CREATE TABLE IF NOT EXISTS order_items (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, orderId INTEGER NOT NULL, productName TEXT NOT NULL, price REAL NOT NULL, quantity INTEGER NOT NULL)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "tienda-db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = inst

                // Pre-populate DB if empty
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    try {
                        val dao = inst.productDao()
                        if (dao.getCount() == 0) {
                            val sample = listOf(
                                Product(1, "Cafetera", "Cafetera de goteo compacta", 49.99, 0, null),
                                Product(2, "Auriculares", "In-ear con cancelación de ruido", 79.99, 0, null),
                                Product(3, "Mochila", "Mochila urbana 20L", 39.99, 0, null),
                                Product(4, "Taza térmica", "Taza para café de viaje 350ml", 12.99, 0, null)
                            )
                            dao.insertAll(sample)
                        }

                        val userDao = inst.userDao()
                        if (userDao.count() == 0) {
                            // Pre-create a demo user (password hashed using PasswordUtils)
                            val demoHash = com.example.tiendaapp.auth.PasswordUtils.hashPassword("demo123".toCharArray())
                            userDao.insert(User(name = "Demo User", email = "demo@example.com", passwordHash = demoHash))
                        }
                    } catch (e: Exception) {
                        // ignore pre-populate errors here; app will still work
                    }
                }

                inst
            }
        }
    }
}
