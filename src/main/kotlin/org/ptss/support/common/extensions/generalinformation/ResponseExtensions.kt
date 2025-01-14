package org.ptss.support.common.extensions.generalinformation

import org.ptss.support.api.dtos.responses.generalinformation.CreateGeneralInformationResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationListItemResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationResponse
import org.ptss.support.common.extensions.media.toResponse
import org.ptss.support.domain.models.GeneralInformation

fun GeneralInformation.toResponse(): GeneralInformationResponse = GeneralInformationResponse(
    id = this.id,
    title = this.title,
    content = this.content,
    media = this.media?.toResponse()
)

fun GeneralInformation.toListItemResponse() = GeneralInformationListItemResponse(
    id = this.id,
    title = this.title
)

fun GeneralInformation.toCreateResponse() = CreateGeneralInformationResponse(
    id = this.id,
    title = this.title,
    content = this.content
)