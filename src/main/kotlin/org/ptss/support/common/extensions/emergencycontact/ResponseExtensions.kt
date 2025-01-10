package org.ptss.support.common.extensions.emergencycontact

import org.ptss.support.api.dtos.responses.emergencycontact.EmergencyContactResponse
import org.ptss.support.domain.models.EmergencyContact

fun EmergencyContact.toResponse() = EmergencyContactResponse(
    id = this.id,
    name = this.name,
    phoneNumber = this.phoneNumber,
    actionLabel = this.actionLabel
)