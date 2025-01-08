package org.ptss.support.core.services

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.infrastructure.repositories.BlobStorageRepository
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.*

@ApplicationScoped
class BlobStorageService(private val blobStorageRepository: BlobStorageRepository) {

    private val logger = LoggerFactory.getLogger(BlobStorageService::class.java)

    suspend fun uploadFileAsync(fileStream: InputStream): String {
        return fileStream.use { stream ->
            val bufferedStream = stream.buffered()
            logger.executeWithExceptionLoggingAsync(
                operation = {
                    val fileType = detectFileTypeMagicNumbers(bufferedStream)
                    val fileName = generateFileName(fileType)
                    blobStorageRepository.uploadFile(fileName, bufferedStream)
                },
                logMessage = "Error uploading file to Azure Blob Storage",
                exceptionHandling = {
                    APIException(
                        errorCode = ErrorCode.MEDIA_CREATION_ERROR,
                        message = "Failed to upload file to Azure Blob Storage"
                    )
                }
            )
        }
    }

    suspend fun deleteFileAsync(blobName: String) {
        logger.executeWithExceptionLoggingAsync(
            operation = { blobStorageRepository.deleteFile(blobName) },
            logMessage = "Error deleting blob $blobName",
            exceptionHandling = {
                APIException(
                    errorCode = ErrorCode.MEDIA_DELETION_ERROR,
                    message = "Failed to delete blob $blobName"
                )
            }
        )
    }

    private fun detectFileTypeMagicNumbers(fileStream: InputStream): String {
        val buffer = ByteArray(8)
        fileStream.mark(8)
        fileStream.read(buffer)
        fileStream.reset()

        return when {
            buffer.startsWith(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())) -> ".jpg"
            buffer.startsWith(byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte())) -> ".png"
            buffer.startsWith("RIFF".toByteArray()) && buffer.slice(8..11).toByteArray().contentEquals("WEBP".toByteArray()) -> ".webp"
            buffer.startsWith(byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x18.toByte(), 0x66.toByte(), 0x74.toByte(), 0x79.toByte(), 0x70.toByte())) -> ".mp4"
            buffer.startsWith(byteArrayOf(0x1A.toByte(), 0x45.toByte(), 0xDF.toByte(), 0xA3.toByte())) -> ".mkv"
            buffer.startsWith("%PDF".toByteArray()) -> ".pdf"
            else -> throw APIException(
                errorCode = ErrorCode.MEDIA_CREATION_ERROR,
                message = "Unsupported file type"
            )
        }
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean =
        this.take(prefix.size).toByteArray().contentEquals(prefix)

    private fun generateFileName(fileType: String): String {
        return "${UUID.randomUUID()}$fileType"
    }
}