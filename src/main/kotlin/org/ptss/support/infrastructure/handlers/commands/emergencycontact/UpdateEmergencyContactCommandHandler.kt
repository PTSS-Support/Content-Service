package org.ptss.support.infrastructure.handlers.commands.emergencycontact

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.commands.emergencycontact.UpdateEmergencyContactCommand
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.domain.models.EmergencyContact
import org.ptss.support.infrastructure.repositories.EmergencyContactRepository

@ApplicationScoped
class UpdateEmergencyContactCommandHandler(
    private val emergencyContactRepository: EmergencyContactRepository
) : ICommandHandler<UpdateEmergencyContactCommand, EmergencyContact> {
    override suspend fun handleAsync(command: UpdateEmergencyContactCommand): EmergencyContact {
        return emergencyContactRepository.update(command.id, command.name, command.phoneNumber, command.actionLabel)
            ?: throw APIException(
                errorCode = ErrorCode.EMERGENCY_CONTACT_NOT_FOUND,
                message = "Emergency contact with ID ${command.id} not found"
            )
    }
}