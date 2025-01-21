package org.ptss.support.domain.interfaces.repositories

import java.io.InputStream

interface IBlobStorageRepository {
    suspend fun uploadFile(fileName: String, fileData: InputStream, contentType: String): String
    suspend fun deleteFile(blobName: String)
}