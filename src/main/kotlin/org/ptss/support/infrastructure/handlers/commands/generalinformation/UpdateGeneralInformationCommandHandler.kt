package org.ptss.support.infrastructure.handlers.commands.generalinformation

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.commands.generalinformation.UpdateGeneralInformationCommand
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.infrastructure.repositories.GeneralInformationRepository

@ApplicationScoped
class UpdateGeneralInformationCommandHandler(
    private val generalInformationRepository: GeneralInformationRepository
) : ICommandHandler<UpdateGeneralInformationCommand, GeneralInformation> {
    override suspend fun handleAsync(command: UpdateGeneralInformationCommand): GeneralInformation {
        return generalInformationRepository.update(command.id, command.title, command.content)
            ?: throw APIException(
                errorCode = ErrorCode.GENERAL_INFORMATION_NOT_FOUND,
                message = "General information with ID ${command.id} not found"
            )
    }
}