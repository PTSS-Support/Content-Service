package org.ptss.support.domain.models

import java.util.UUID

data class EmergencyContact(
    val id: UUID,
    val name: String,
    val phoneNumber: String,
    val actionLabel: String
)
