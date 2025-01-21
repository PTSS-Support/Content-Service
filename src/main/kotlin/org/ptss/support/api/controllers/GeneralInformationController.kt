package org.ptss.support.api.controllers

import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.ws.rs.Path
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestForm
import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.api.dtos.requests.generalinformation.UpdateGeneralInformationRequest
import org.ptss.support.api.dtos.requests.media.CreateMediaRequest
import org.ptss.support.api.dtos.responses.generalinformation.CreateGeneralInformationResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationListItemResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationResponse
import org.ptss.support.api.dtos.responses.media.MediaResponse
import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.core.facades.GeneralInformationFacade
import org.ptss.support.domain.constants.PaginationConstraints
import org.ptss.support.domain.enums.Role
import org.ptss.support.domain.interfaces.controllers.IGeneralInformationController
import org.ptss.support.security.Authentication
import java.io.InputStream

@Path("/general-information")
@ApplicationScoped
@Authentication(roles = [Role.ADMIN, Role.PATIENT, Role.HCP, Role.FAMILY_MEMBER])
class GeneralInformationController(
    private val generalInformationFacade: GeneralInformationFacade
) : IGeneralInformationController {
    override suspend fun getAllGeneralInformation(
        @QueryParam("cursor") cursor: String?,
        @QueryParam("size")
        @DefaultValue(PaginationConstraints.DEFAULT_PAGE_SIZE.toString())
        @Min(PaginationConstraints.MIN_PAGE_SIZE)
        @Max(PaginationConstraints.MAX_PAGE_SIZE)
        pageSize: Int
    ): PagedResult<GeneralInformationListItemResponse> =
        generalInformationFacade.getAllGeneralInformation(cursor, pageSize)

    override suspend fun getGeneralInformationById(@PathParam("id") id: String): GeneralInformationResponse? =
        generalInformationFacade.getGeneralInformationById(id)

    @Authentication(roles = [Role.ADMIN])
    override suspend fun createGeneralInformation(request: CreateGeneralInformationRequest): CreateGeneralInformationResponse =
        generalInformationFacade.createGeneralInformation(request)

    @Authentication(roles = [Role.ADMIN])
    override suspend fun updateGeneralInformation(@PathParam("id") id: String, request: UpdateGeneralInformationRequest): GeneralInformationResponse =
        generalInformationFacade.updateGeneralInformation(id, request)

    @Authentication(roles = [Role.ADMIN])
    override suspend fun deleteGeneralInformation(@PathParam("id") id: String): Response {
        generalInformationFacade.deleteGeneralInformation(id)
        return Response.noContent().build()
    }

    @Authentication(roles = [Role.ADMIN])
    override suspend fun createGeneralInformationMedia(
        @PathParam("id") id: String,
        @RestForm("file") file: InputStream,
        @RestForm("href") href: String?
    ): MediaResponse = generalInformationFacade.createGeneralInformationMedia(id, CreateMediaRequest(file, href))

    @Authentication(roles = [Role.ADMIN])
    override suspend fun deleteGeneralInformationMedia(@PathParam("id") generalInformationId: String, @PathParam("mediaId") mediaId: String): Response {
        generalInformationFacade.deleteGeneralInformationMedia(generalInformationId, mediaId)
        return Response.noContent().build()
    }
}