package org.ptss.support.core.facades

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.ptss.support.api.dtos.requests.emergencycontact.UpdateEmergencyContactRequest
import org.ptss.support.api.dtos.responses.emergencycontact.EmergencyContactResponse
import org.ptss.support.common.extensions.emergencycontact.toResponse
import org.ptss.support.core.services.EmergencyContactService

@ApplicationScoped
class EmergencyContactFacade @Inject constructor(
    private val emergencyContactService: EmergencyContactService
) {
    suspend fun getAllEmergencyContacts(): List<EmergencyContactResponse> =
        emergencyContactService.getAllEmergencyContactsAsync()
            .map { it.toResponse() }

    suspend fun updateEmergencyContact(id: String, request: UpdateEmergencyContactRequest): EmergencyContactResponse {
        val updatedEmergencyContact = emergencyContactService.updateEmergencyContactAsync(id, request)
        return updatedEmergencyContact.toResponse()
    }
}