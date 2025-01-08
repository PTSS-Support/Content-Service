package org.ptss.support.api.dtos.responses.generalinformation

import org.ptss.support.api.dtos.responses.media.MediaResponse
import java.util.UUID

data class GeneralInformationResponse(
    val id: UUID,
    val title: String,
    val content: String,
    val media: MediaResponse?
)
