package org.ptss.support.core.controllers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.MockKAnnotations
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.ptss.support.api.controllers.EmergencyContactController
import org.ptss.support.api.dtos.requests.emergencycontact.UpdateEmergencyContactRequest
import org.ptss.support.api.dtos.responses.emergencycontact.EmergencyContactResponse
import org.ptss.support.core.facades.EmergencyContactFacade
import java.util.UUID

@QuarkusTest
class EmergencyContactControllerTest {
    @InjectMock
    private lateinit var emergencyContactFacade: EmergencyContactFacade

    @Inject
    private lateinit var controller: EmergencyContactController

    private val mockUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getAllEmergencyContacts returns list successfully`() = runTest {
        // Arrange
        val mockResponse = listOf(
            EmergencyContactResponse(
                id = mockUUID,
                name = "John Doe",
                phoneNumber = "+1234567890",
                actionLabel = "Call Emergency"
            )
        )
        coEvery {
            emergencyContactFacade.getAllEmergencyContacts()
        } returns mockResponse

        // Act
        val result = controller.getAllEmergencyContacts()

        // Assert
        assertEquals(mockResponse, result)
        coVerify { emergencyContactFacade.getAllEmergencyContacts() }
    }

    @Test
    fun `getAllEmergencyContacts returns empty list`() = runTest {
        // Arrange
        coEvery { emergencyContactFacade.getAllEmergencyContacts() } returns emptyList()

        // Act
        val result = controller.getAllEmergencyContacts()

        // Assert
        assertTrue(result.isEmpty())
        coVerify { emergencyContactFacade.getAllEmergencyContacts() }
    }

    @Test
    fun `getAllEmergencyContacts throws internal server error`() = runTest {
        // Arrange
        coEvery { emergencyContactFacade.getAllEmergencyContacts() } throws RuntimeException("Database error")

        // Act & Assert
        val exception = assertThrows<RuntimeException> {
            controller.getAllEmergencyContacts()
        }
        assertEquals("Database error", exception.message)
        coVerify { emergencyContactFacade.getAllEmergencyContacts() }
    }

    @Test
    fun `getAllEmergencyContacts handles large list`() = runTest {
        // Arrange
        val largeResponse = List(1000) {
            EmergencyContactResponse(
                id = UUID.randomUUID(),
                name = "Contact $it",
                phoneNumber = "+1234567$it",
                actionLabel = "Label $it"
            )
        }
        coEvery { emergencyContactFacade.getAllEmergencyContacts() } returns largeResponse

        // Act
        val result = controller.getAllEmergencyContacts()

        // Assert
        assertEquals(1000, result.size)
        assertEquals(largeResponse, result)
        coVerify { emergencyContactFacade.getAllEmergencyContacts() }
    }

    @Test
    fun `updateEmergencyContact updates contact successfully`() = runTest {
        // Arrange
        val request = UpdateEmergencyContactRequest(
            name = "John Doe",
            phoneNumber = "+1234567890",
            actionLabel = "Call Emergency"
        )
        val mockResponse = EmergencyContactResponse(
            id = mockUUID,
            name = "John Doe",
            phoneNumber = "+1234567890",
            actionLabel = "Call Emergency"
        )
        coEvery {
            emergencyContactFacade.updateEmergencyContact(any(), any())
        } returns mockResponse

        // Act
        val result = controller.updateEmergencyContact(mockUUID.toString(), request)

        // Assert
        assertEquals(mockResponse, result)
        coVerify { emergencyContactFacade.updateEmergencyContact(mockUUID.toString(), request) }
    }

    @Test
    fun `updateEmergencyContact throws exception for invalid UUID`() = runTest {
        // Arrange
        val invalidUUID = "invalid-uuid"
        val request = UpdateEmergencyContactRequest(
            name = "John Doe",
            phoneNumber = "+1234567890",
            actionLabel = "Call Emergency"
        )
        coEvery {
            emergencyContactFacade.updateEmergencyContact(any(), any())
        } throws IllegalArgumentException("Invalid UUID string: $invalidUUID")

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> {
            controller.updateEmergencyContact(invalidUUID, request)
        }
        assertEquals("Invalid UUID string: $invalidUUID", exception.message)
        coVerify { emergencyContactFacade.updateEmergencyContact(invalidUUID, request) }
    }

    @Test
    fun `updateEmergencyContact throws not found exception for non-existent ID`() = runTest {
        // Arrange
        val nonExistentUUID = UUID.randomUUID().toString()
        val request = UpdateEmergencyContactRequest(
            name = "Jane Doe",
            phoneNumber = "+9876543210",
            actionLabel = "Emergency Contact"
        )
        coEvery {
            emergencyContactFacade.updateEmergencyContact(any(), any())
        } throws NoSuchElementException("Emergency contact not found")

        // Act & Assert
        val exception = assertThrows<NoSuchElementException> {
            controller.updateEmergencyContact(nonExistentUUID, request)
        }
        assertEquals("Emergency contact not found", exception.message)
        coVerify { emergencyContactFacade.updateEmergencyContact(nonExistentUUID, request) }
    }

    @Test
    fun `updateEmergencyContact throws exception for empty UUID`() = runTest {
        // Arrange
        val emptyUUID = ""
        val request = UpdateEmergencyContactRequest(
            name = "John Doe",
            phoneNumber = "+1234567890",
            actionLabel = "Call Emergency"
        )
        coEvery {
            emergencyContactFacade.updateEmergencyContact(any(), any())
        } throws IllegalArgumentException("Invalid UUID string")

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> {
            controller.updateEmergencyContact(emptyUUID, request)
        }
        assertEquals("Invalid UUID string", exception.message)
        coVerify { emergencyContactFacade.updateEmergencyContact(emptyUUID, request) }
    }

    @Test
    fun `updateEmergencyContact handles special characters in fields`() = runTest {
        // Arrange
        val request = UpdateEmergencyContactRequest(
            name = "John Dœ✨",
            phoneNumber = "+1234567890",
            actionLabel = "🚨 Emergency!"
        )
        val mockResponse = EmergencyContactResponse(
            id = mockUUID,
            name = "John Dœ✨",
            phoneNumber = "+1234567890",
            actionLabel = "🚨 Emergency!"
        )
        coEvery {
            emergencyContactFacade.updateEmergencyContact(any(), any())
        } returns mockResponse

        // Act
        val result = controller.updateEmergencyContact(mockUUID.toString(), request)

        // Assert
        assertEquals(mockResponse, result)
        coVerify { emergencyContactFacade.updateEmergencyContact(mockUUID.toString(), request) }
    }
}