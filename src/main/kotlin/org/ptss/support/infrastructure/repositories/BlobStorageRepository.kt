package org.ptss.support.infrastructure.repositories

import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.azure.storage.blob.models.BlobHttpHeaders
import com.azure.storage.blob.sas.BlobSasPermission
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.faulttolerance.Bulkhead
import org.eclipse.microprofile.faulttolerance.Fallback
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException
import org.ptss.support.domain.constants.SasConstraints.DEFAULT_EXPIRY_MINUTES
import org.ptss.support.domain.interfaces.repositories.IBlobStorageRepository
import org.ptss.support.infrastructure.config.AzureStorageConfig
import org.ptss.support.infrastructure.util.retryWithExponentialBackoff
import java.io.InputStream
import java.time.OffsetDateTime

@ApplicationScoped
class BlobStorageRepository(private val azureConfig: AzureStorageConfig) : IBlobStorageRepository {

    private val blobServiceClient: BlobServiceClient = BlobServiceClientBuilder()
        .connectionString(azureConfig.connectionString())
        .buildClient()

    private val containerClient: BlobContainerClient = blobServiceClient.getBlobContainerClient(azureConfig.blobContainerName())
        .apply { if (!exists()) create() }

    @Bulkhead
    @Fallback(fallbackMethod = "fallbackForUploadFile")
    override suspend fun uploadFile(fileName: String, fileData: InputStream, contentType: String): String {
        val blobClient = containerClient.getBlobClient(fileName)

        blobClient.upload(fileData, true)

        val headers = BlobHttpHeaders().setContentType(contentType)
        blobClient.setHttpHeaders(headers)

        return blobClient.blobUrl
    }

    override suspend fun deleteFile(blobName: String) {
        val blobClient = containerClient.getBlobClient(blobName)
        blobClient.delete()
    }

    suspend fun getBlobUrlWithSasToken(blobName: String, expiryMinutes: Long = DEFAULT_EXPIRY_MINUTES): String {
        val blobClient = containerClient.getBlobClient(blobName)

        val sasPermission = BlobSasPermission()
            .setReadPermission(true)
            .setWritePermission(true)
            .setDeletePermission(true)

        val sasValues = BlobServiceSasSignatureValues(
            OffsetDateTime.now().plusMinutes(expiryMinutes),
            sasPermission
        )

        return retryWithExponentialBackoff {
            val sasToken = blobClient.generateSas(sasValues)
            "${blobClient.blobUrl}?$sasToken"
        }
    }

    suspend fun fallbackForUploadFile(fileName: String, fileData: InputStream, contentType: String): String {
        throw CircuitBreakerOpenException("Identity creation temporarily unavailable")
    }
}