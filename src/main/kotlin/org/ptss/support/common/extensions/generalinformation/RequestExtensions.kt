package org.ptss.support.common.extensions.generalinformation

import org.ptss.support.api.dtos.requests.generalinformation.CreateGeneralInformationRequest
import org.ptss.support.domain.commands.generalinformation.CreateGeneralInformationCommand

fun CreateGeneralInformationRequest.toCommand() = CreateGeneralInformationCommand(
    title = this.title,
    content = this.content
)