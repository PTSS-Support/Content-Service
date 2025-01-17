package org.ptss.support.core.services

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.quarkus.test.junit.QuarkusTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.infrastructure.repositories.BlobStorageRepository
import java.io.ByteArrayInputStream
import java.util.UUID

@QuarkusTest
class BlobStorageServiceTest {
    private lateinit var blobStorageService: BlobStorageService
    private lateinit var blobStorageRepository: BlobStorageRepository
    private lateinit var generalInformationService: GeneralInformationService

    @BeforeEach
    fun setup() {
        blobStorageRepository = mockk()
        generalInformationService = mockk()
        blobStorageService = BlobStorageService(blobStorageRepository, generalInformationService)
    }

    @Test
    fun `uploadFileAsync successfully uploads file`() = runTest {
        // Arrange
        val fileContent = "test content".toByteArray()
        val inputStream = ByteArrayInputStream(fileContent)
        val fileName = "test-${UUID.randomUUID()}.jpg"
        val contentType = "image/jpeg"

        coEvery {
            generalInformationService.detectFileTypeAndContentType(any())
        } returns Pair(".jpg", contentType)

        coEvery {
            generalInformationService.generateFileName(".jpg")
        } returns fileName

        coEvery {
            blobStorageRepository.uploadFile(fileName, any(), contentType)
        } returns fileName

        // Act
        val result = blobStorageService.uploadFileAsync(inputStream)

        // Assert
        assertEquals(fileName, result)
        coVerify {
            blobStorageRepository.uploadFile(fileName, any(), contentType)
            generalInformationService.detectFileTypeAndContentType(any())
            generalInformationService.generateFileName(".jpg")
        }
    }

    @Test
    fun `uploadFileAsync throws APIException when upload fails`() = runTest {
        // Arrange
        val fileContent = "test content".toByteArray()
        val inputStream = ByteArrayInputStream(fileContent)

        coEvery {
            generalInformationService.detectFileTypeAndContentType(any())
        } throws RuntimeException("Upload failed")

        // Act & Assert
        assertThrows<APIException> {
            blobStorageService.uploadFileAsync(inputStream)
        }.also { exception ->
            assertEquals(ErrorCode.MEDIA_CREATION_ERROR, exception.errorCode)
            assertEquals("Failed to upload file to Azure Blob Storage", exception.message)
        }
    }

    @Test
    fun `uploadFileAsync throws APIException for empty file`() = runTest {
        // Arrange
        val emptyInputStream = ByteArrayInputStream(ByteArray(0))

        // Act & Assert
        assertThrows<APIException> {
            blobStorageService.uploadFileAsync(emptyInputStream)
        }.also { exception ->
            assertEquals(ErrorCode.MEDIA_CREATION_ERROR, exception.errorCode)
        }
    }

    @Test
    fun `deleteFileAsync successfully deletes file`() = runTest {
        // Arrange
        val blobName = "test-blob.jpg"
        coEvery {
            blobStorageRepository.deleteFile(blobName)
        } just runs

        // Act & Assert
        blobStorageService.deleteFileAsync(blobName)

        // Assert
        coVerify { blobStorageRepository.deleteFile(blobName) }
    }

    @Test
    fun `deleteFileAsync throws APIException when deletion fails`() = runTest {
        // Arrange
        val blobName = "test-blob.jpg"
        coEvery {
            blobStorageRepository.deleteFile(blobName)
        } throws RuntimeException("Deletion failed")

        // Act & Assert
        assertThrows<APIException> {
            blobStorageService.deleteFileAsync(blobName)
        }.also { exception ->
            assertEquals(ErrorCode.MEDIA_DELETION_ERROR, exception.errorCode)
            assertEquals("Failed to delete blob test-blob.jpg", exception.message)
        }
    }

    @Test
    fun `deleteFileAsync handles non-existent blob gracefully`() = runTest {
        // Arrange
        val blobName = "non-existent-blob.jpg"
        coEvery { blobStorageRepository.deleteFile(blobName) } throws RuntimeException("Blob not found")

        // Act & Assert
        assertThrows<APIException> {
            blobStorageService.deleteFileAsync(blobName)
        }.also { exception ->
            assertEquals(ErrorCode.MEDIA_DELETION_ERROR, exception.errorCode)
            assertEquals("Failed to delete blob non-existent-blob.jpg", exception.message)
        }
    }


    @Test
    fun `getPublicBlobUrl returns correct URL`() = runTest {
        // Arrange
        val blobName = "test-blob.jpg"
        val expectedUrl = "https://storage.azure.com/test-blob.jpg?token=123"
        coEvery {
            blobStorageRepository.getBlobUrlWithSasToken(blobName)
        } returns expectedUrl

        // Act
        val result = blobStorageService.getPublicBlobUrl(blobName)

        // Assert
        assertEquals(expectedUrl, result)
        coVerify { blobStorageRepository.getBlobUrlWithSasToken(blobName) }
    }
}