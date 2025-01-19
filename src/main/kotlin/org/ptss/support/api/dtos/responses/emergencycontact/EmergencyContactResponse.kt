package org.ptss.support.api.dtos.responses.emergencycontact

import java.util.UUID

data class EmergencyContactResponse(
    val id: UUID,
    val name: String,
    val phoneNumber: String,
    val actionLabel: String
)
