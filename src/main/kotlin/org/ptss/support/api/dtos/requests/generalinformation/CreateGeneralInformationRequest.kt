package org.ptss.support.api.dtos.requests.generalinformation

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.ptss.support.domain.constants.ValidationConstraints

data class CreateGeneralInformationRequest(
    @field:NotBlank
    @field:Size(min = ValidationConstraints.MIN_LENGTH, max = ValidationConstraints.TITLE_MAX_LENGTH)
    val title: String,

    @field:NotBlank
    @field:Size(min = ValidationConstraints.MIN_LENGTH, max = ValidationConstraints.CONTENT_MAX_LENGTH)
    val content: String
)
