package org.ptss.support.core.mappers

import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.generalinformation.CreateGeneralInformationResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationListItemResponse
import org.ptss.support.api.dtos.responses.generalinformation.GeneralInformationResponse
import org.ptss.support.domain.commands.generalinformation.CreateGeneralInformationCommand
import org.ptss.support.domain.models.GeneralInformation

object GeneralInformationMapper {
    fun toCommand(request: CreateGeneralInformationRequest) = CreateGeneralInformationCommand(
        title = request.title,
        content = request.content
    )

    fun toResponse(generalInformation: GeneralInformation) = GeneralInformationResponse(
        id = generalInformation.id,
        title = generalInformation.title,
        content = generalInformation.content,
        media = null
    )

    fun toListItemResponse(generalInformation: GeneralInformation) = GeneralInformationListItemResponse(
        id = generalInformation.id,
        title = generalInformation.title
    )

    fun toCreateResponse(generalInformation: GeneralInformation) = CreateGeneralInformationResponse(
        id = generalInformation.id,
        title = generalInformation.title,
        content = generalInformation.content
    )
}