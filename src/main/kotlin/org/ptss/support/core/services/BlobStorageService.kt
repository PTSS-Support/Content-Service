package org.ptss.support.core.services

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.infrastructure.repositories.BlobStorageRepository
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory
import java.io.InputStream

@ApplicationScoped
class BlobStorageService(
    private val blobStorageRepository: BlobStorageRepository,
    private val generalInformationService: GeneralInformationService
) {

    private val logger = LoggerFactory.getLogger(BlobStorageService::class.java)

    suspend fun uploadFileAsync(fileStream: InputStream): String {
        return fileStream.use { stream ->
            val bufferedStream = stream.buffered()
            logger.executeWithExceptionLoggingAsync(
                operation = {
                    val (fileType, contentType) = generalInformationService.detectFileTypeAndContentType(bufferedStream)
                    val fileName = generalInformationService.generateFileName(fileType)
                    blobStorageRepository.uploadFile(fileName, bufferedStream, contentType)
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

    suspend fun getPublicBlobUrl(blobName: String): String {
        return blobStorageRepository.getBlobUrlWithSasToken(blobName)
    }
}