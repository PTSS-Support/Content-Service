package org.ptss.support.core.services

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.api.dtos.requests.emergencycontact.UpdateEmergencyContactRequest
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.commands.emergencycontact.UpdateEmergencyContactCommand
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.domain.models.EmergencyContact
import org.ptss.support.domain.queries.emergencycontact.GetAllEmergencyContactsQuery
import org.ptss.support.infrastructure.handlers.queries.emergencycontact.GetAllEmergencyContactsQueryHandler
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory

@ApplicationScoped
class EmergencyContactService(
    private val getAllEmergencyContactsHandler: GetAllEmergencyContactsQueryHandler,
    private val updateEmergencyContactsHandler: ICommandHandler<UpdateEmergencyContactCommand, EmergencyContact>,
) {
    private val logger = LoggerFactory.getLogger(EmergencyContactService::class.java)
    suspend fun getAllEmergencyContactsAsync(): List<EmergencyContact> {
        return logger.executeWithExceptionLoggingAsync(
            operation = { getAllEmergencyContactsHandler.handleAsync(GetAllEmergencyContactsQuery()) },
            logMessage = "Error retrieving all emergency contacts",
            exceptionHandling = { ex ->
                APIException(
                    errorCode = ErrorCode.EMERGENCY_CONTACT_CREATION_ERROR,
                    message = "Unable to retrieve emergency contacts",
                )
            }
        )
    }

    suspend fun updateEmergencyContactAsync(emergencyContactId: String, request: UpdateEmergencyContactRequest): EmergencyContact {
        val command = UpdateEmergencyContactCommand(emergencyContactId, request.name, request.phoneNumber, request.actionLabel)

        return logger.executeWithExceptionLoggingAsync(
            operation = { updateEmergencyContactsHandler.handleAsync(command) },
            logMessage = "Error updating emergency contact $emergencyContactId",
            exceptionHandling = { ex ->
                when (ex) {
                    is APIException -> ex
                    else -> APIException(
                        errorCode = ErrorCode.EMERGENCY_CONTACT_UPDATE_ERROR,
                        message = "Failed to update emergency contact $emergencyContactId",
                    )
                }
            }
        )
    }

}