package org.ptss.support.domain.interfaces.repositories

import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.domain.models.Media

interface IGeneralInformationRepository {
    suspend fun getAll(cursor: String?, pageSize: Int): PagedResult<GeneralInformation>
    suspend fun getById(id: String): GeneralInformation?
    suspend fun create(generalInformation: GeneralInformation): String
    suspend fun update(id: String, title: String, content: String): GeneralInformation?
    suspend fun delete(id: String): GeneralInformation?
    suspend fun createMedia(id: String, media: Media? = null): GeneralInformation?
    suspend fun deleteMedia(generalInformationId: String, mediaId: String): Media?
}