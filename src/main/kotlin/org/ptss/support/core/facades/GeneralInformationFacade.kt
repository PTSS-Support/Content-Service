package org.ptss.support.core.facades

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.api.dtos.requests.generalinformation.UpdateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationListItemResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationResponse
import org.ptss.support.core.mappers.GeneralInformationMapper
import org.ptss.support.core.services.GeneralInformationService

@ApplicationScoped
class GeneralInformationFacade @Inject constructor(
    private val generalInformationService: GeneralInformationService
) {
    suspend fun getAllGeneralInformation(): List<GeneralInformationListItemResponse> =
        generalInformationService.getAllGeneralInformationAsync()
            .map(GeneralInformationMapper::toListItemResponse)

    suspend fun getGeneralInformationById(id: String): GeneralInformationResponse? =
        generalInformationService.getGeneralInformationByIdAsync(id)
            ?.let(GeneralInformationMapper::toResponse)

    suspend fun createGeneralInformation(request: CreateGeneralInformationRequest): String =
        generalInformationService.createGeneralInformationAsync(GeneralInformationMapper.toCommand(request))

    suspend fun updateGeneralInformation(id: String, request: UpdateGeneralInformationRequest): GeneralInformationResponse {
        val updatedGeneralInformation = generalInformationService.updateGeneralInformationAsync(id, request)
        return GeneralInformationMapper.toResponse(updatedGeneralInformation)
    }

    suspend fun deleteGeneralInformation(id: String) =
        generalInformationService.deleteGeneralInformationAsync(id)
}