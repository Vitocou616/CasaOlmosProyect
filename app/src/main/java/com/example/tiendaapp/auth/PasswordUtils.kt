package com.example.tiendaapp.auth

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordUtils {
    private const val ITERATIONS = 65536
    private const val KEY_LENGTH = 256 // bits

    // Format: iterations:saltBase64:hashBase64
    fun hashPassword(password: CharArray): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = skf.generateSecret(spec).encoded
        return "$ITERATIONS:${Base64.encodeToString(salt, Base64.NO_WRAP)}:${Base64.encodeToString(hash, Base64.NO_WRAP)}"
    }

    fun verifyPassword(password: CharArray, stored: String): Boolean {
        val parts = stored.split(":")
        if (parts.size != 3) return false
        val iterations = parts[0].toInt()
        val salt = Base64.decode(parts[1], Base64.NO_WRAP)
        val hash = Base64.decode(parts[2], Base64.NO_WRAP)

        val spec = PBEKeySpec(password, salt, iterations, hash.size * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val testHash = skf.generateSecret(spec).encoded
        // constant-time compare
        if (testHash.size != hash.size) return false
        var diff = 0
        for (i in testHash.indices) diff = diff or (testHash[i].toInt() xor hash[i].toInt())
        return diff == 0
    }
}
