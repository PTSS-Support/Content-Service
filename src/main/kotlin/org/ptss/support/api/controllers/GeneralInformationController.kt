package org.ptss.support.api.controllers

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.api.dtos.requests.generalinformation.UpdateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.generalinformation.CreateGeneralInformationResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationListItemResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationResponse
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
    override suspend fun getAllGeneralInformation(): List<GeneralInformationListItemResponse> =
        generalInformationFacade.getAllGeneralInformation()

    override suspend fun getGeneralInformationById(@PathParam("id") id: String): GeneralInformationResponse? =
        generalInformationFacade.getGeneralInformationById(id)

    override suspend fun createGeneralInformation(request: CreateGeneralInformationRequest): CreateGeneralInformationResponse =
        CreateGeneralInformationResponse(
            id = UUID.fromString(generalInformationFacade.createGeneralInformation(request)),
            title = request.title,
            content = request.content)

    override suspend fun updateGeneralInformation(@PathParam("id") id: String, request: UpdateGeneralInformationRequest): GeneralInformationResponse =
        generalInformationFacade.updateGeneralInformation(id, request)
}