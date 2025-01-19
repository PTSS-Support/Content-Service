package org.ptss.support.domain.interfaces.controllers

import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.ptss.support.api.dtos.requests.emergencycontact.UpdateEmergencyContactRequest
import org.ptss.support.api.dtos.requests.generalinformation.UpdateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.emergencycontact.EmergencyContactResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationResponse
import org.ptss.support.common.exceptions.ServiceError

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
interface IEmergencyContactController {
    @GET
    @Operation(summary = "Get all emergency contacts", description = "Retrieves a list of all emergency contacts")
    @APIResponses(
        APIResponse(
            responseCode = "200",
            description = "List of emergency contacts successfully retrieved",
            content = [Content(schema = Schema(implementation = Array<EmergencyContactResponse>::class))]
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
    suspend fun getAllEmergencyContacts(): List<EmergencyContactResponse>

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update emergency contact", description = "Updates the emergency contact info of a existing ID")
    @APIResponses(
        APIResponse(
            responseCode = "200",
            description = "Emergency contact successfully updated",
            content = [Content(schema = Schema(implementation = EmergencyContactResponse::class))]
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
            description = "Not authorized to update this emergency contact"
        ),
        APIResponse(
            responseCode = "404",
            description = "Emergency contact not found"
        ),
        APIResponse(
            responseCode = "500",
            description = "Internal server error",
            content = [Content(schema = Schema(implementation = ServiceError::class))]
        )
    )
    suspend fun updateEmergencyContact(
        @Parameter(description = "Emergency contact ID", required = true) @PathParam("id") id: String,
        @Parameter(description = "Emergency contact update data", required = true) @Valid request: UpdateEmergencyContactRequest
    ): EmergencyContactResponse
}