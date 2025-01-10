package org.ptss.support.domain.commands.emergencycontact

data class UpdateEmergencyContactCommand(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val actionLabel: String
)
