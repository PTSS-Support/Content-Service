package org.ptss.support.domain.models

import java.util.UUID

data class GeneralInformation(
    val id: UUID,
    val title: String,
    val content: String,
    val media: Media? = null
)

