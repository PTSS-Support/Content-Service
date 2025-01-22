package org.ptss.support.core.controllers

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.MockKAnnotations
import io.mockk.runs
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.ptss.support.api.controllers.GeneralInformationController
import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.generalinformation.CreateGeneralInformationResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationListItemResponse
import org.ptss.support.api.dtos.responses.media.MediaResponse
import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.core.facades.GeneralInformationFacade
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.TimeoutException

@QuarkusTest
class GeneralInformationControllerTest {
    @InjectMock
    private lateinit var generalInformationFacade: GeneralInformationFacade

    @Inject
    private lateinit var controller: GeneralInformationController

    private val mockUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `getAllGeneralInformation returns paged result successfully`() = runTest {
        // Arrange
        val mockPagedResult = PagedResult(
            data = listOf(
                GeneralInformationListItemResponse(mockUUID, "Test Title")
            ),
            nextCursor = "nextCursor",
            pageSize = 10,
            totalItems = 1,
            totalPages = 1
        )
        coEvery {
            generalInformationFacade.getAllGeneralInformation(any(), any())
        } returns mockPagedResult

        // Act
        val result = controller.getAllGeneralInformation("cursor", 10)

        // Assert
        assertEquals(mockPagedResult, result)
        coVerify { generalInformationFacade.getAllGeneralInformation("cursor", 10) }
    }

    @Test
    fun `getAllGeneralInformation returns empty list successfully`() = runTest {
        // Arrange
        val mockPagedResult = PagedResult(
            data = emptyList<GeneralInformationListItemResponse>(),
            nextCursor = null,
            pageSize = 10,
            totalItems = 0,
            totalPages = 0
        )
        coEvery { generalInformationFacade.getAllGeneralInformation(any(), any()) } returns mockPagedResult

        // Act
        val result = controller.getAllGeneralInformation("cursor", 10)

        // Assert
        assertEquals(mockPagedResult, result)
        coVerify { generalInformationFacade.getAllGeneralInformation("cursor", 10) }
    }

