package org.ptss.support.core.services

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.commands.generalinformation.CreateGeneralInformationCommand
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory

@ApplicationScoped
class GeneralInformationService(
    private val createGeneralInformationHandler: ICommandHandler<CreateGeneralInformationCommand, String>
) {
    private val logger = LoggerFactory.getLogger(ProductService::class.java)

    suspend fun createGeneralInformationAsync(command: CreateGeneralInformationCommand): String {
        //validateGeneralInformationCommand(command)
        return logger.executeWithExceptionLoggingAsync(
            operation = { createGeneralInformationHandler.handleAsync(command) },
            logMessage = "Error creating general information ${command.title}",
            exceptionHandling = { ex ->
                APIException(
                    errorCode = ErrorCode.GENERAL_INFORMATION_CREATION_ERROR,
                    message = "Failed to create general information ${command.title}",
                )
            }
        )
    }
}