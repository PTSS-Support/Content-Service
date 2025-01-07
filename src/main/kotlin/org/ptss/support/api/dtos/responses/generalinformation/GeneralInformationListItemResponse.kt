package org.ptss.support.api.dtos.responses.generalinformation

import java.util.UUID

data class GeneralInformationListItemResponse(
    val id: UUID,
    val title: String
)
