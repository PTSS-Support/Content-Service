package org.ptss.support.core.facades

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.api.dtos.requests.generalinformation.UpdateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.generalinformation.CreateGeneralInformationResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationListItemResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationResponse
import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.common.extensions.generalinformation.toCommand
import org.ptss.support.common.extensions.generalinformation.toCreateResponse
import org.ptss.support.common.extensions.generalinformation.toListItemResponse
import org.ptss.support.common.extensions.generalinformation.toResponse
import org.ptss.support.common.extensions.pagination.mapData
import org.ptss.support.core.services.GeneralInformationService

@ApplicationScoped
class GeneralInformationFacade @Inject constructor(
    private val generalInformationService: GeneralInformationService
) {
    suspend fun getAllGeneralInformation(cursor: String?, pageSize: Int): PagedResult<GeneralInformationListItemResponse> =
        generalInformationService.getAllGeneralInformationAsync(cursor, pageSize)
            .mapData { it.toListItemResponse() }

    suspend fun getGeneralInformationById(id: String): GeneralInformationResponse? =
        generalInformationService.getGeneralInformationByIdAsync(id)
            ?.let { it.toResponse() }

    suspend fun createGeneralInformation(request: CreateGeneralInformationRequest): CreateGeneralInformationResponse {
        val generalInformation = generalInformationService.createGeneralInformationAsync(request.toCommand())
        return generalInformation.toCreateResponse()
    }

    suspend fun updateGeneralInformation(id: String, request: UpdateGeneralInformationRequest): GeneralInformationResponse {
        val updatedGeneralInformation = generalInformationService.updateGeneralInformationAsync(id, request)
        return updatedGeneralInformation.toResponse()
    }

    suspend fun deleteGeneralInformation(id: String) =
        generalInformationService.deleteGeneralInformationAsync(id)
}