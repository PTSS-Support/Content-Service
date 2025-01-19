package org.ptss.support.api.dtos.requests.emergencycontact

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.ptss.support.domain.constants.ValidationConstraints

data class UpdateEmergencyContactRequest(
    @field:NotBlank
    @field:Size(min = ValidationConstraints.MIN_LENGTH, max = ValidationConstraints.TITLE_MAX_LENGTH)
    val name: String,

    @field:NotBlank
    @field:Size(min = ValidationConstraints.MIN_LENGTH, max = ValidationConstraints.TITLE_MAX_LENGTH)
    val phoneNumber: String,

    @field:NotBlank
    @field:Size(min = ValidationConstraints.MIN_LENGTH, max = ValidationConstraints.TITLE_MAX_LENGTH)
    val actionLabel: String
)
