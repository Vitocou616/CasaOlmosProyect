package com.example.tiendaapp.data

import org.junit.Test
import org.junit.Assert.*

class UserTest {

    @Test
    fun `user creation with all fields should succeed`() {
        // Arrange & Act
        val user = User(
            id = 1,
            name = "John Doe",
            email = "john@example.com",
            passwordHash = "hashedpassword123"
        )

        // Assert
        assertEquals(1, user.id)
        assertEquals("John Doe", user.name)
        assertEquals("john@example.com", user.email)
        assertEquals("hashedpassword123", user.passwordHash)
    }

    @Test
    fun `user with auto-generated id defaults to zero`() {
        // Arrange & Act
        val user = User(
            name = "John Doe",
            email = "john@example.com",
            passwordHash = "hashedpassword123"
        )

        // Assert
        assertEquals(0, user.id)
    }

    @Test
    fun `user email should be case-sensitive`() {
        // Arrange
        val user1 = User(
            id = 1,
            name = "John Doe",
            email = "John@Example.com",
            passwordHash = "hash"
        )
        val user2 = User(
            id = 2,
            name = "John Doe",
            email = "john@example.com",
            passwordHash = "hash"
        )

        // Assert
        assertNotEquals(user1.email, user2.email)
    }

    @Test
    fun `user with special characters in name should be valid`() {
        // Arrange & Act
        val user = User(
            id = 1,
            name = "José María O'Brien-Smith",
            email = "jose@example.com",
            passwordHash = "hash"
        )

        // Assert
        assertEquals("José María O'Brien-Smith", user.name)
    }

    @Test
    fun `user email with plus sign should be valid`() {
        // Arrange & Act
        val user = User(
            id = 1,
            name = "John Doe",
            email = "john+test@example.com",
            passwordHash = "hash"
        )

        // Assert
        assertEquals("john+test@example.com", user.email)
    }

    @Test
    fun `user equality should work correctly`() {
        // Arrange
        val user1 = User(
            id = 1,
            name = "John Doe",
            email = "john@example.com",
            passwordHash = "hash"
        )
        val user2 = User(
            id = 1,
            name = "John Doe",
            email = "john@example.com",
            passwordHash = "hash"
        )

        // Assert
        assertEquals(user1, user2)
        assertEquals(user1.hashCode(), user2.hashCode())
    }

    @Test
    fun `users with different ids should not be equal`() {
        // Arrange
        val user1 = User(
            id = 1,
            name = "John Doe",
            email = "john@example.com",
            passwordHash = "hash"
        )
        val user2 = User(
            id = 2,
            name = "John Doe",
            email = "john@example.com",
            passwordHash = "hash"
        )

        // Assert
        assertNotEquals(user1, user2)
    }

    @Test
    fun `users with different emails should not be equal`() {
        // Arrange
        val user1 = User(
            id = 1,
            name = "John Doe",
            email = "john@example.com",
            passwordHash = "hash"
        )
        val user2 = User(
            id = 1,
            name = "John Doe",
            email = "jane@example.com",
            passwordHash = "hash"
        )

        // Assert
        assertNotEquals(user1, user2)
    }

    @Test
    fun `users with different password hashes should not be equal`() {
        // Arrange
        val user1 = User(
            id = 1,
            name = "John Doe",
            email = "john@example.com",
            passwordHash = "hash1"
        )
        val user2 = User(
            id = 1,
            name = "John Doe",
            email = "john@example.com",
            passwordHash = "hash2"
        )

        // Assert
        assertNotEquals(user1, user2)
    }

    @Test
    fun `user with long name should be valid`() {
        // Arrange & Act
        val longName = "A".repeat(100)
        val user = User(
            id = 1,
            name = longName,
            email = "test@example.com",
            passwordHash = "hash"
        )

        // Assert
        assertEquals(longName, user.name)
        assertEquals(100, user.name.length)
    }
}
