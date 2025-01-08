package org.ptss.support.infrastructure.repositories

import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.infrastructure.config.AzureStorageConfig
import java.io.InputStream

@ApplicationScoped
class BlobStorageRepository(private val azureConfig: AzureStorageConfig) {

    private val blobServiceClient: BlobServiceClient = BlobServiceClientBuilder()
        .connectionString(azureConfig.connectionString())
        .buildClient()

    private val containerClient: BlobContainerClient = blobServiceClient.getBlobContainerClient(azureConfig.containerName())
        .apply { if (!exists()) create() }

    suspend fun uploadFile(fileName: String, fileData: InputStream): String {
        val blobClient = containerClient.getBlobClient(fileName)
        blobClient.upload(fileData, true)
        return blobClient.blobUrl
    }

    suspend fun deleteFile(blobName: String) {
        val blobClient = containerClient.getBlobClient(blobName)
        blobClient.delete()
    }
}