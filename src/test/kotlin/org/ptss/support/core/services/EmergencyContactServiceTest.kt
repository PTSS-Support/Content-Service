package org.ptss.support.core.services

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.quarkus.test.junit.QuarkusTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.ptss.support.api.dtos.requests.emergencycontact.UpdateEmergencyContactRequest
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.commands.emergencycontact.UpdateEmergencyContactCommand
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.domain.models.EmergencyContact
import org.ptss.support.domain.queries.emergencycontact.GetAllEmergencyContactsQuery
import org.ptss.support.infrastructure.handlers.queries.emergencycontact.GetAllEmergencyContactsQueryHandler
import java.util.UUID

@QuarkusTest
class EmergencyContactServiceTest {
    private lateinit var service: EmergencyContactService
    private lateinit var getAllEmergencyContactsHandler: GetAllEmergencyContactsQueryHandler
    private lateinit var updateEmergencyContactsHandler: ICommandHandler<UpdateEmergencyContactCommand, EmergencyContact>

    private val mockUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

    @BeforeEach
    fun setup() {
        getAllEmergencyContactsHandler = mockk()
        updateEmergencyContactsHandler = mockk()
        service = EmergencyContactService(
            getAllEmergencyContactsHandler,
            updateEmergencyContactsHandler
        )
    }

    @Test
    fun `getAllEmergencyContactsAsync returns contacts successfully`() = runTest {
        // Arrange
        val mockContacts = listOf(
            EmergencyContact(
                id = mockUUID,
                name = "John Doe",
                phoneNumber = "+1234567890",
                actionLabel = "Call Emergency"
            )
        )
        coEvery {
            getAllEmergencyContactsHandler.handleAsync(any())
        } returns mockContacts

        // Act
        val result = service.getAllEmergencyContactsAsync()

        // Assert
        assertEquals(mockContacts, result)
        coVerify { getAllEmergencyContactsHandler.handleAsync(match { true }) }
    }

    @Test
    fun `getAllEmergencyContactsAsync handles errors properly`() = runTest {
        // Arrange
        coEvery {
            getAllEmergencyContactsHandler.handleAsync(any())
        } throws RuntimeException("Test exception")

        // Act & Assert
        assertThrows<APIException> {
            service.getAllEmergencyContactsAsync()
        }.also { exception ->
            assertEquals(ErrorCode.EMERGENCY_CONTACT_CREATION_ERROR, exception.errorCode)
            assertEquals("Unable to retrieve emergency contacts", exception.message)
        }
    }

    @Test
    fun `updateEmergencyContactAsync updates contact successfully`() = runTest {
        // Arrange
        val request = UpdateEmergencyContactRequest(
            name = "John Doe",
            phoneNumber = "+1234567890",
            actionLabel = "Call Emergency"
        )
        val mockContact = EmergencyContact(
            id = mockUUID,
            name = "John Doe",
            phoneNumber = "+1234567890",
            actionLabel = "Call Emergency"
        )
        coEvery {
            updateEmergencyContactsHandler.handleAsync(any())
        } returns mockContact

        // Act
        val result = service.updateEmergencyContactAsync(mockUUID.toString(), request)

        // Assert
        assertEquals(mockContact, result)
        coVerify {
            updateEmergencyContactsHandler.handleAsync(
                match { command ->
                    command.id == mockUUID.toString() &&
                            command.name == request.name &&
                            command.phoneNumber == request.phoneNumber &&
                            command.actionLabel == request.actionLabel
                }
            )
        }
    }

    @Test
    fun `updateEmergencyContactAsync handles APIException`() = runTest {
        // Arrange
        val request = UpdateEmergencyContactRequest(
            name = "John Doe",
            phoneNumber = "+1234567890",
            actionLabel = "Call Emergency"
        )
        val apiException = APIException(
            errorCode = ErrorCode.EMERGENCY_CONTACT_NOT_FOUND,
            message = "Contact not found"
        )
        coEvery {
            updateEmergencyContactsHandler.handleAsync(any())
        } throws apiException

        // Act & Assert
        val exception = assertThrows<APIException> {
            service.updateEmergencyContactAsync(mockUUID.toString(), request)
        }
        assertEquals(ErrorCode.EMERGENCY_CONTACT_NOT_FOUND, exception.errorCode)
    }
}