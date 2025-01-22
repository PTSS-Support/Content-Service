package org.ptss.support.core.services

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.quarkus.test.junit.QuarkusTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.commands.generalinformation.CreateGeneralInformationCommand
import org.ptss.support.domain.commands.generalinformation.DeleteGeneralInformationCommand
import org.ptss.support.domain.commands.generalinformation.UpdateGeneralInformationCommand
import org.ptss.support.domain.commands.media.CreateMediaCommand
import org.ptss.support.domain.commands.media.DeleteMediaCommand
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.domain.interfaces.queries.IQueryHandler
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.domain.models.Media
import org.ptss.support.domain.queries.generalinformation.GetGeneralInformationByIdQuery
import org.ptss.support.infrastructure.handlers.queries.generalinformation.GetAllGeneralInformationQueryHandler
import java.io.ByteArrayInputStream
import java.util.UUID

@QuarkusTest
class GeneralInformationServiceTest {
    private lateinit var service: GeneralInformationService
    private lateinit var createGeneralInformationHandler: ICommandHandler<CreateGeneralInformationCommand, GeneralInformation>
    private lateinit var getAllGeneralInformationHandler: GetAllGeneralInformationQueryHandler
    private lateinit var getGeneralInformationByIdHandler: IQueryHandler<GetGeneralInformationByIdQuery, GeneralInformation?>
    private lateinit var updateGeneralInformationHandler: ICommandHandler<UpdateGeneralInformationCommand, GeneralInformation>
    private lateinit var deleteGeneralInformationHandler: ICommandHandler<DeleteGeneralInformationCommand, Unit>
    private lateinit var createMediaHandler: ICommandHandler<CreateMediaCommand, Media>
    private lateinit var deleteMediaHandler: ICommandHandler<DeleteMediaCommand, Unit>

    private val mockUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")

    @BeforeEach
    fun setup() {
        createGeneralInformationHandler = mockk()
        getAllGeneralInformationHandler = mockk()
        getGeneralInformationByIdHandler = mockk()
        updateGeneralInformationHandler = mockk()
        deleteGeneralInformationHandler = mockk()
        createMediaHandler = mockk()
        deleteMediaHandler = mockk()

        service = GeneralInformationService(
            createGeneralInformationHandler,
            getAllGeneralInformationHandler,
            getGeneralInformationByIdHandler,
            updateGeneralInformationHandler,
            deleteGeneralInformationHandler,
            createMediaHandler,
            deleteMediaHandler
        )
    }

    @Test
    fun `getAllGeneralInformationAsync returns paged result successfully`() = runTest {
        // Arrange
        val mockResult = PagedResult(
            data = listOf(
                GeneralInformation(
                    id = mockUUID,
                    title = "Test Title",
                    content = "Test Content"
                )
            ),
            nextCursor = "nextCursor",
            pageSize = 10,
            totalItems = 1,
            totalPages = 1
        )

        coEvery {
            getAllGeneralInformationHandler.handleAsync(any())
        } returns mockResult

        // Act
        val result = service.getAllGeneralInformationAsync("cursor", 10)

        // Assert
        assertEquals(mockResult, result)
        coVerify {
            getAllGeneralInformationHandler.handleAsync(
                match {
                    it.cursor == "cursor" && it.pageSize == 10
                }
            )
        }
    }

    @Test
    fun `getGeneralInformationByIdAsync returns information successfully`() = runTest {
        // Arrange
        val mockInfo = GeneralInformation(
            id = mockUUID,
            title = "Test Title",
            content = "Test Content"
        )

        coEvery {
            getGeneralInformationByIdHandler.handleAsync(any())
        } returns mockInfo

        // Act
        val result = service.getGeneralInformationByIdAsync(mockUUID.toString())

        // Assert
        assertNotNull(result)
        assertEquals(mockInfo, result)
        coVerify {
            getGeneralInformationByIdHandler.handleAsync(
                match {
                    it.id == mockUUID.toString()
                }
            )
        }
    }

    @Test
    fun `validateMediaCommand throws exception for large files`() = runTest {
        // Arrange
        val largeFile = ByteArray(11 * 1024 * 1024) // 11MB
        val inputStream = ByteArrayInputStream(largeFile)
        val command = CreateMediaCommand(
            generalInformationId = mockUUID.toString(),
            fileData = inputStream,
            href = null
        )

        // Act & Assert
        assertThrows<APIException> {
            service.validateMediaCommand(command)
        }.also { exception ->
            assertEquals(ErrorCode.FILE_SIZE_EXCEEDED, exception.errorCode)
        }
    }

    @Test
    fun `validateMediaCommand throws exception for extremely large file`() = runTest {
        // Arrange
        val largeFile = ByteArray(1024 * 1024 * 1024) // 1GB
        val inputStream = ByteArrayInputStream(largeFile)
        val command = CreateMediaCommand(
            generalInformationId = mockUUID.toString(),
            fileData = inputStream,
            href = null
        )

        // Act & Assert
        assertThrows<APIException> {
            service.validateMediaCommand(command)
        }.also { exception ->
            assertEquals(ErrorCode.FILE_SIZE_EXCEEDED, exception.errorCode)
        }
    }

    @Test
    fun `detectFileTypeAndContentType detects JPEG correctly`() = runTest {
        // Arrange
        val jpegHeader = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte())
        val inputStream = ByteArrayInputStream(jpegHeader)

        // Act
        val (fileType, contentType) = service.detectFileTypeAndContentType(inputStream)

        // Assert
        assertEquals(".jpg", fileType)
        assertEquals("image/jpeg", contentType)
    }

    @Test
    fun `detectFileTypeAndContentType throws exception for unsupported type`() = runTest {
        // Arrange
        val unsupportedHeader = byteArrayOf(0x00, 0x11, 0x22, 0x33)
        val inputStream = ByteArrayInputStream(unsupportedHeader)

        // Act & Assert
        assertThrows<APIException> {
            service.detectFileTypeAndContentType(inputStream)
        }.also { exception ->
            assertEquals(ErrorCode.MEDIA_CREATION_ERROR, exception.errorCode)
        }
    }

    @Test
    fun `generateFileName generates unique name with correct extension`() = runTest {
        // Act
        val fileName = service.generateFileName(".jpg")

        // Assert
        assert(fileName.endsWith(".jpg"))
        assert(fileName.length > 36) // UUID length + extension
    }
}