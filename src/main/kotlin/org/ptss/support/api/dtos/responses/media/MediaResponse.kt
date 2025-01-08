package org.ptss.support.api.dtos.responses.media

import java.util.UUID

data class MediaResponse(
    val id: UUID,
    val url: String,
    val href: String?
)
