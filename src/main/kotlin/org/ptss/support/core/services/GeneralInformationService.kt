package org.ptss.support.core.services

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.commands.generalinformation.CreateGeneralInformationCommand
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.domain.interfaces.queries.IQueryHandler
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.domain.queries.generalinformation.GetAllGeneralInformationQuery
import org.ptss.support.domain.queries.generalinformation.GetGeneralInformationByIdQuery
import org.ptss.support.infrastructure.handlers.queries.generalinformation.GetAllGeneralInformationQueryHandler
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory

@ApplicationScoped
class GeneralInformationService(
    private val createGeneralInformationHandler: ICommandHandler<CreateGeneralInformationCommand, String>,
    private val getAllGeneralInformationHandler: GetAllGeneralInformationQueryHandler,
    private val getGeneralInformationByIdHandler: IQueryHandler<GetGeneralInformationByIdQuery, GeneralInformation?>,
) {
    private val logger = LoggerFactory.getLogger(GeneralInformationService::class.java)

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

    suspend fun getAllGeneralInformationAsync(): List<GeneralInformation> {
        return logger.executeWithExceptionLoggingAsync(
            operation = { getAllGeneralInformationHandler.handleAsync(GetAllGeneralInformationQuery()) },
            logMessage = "Error retrieving all general information",
            exceptionHandling = { ex ->
                APIException(
                    errorCode = ErrorCode.GENERAL_INFORMATION_CREATION_ERROR,
                    message = "Unable to retrieve general information",
                )
            }
        )
    }

    suspend fun getGeneralInformationByIdAsync(generalInformationId: String): GeneralInformation? {
        return logger.executeWithExceptionLoggingAsync(
            operation = {
                getGeneralInformationByIdHandler.handleAsync(GetGeneralInformationByIdQuery(generalInformationId))
                    ?: throw APIException(
                        errorCode = ErrorCode.GENERAL_INFORMATION_NOT_FOUND,
                        message = "General information with ID $generalInformationId not found"
                    )
            },
            logMessage = "Error retrieving product $generalInformationId",
            exceptionHandling = { ex ->
                when (ex) {
                    is APIException -> ex
                    else -> APIException(
                        errorCode = ErrorCode.GENERAL_INFORMATION_CREATION_ERROR,
                        message = "Unable to retrieve general information with ID: $generalInformationId",
                    )
                }
            }
        )
    }
}