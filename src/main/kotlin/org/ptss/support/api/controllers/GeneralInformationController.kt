package org.ptss.support.api.controllers

import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.api.dtos.requests.generalinformation.UpdateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.generalinformation.CreateGeneralInformationResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationListItemResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationResponse
import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.core.facades.GeneralInformationFacade
import org.ptss.support.domain.enums.Role
import org.ptss.support.domain.interfaces.controllers.IGeneralInformationController
import org.ptss.support.security.Authentication

@Path("/general-information")
@ApplicationScoped
@Authentication(roles = [Role.ADMIN])
class GeneralInformationController(
    private val generalInformationFacade: GeneralInformationFacade
) : IGeneralInformationController {
    override suspend fun getAllGeneralInformation(
        @QueryParam("cursor") cursor: String?,
        @QueryParam("size") @DefaultValue("20") @Min(1) @Max(50) pageSize: Int
    ): PagedResult<GeneralInformationListItemResponse> =
        generalInformationFacade.getAllGeneralInformation(cursor, pageSize)

    override suspend fun getGeneralInformationById(@PathParam("id") id: String): GeneralInformationResponse? =
        generalInformationFacade.getGeneralInformationById(id)

    override suspend fun createGeneralInformation(request: CreateGeneralInformationRequest): CreateGeneralInformationResponse =
        generalInformationFacade.createGeneralInformation(request)

    override suspend fun updateGeneralInformation(@PathParam("id") id: String, request: UpdateGeneralInformationRequest): GeneralInformationResponse =
        generalInformationFacade.updateGeneralInformation(id, request)

    override suspend fun deleteGeneralInformation(@PathParam("id") id: String): Response {
        generalInformationFacade.deleteGeneralInformation(id)
        return Response.noContent().build()
    }
}