package org.ptss.support.common.extensions.media

import org.ptss.support.api.dtos.requests.media.CreateMediaRequest
import org.ptss.support.domain.commands.media.CreateMediaCommand

fun CreateMediaRequest.toCommand(generalInformationId: String) = CreateMediaCommand(
    generalInformationId = generalInformationId,
    fileData = media,
    href = href
)