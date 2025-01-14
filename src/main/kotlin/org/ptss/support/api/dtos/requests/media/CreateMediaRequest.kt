package org.ptss.support.api.dtos.requests.media

import java.io.InputStream

data class CreateMediaRequest(
    val media: InputStream,
    val href: String?
)
