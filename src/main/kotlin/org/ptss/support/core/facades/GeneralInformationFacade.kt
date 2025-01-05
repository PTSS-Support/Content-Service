package org.ptss.support.core.facades

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.core.mappers.GeneralInformationMapper
import org.ptss.support.core.services.GeneralInformationService

@ApplicationScoped
class GeneralInformationFacade @Inject constructor(
    private val generalInformationService: GeneralInformationService
) {
    suspend fun createGeneralInformation(request: CreateGeneralInformationRequest): String =
        generalInformationService.createGeneralInformationAsync(GeneralInformationMapper.toCommand(request))
}