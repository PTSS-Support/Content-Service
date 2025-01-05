package org.ptss.support.api.controllers

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Path
import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.generalinformation.CreateGeneralInformationResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationListItemResponse
import org.ptss.support.core.facades.GeneralInformationFacade
import org.ptss.support.domain.enums.Role
import org.ptss.support.domain.interfaces.controllers.IGeneralInformationController
import org.ptss.support.security.Authentication
import java.util.UUID

@Path("/general-information")
@ApplicationScoped
@Authentication(roles = [Role.ADMIN])
class GeneralInformationController(
    private val generalInformationFacade: GeneralInformationFacade
) : IGeneralInformationController {
    override suspend fun createGeneralInformation(request: CreateGeneralInformationRequest): CreateGeneralInformationResponse =
        CreateGeneralInformationResponse(
            id = UUID.fromString(generalInformationFacade.createGeneralInformation(request)),
            title = request.title,
            content = request.content)
}