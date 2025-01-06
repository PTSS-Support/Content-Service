package org.ptss.support.domain.interfaces.controllers

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.Produces
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.api.dtos.requests.generalinformation.UpdateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.generalinformation.CreateGeneralInformationResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationListItemResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationResponse
import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.common.exceptions.ServiceError

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface IGeneralInformationController {
    @GET
    @Operation(summary = "Get all general information", description = "Retrieves a paginated list of general information")
    @APIResponses(
        APIResponse(
            responseCode = "200",
            description = "List of general information successfully retrieved",
            content = [Content(schema = Schema(implementation = Array<GeneralInformationListItemResponse>::class))]
        ),
        APIResponse(
            responseCode = "400",
            description = "Invalid parameters"
        ),
        APIResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        APIResponse(
            responseCode = "403",
            description = "Forbidden"
        ),
        APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(schema = Schema(implementation = ServiceError::class))]
        )
    )
    suspend fun getAllGeneralInformation(
        @Parameter(description = "Cursor for pagination") @QueryParam("cursor") cursor: String?,
        @Parameter(description = "Page size (1-50)") @QueryParam("size") @DefaultValue("20") @Min(1) @Max(50) pageSize: Int
    ): PagedResult<GeneralInformationListItemResponse>

    @GET
    @Path("/{id}")
    @Operation(summary = "Get general information by ID", description = "Retrieves a specific general information by its ID")
    @APIResponses(
        APIResponse(
            responseCode = "200",
            description = "General information successfully retrieved",
            content = [Content(schema = Schema(implementation = GeneralInformationResponse::class))]
        ),
        APIResponse(
            responseCode = "400",
            description = "Invalid parameters"
        ),
        APIResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        APIResponse(
            responseCode = "403",
            description = "Forbidden"
        ),
        APIResponse(
            responseCode = "404",
            description = "General information not found",
            content = [Content(schema = Schema(implementation = ServiceError::class))]
        )
    )
    suspend fun getGeneralInformationById(
        @Parameter(description = "ID of the general information", required = true)
        @PathParam("id") id: String
    ): GeneralInformationResponse?

    @POST
    @Operation(summary = "Create general-information", description = "Creates a new general-information")
    @APIResponses(
        APIResponse(
            responseCode = "201",
            description = "general-information successfully created",
            content = [Content(schema = Schema(implementation = CreateGeneralInformationResponse::class))]
        ),
        APIResponse(
            responseCode = "400",
            description = "Invalid parameters"
        ),
        APIResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        APIResponse(
            responseCode = "403",
            description = "Forbidden"
        ),
        APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(schema = Schema(implementation = ServiceError::class))]
        )
    )
    suspend fun createGeneralInformation(request: CreateGeneralInformationRequest): CreateGeneralInformationResponse

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update general information", description = "Updates the content/title of a existing general information by its ID")
    @APIResponses(
        APIResponse(
            responseCode = "200",
            description = "General information successfully updated",
            content = [Content(schema = Schema(implementation = GeneralInformationResponse::class))]
        ),
        APIResponse(
            responseCode = "400",
            description = "Invalid input"
        ),
        APIResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        APIResponse(
            responseCode = "403",
            description = "Not authorized to update this general information",
            content = [Content(schema = Schema(implementation = ServiceError::class))]
        ),
        APIResponse(
            responseCode = "404",
            description = "General information not found",
            content = [Content(schema = Schema(implementation = ServiceError::class))]
        ),
        APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(schema = Schema(implementation = ServiceError::class))]
        )
    )
    suspend fun updateGeneralInformation(
        @Parameter(description = "General information ID", required = true) @PathParam("id") id: String,
        @Parameter(description = "General information update data", required = true) request: UpdateGeneralInformationRequest
    ): GeneralInformationResponse

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete general information", description = "Deletes a specific general information by its ID")
    @APIResponses(
        APIResponse(
            responseCode = "204",
            description = "General information successfully deleted"
        ),
        APIResponse(
            responseCode = "400",
            description = "Invalid input"
        ),
        APIResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        APIResponse(
            responseCode = "403",
            description = "Not authorized to delete this general information",
            content = [Content(schema = Schema(implementation = ServiceError::class))]
        ),
        APIResponse(
            responseCode = "404",
            description = "General information not found",
            content = [Content(schema = Schema(implementation = ServiceError::class))]
        ),
        APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(schema = Schema(implementation = ServiceError::class))]
        )
    )
    suspend fun deleteGeneralInformation(
        @Parameter(description = "General information ID", required = true) @PathParam("id") id: String
    ): Response
}