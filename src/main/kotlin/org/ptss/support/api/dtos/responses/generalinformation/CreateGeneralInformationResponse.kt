package org.ptss.support.api.dtos.responses.generalinformation

import java.util.UUID

data class CreateGeneralInformationResponse(
    val id: UUID,
    val title: String,
    val content: String
)
