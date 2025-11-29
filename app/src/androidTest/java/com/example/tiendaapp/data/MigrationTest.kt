package com.example.tiendaapp.data

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        // Create database with version 1
        var db = helper.createDatabase(TEST_DB, 1).apply {
            // Insert test data into v1 schema
            execSQL(
                """
                INSERT INTO products (id, name, description, price) 
                VALUES (1, 'Test Product', 'Test Description', 10.0)
                """.trimIndent()
            )
            close()
        }

        // Re-open the database with version 2 and provide MIGRATION_1_2 as the migration process
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true)

        // Verify that data is preserved and new columns exist
        val cursor = db.query("SELECT * FROM products WHERE id = 1")
        cursor.moveToFirst()
        
        // Check that new columns were added
        val imageResIndex = cursor.getColumnIndex("imageRes")
        val imageUrlIndex = cursor.getColumnIndex("imageUrl")
        
        assert(imageResIndex >= 0) { "imageRes column should exist" }
        assert(imageUrlIndex >= 0) { "imageUrl column should exist" }
        
        // Check default values
        assert(cursor.getInt(imageResIndex) == 0) { "imageRes should default to 0" }
        assert(cursor.isNull(imageUrlIndex)) { "imageUrl should default to null" }
        
        cursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        // Create database with version 2
        var db = helper.createDatabase(TEST_DB, 2).apply {
            // Insert test data with old prices
            execSQL(
                """
                INSERT INTO products (id, name, description, price, imageRes, imageUrl) 
                VALUES (1, 'Cafetera', 'Test', 50.0, 0, NULL)
                """.trimIndent()
            )
            execSQL(
                """
                INSERT INTO products (id, name, description, price, imageRes, imageUrl) 
                VALUES (2, 'Auriculares', 'Test', 80.0, 0, NULL)
                """.trimIndent()
            )
            close()
        }

        // Migrate to version 3
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true)

        // Verify that prices were updated
        val cursor = db.query("SELECT id, price FROM products ORDER BY id")
        
        cursor.moveToFirst()
        assert(cursor.getDouble(cursor.getColumnIndex("price")) == 49.99) { "Cafetera price should be 49.99" }
        
        cursor.moveToNext()
        assert(cursor.getDouble(cursor.getColumnIndex("price")) == 79.99) { "Auriculares price should be 79.99" }
        
        cursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrate3To4() {
        // Create database with version 3
        var db = helper.createDatabase(TEST_DB, 3).apply {
            // Insert test data without image URLs
            execSQL(
                """
                INSERT INTO products (id, name, description, price, imageRes, imageUrl) 
                VALUES (1, 'Cafetera', 'Test', 49.99, 0, NULL)
                """.trimIndent()
            )
            close()
        }

        // Migrate to version 4
        db = helper.runMigrationsAndValidate(TEST_DB, 4, true)

        // Verify that imageUrl was updated
        val cursor = db.query("SELECT imageUrl FROM products WHERE id = 1")
        cursor.moveToFirst()
        
        val imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"))
        assert(imageUrl != null) { "imageUrl should not be null after migration" }
        assert(imageUrl.startsWith("https://")) { "imageUrl should be a valid URL" }
        
        cursor.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrateAll1To4() {
        // Create database with version 1
        var db = helper.createDatabase(TEST_DB, 1).apply {
            execSQL(
                """
                INSERT INTO products (id, name, description, price) 
                VALUES (1, 'Test Product', 'Test Description', 10.0)
                """.trimIndent()
            )
            close()
        }

        // Run all migrations 1 -> 2 -> 3 -> 4
        db = helper.runMigrationsAndValidate(TEST_DB, 4, true)

        // Verify final state
        val cursor = db.query("SELECT * FROM products WHERE id = 1")
        cursor.moveToFirst()
        
        // All columns should exist
        assert(cursor.getColumnIndex("imageRes") >= 0)
        assert(cursor.getColumnIndex("imageUrl") >= 0)
        
        // Price should be updated (since we inserted id=1, it should be 49.99)
        val price = cursor.getDouble(cursor.getColumnIndex("price"))
        assert(price == 49.99) { "Price should be updated to 49.99 for id=1" }
        
        cursor.close()
    }
}
