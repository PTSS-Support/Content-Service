package org.ptss.support.infrastructure.handlers.commands.generalinformation

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.commands.generalinformation.DeleteGeneralInformationCommand
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.infrastructure.repositories.GeneralInformationRepository

@ApplicationScoped
class DeleteGeneralInformationCommandHandler(
    private val generalInformationRepository: GeneralInformationRepository
) : ICommandHandler<DeleteGeneralInformationCommand, Unit> {
    override suspend fun handleAsync(command: DeleteGeneralInformationCommand) {
        generalInformationRepository.delete(command.id)
            ?: throw APIException(
                errorCode = ErrorCode.GENERAL_INFORMATION_NOT_FOUND,
                message = "General information with ID ${command.id} not found"
            )
    }
}