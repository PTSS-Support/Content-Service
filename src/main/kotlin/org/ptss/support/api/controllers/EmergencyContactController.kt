package org.ptss.support.api.controllers

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import org.ptss.support.api.dtos.requests.emergencycontact.UpdateEmergencyContactRequest
import org.ptss.support.api.dtos.responses.emergencycontact.EmergencyContactResponse
import org.ptss.support.core.facades.EmergencyContactFacade
import org.ptss.support.domain.enums.Role
import org.ptss.support.domain.interfaces.controllers.IEmergencyContactController
import org.ptss.support.security.Authentication

@Path("/emergency-contacts")
@ApplicationScoped
@Authentication(roles = [Role.PATIENT])
class EmergencyContactController(
    private val emergencyContactFacade: EmergencyContactFacade
): IEmergencyContactController {
    override suspend fun getAllEmergencyContacts(): List<EmergencyContactResponse> =
        emergencyContactFacade.getAllEmergencyContacts()

    @Authentication(roles = [Role.ADMIN])
    override suspend fun updateEmergencyContact(@PathParam("id") id: String, request: UpdateEmergencyContactRequest): EmergencyContactResponse =
        emergencyContactFacade.updateEmergencyContact(id, request)
}