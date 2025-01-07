package org.ptss.support.domain.models

import java.util.UUID

data class Media(
    val id: UUID,
    val url: String,
    val href: String?
)
