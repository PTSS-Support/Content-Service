package org.ptss.support.common.extensions.media

import org.ptss.support.api.dtos.responses.media.MediaResponse
import org.ptss.support.domain.models.Media

fun Media.toResponse(): MediaResponse = MediaResponse(
    id = this.mediaId,
    url = this.url,
    href = this.href
)