    @Test
    fun `getAllGeneralInformation handles invalid cursor gracefully`() = runTest {
        // Arrange
        coEvery { generalInformationFacade.getAllGeneralInformation(any(), any()) } throws IllegalArgumentException("Invalid cursor")

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            controller.getAllGeneralInformation("invalid_cursor", 10)
        }
    }

    @Test
    fun `getAllGeneralInformation handles service timeout gracefully`() = runTest {
        // Arrange
        coEvery { generalInformationFacade.getAllGeneralInformation(any(), any()) } throws TimeoutException("Service timeout")

        // Act & Assert
        assertThrows<TimeoutException> {
            controller.getAllGeneralInformation("cursor", 10)
        }
        coVerify { generalInformationFacade.getAllGeneralInformation("cursor", 10) }
    }

    @Test
    fun `getAllGeneralInformation throws error when pageSize exceeds the limit`() = runTest {
        // Arrange
        val invalidPageSize = 100 // Exceeds the page size limit
        val expectedExceptionMessage = "pageSize: must be less than or equal to 50"

        // Act & Assert
        val exception = assertThrows<ResteasyReactiveViolationException> {
            controller.getAllGeneralInformation("cursor", invalidPageSize)
        }

        // Verify that the exception message contains the expected message
        assertTrue(exception.message?.contains(expectedExceptionMessage) == true)

        // Ensure the facade is not called
        coVerify(exactly = 0) { generalInformationFacade.getAllGeneralInformation(any(), any()) }
    }

    @Test
    fun `getAllGeneralInformation handles maximum valid pageSize successfully`() = runTest {
        // Arrange
        val maxPageSize = 50
        val data = List(maxPageSize) {
            GeneralInformationListItemResponse(
                id = UUID.randomUUID(),
                title = "Title $it"
            )
        }
        val response = PagedResult(
            data = data,
            nextCursor = "next-cursor",
            pageSize = maxPageSize,
            totalItems = maxPageSize,
            totalPages = 1
        )
        coEvery { generalInformationFacade.getAllGeneralInformation(any(), any()) } returns response

        // Act
        val result = controller.getAllGeneralInformation("cursor", maxPageSize)

        // Assert
        assertEquals(response, result)
        coVerify { generalInformationFacade.getAllGeneralInformation("cursor", maxPageSize) }
    }

    @Test
    fun `getAllGeneralInformation handles minimum valid pageSize successfully`() = runTest {
        // Arrange
        val minPageSize = 1
        val data = List(minPageSize) {
            GeneralInformationListItemResponse(
                id = UUID.randomUUID(),
                title = "Title $it"
            )
        }
        val response = PagedResult(
            data = data,
            nextCursor = "next-cursor",
            pageSize = minPageSize,
            totalItems = minPageSize,
            totalPages = 1
        )
        coEvery { generalInformationFacade.getAllGeneralInformation(any(), any()) } returns response

        // Act
        val result = controller.getAllGeneralInformation("cursor", minPageSize)

        // Assert
        assertEquals(response, result)
        coVerify { generalInformationFacade.getAllGeneralInformation("cursor", minPageSize) }
    }

    @Test
    fun `getAllGeneralInformation handles null cursor gracefully`() = runTest {
        // Arrange
        val data = List(10) {
            GeneralInformationListItemResponse(
                id = UUID.randomUUID(),
                title = "Title $it"
            )
        }
        val response = PagedResult(
            data = data,
            nextCursor = "next-cursor",
            pageSize = 10,
            totalItems = 10,
            totalPages = 1
        )
        coEvery { generalInformationFacade.getAllGeneralInformation(null, 10) } returns response

        // Act
        val result = controller.getAllGeneralInformation(null, 10)

        // Assert
        assertEquals(response, result)
        coVerify { generalInformationFacade.getAllGeneralInformation(null, 10) }
    }

    @Test
    fun `getAllGeneralInformation works when cursor is empty string`() = runTest {
        // Arrange
        val data = List(10) {
            GeneralInformationListItemResponse(
                id = UUID.randomUUID(),
                title = "Title $it"
            )
        }
        val response = PagedResult(
            data = data,
            nextCursor = "next-cursor",
            pageSize = 10,
            totalItems = 10,
            totalPages = 1
        )
        coEvery { generalInformationFacade.getAllGeneralInformation("", 10) } returns response

        // Act
        val result = controller.getAllGeneralInformation("", 10)

        // Assert
        assertEquals(response, result)
        coVerify { generalInformationFacade.getAllGeneralInformation("", 10) }
    }

    @Test
    fun `getGeneralInformationById returns null when not found`() = runTest {
        // Arrange
        coEvery {
            generalInformationFacade.getGeneralInformationById(any())
        } returns null

        // Act
        val result = controller.getGeneralInformationById(mockUUID.toString())

        // Assert
        assertEquals(null, result)
        coVerify { generalInformationFacade.getGeneralInformationById(mockUUID.toString()) }
    }

    @Test
    fun `getGeneralInformationById throws 403 for unauthorized access`() = runTest {
        // Arrange
        coEvery { generalInformationFacade.getGeneralInformationById(any()) } throws SecurityException("Forbidden")

        // Act & Assert
        assertThrows<SecurityException> {
            controller.getGeneralInformationById(mockUUID.toString())
        }
        coVerify { generalInformationFacade.getGeneralInformationById(mockUUID.toString()) }
    }

    @Test
    fun `createGeneralInformation creates successfully`() = runTest {
        // Arrange
        val request = CreateGeneralInformationRequest(
            title = "Test Title",
            content = "Test Content"
        )
        val expectedResponse = CreateGeneralInformationResponse(
            id = mockUUID,
            title = "Test Title",
            content = "Test Content"
        )
        coEvery {
            generalInformationFacade.createGeneralInformation(request)
        } returns expectedResponse

        // Act
        val result = controller.createGeneralInformation(request)

        // Assert
        assertEquals(expectedResponse, result)
        coVerify { generalInformationFacade.createGeneralInformation(request) }
    }

    @Test
    fun `createGeneralInformation throws error for duplicate request`() = runTest {
        // Arrange
        val duplicateRequest = CreateGeneralInformationRequest(
            title = "Duplicate Title",
            content = "Duplicate Content"
        )
        coEvery { generalInformationFacade.createGeneralInformation(duplicateRequest) } throws IllegalStateException("Duplicate entry")

        // Act & Assert
        assertThrows<IllegalStateException> {
            controller.createGeneralInformation(duplicateRequest)
        }
        coVerify { generalInformationFacade.createGeneralInformation(duplicateRequest) }
    }

    @Test
    fun `createGeneralInformationMedia throws error for invalid media file`() = runTest {
        // Arrange
        val mockInputStream = mockk<InputStream>(relaxed = true)
        coEvery {
            generalInformationFacade.createGeneralInformationMedia(any(), any())
        } throws IllegalArgumentException("Invalid file type or size")

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            controller.createGeneralInformationMedia(
                mockUUID.toString(),
                mockInputStream,
                "test-href"
            )
        }
        coVerify {
            generalInformationFacade.createGeneralInformationMedia(
                mockUUID.toString(),
                match { it.media == mockInputStream && it.href == "test-href" }
            )
        }
    }

    @Test
    fun `deleteGeneralInformation returns no content response`() = runTest {
        // Arrange
        coEvery {
            generalInformationFacade.deleteGeneralInformation(any())
        } just runs

        // Act
        val result = controller.deleteGeneralInformation(mockUUID.toString())

        // Assert
        assertEquals(Response.Status.NO_CONTENT.statusCode, result.status)
        coVerify { generalInformationFacade.deleteGeneralInformation(mockUUID.toString()) }
    }

    @Test
    fun `createGeneralInformationMedia creates media successfully`() = runTest {
        // Arrange
        val mockInputStream = mockk<InputStream>(relaxed = true)
        val expectedResponse = MediaResponse(
            id = mockUUID,
            url = "test-url",
            href = "https://www.youtube.com/"
        )
        coEvery {
            generalInformationFacade.createGeneralInformationMedia(any(), any())
        } returns expectedResponse

        // Act
        val result = controller.createGeneralInformationMedia(
            mockUUID.toString(),
            mockInputStream,
            "test-href"
        )

        // Assert
        assertEquals(expectedResponse, result)
        coVerify {
            generalInformationFacade.createGeneralInformationMedia(
                mockUUID.toString(),
                match {
                    it.media == mockInputStream && it.href == "test-href"
                }
            )
        }
    }

    @Test
    fun `deleteGeneralInformationMedia returns no content response`() = runTest {
        // Arrange
        coEvery {
            generalInformationFacade.deleteGeneralInformationMedia(any(), any())
        } just runs

        // Act
        val result = controller.deleteGeneralInformationMedia(
            mockUUID.toString(),
            mockUUID.toString()
        )

        // Assert
        assertEquals(Response.Status.NO_CONTENT.statusCode, result.status)
        coVerify {
            generalInformationFacade.deleteGeneralInformationMedia(
                mockUUID.toString(),
                mockUUID.toString()
            )
        }
    }

    @Test
    fun `facade exception is propagated`() = runTest {
        // Arrange
        coEvery {
            generalInformationFacade.getAllGeneralInformation(any(), any())
        } throws RuntimeException("Test exception")

        // Act & Assert
        assertThrows<RuntimeException> {
            controller.getAllGeneralInformation("cursor", 10)
        }
    }
}