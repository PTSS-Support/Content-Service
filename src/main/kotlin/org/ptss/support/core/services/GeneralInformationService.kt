package org.ptss.support.core.services

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.api.dtos.requests.generalinformation.UpdateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.commands.generalinformation.CreateGeneralInformationCommand
import org.ptss.support.domain.commands.generalinformation.DeleteGeneralInformationCommand
import org.ptss.support.domain.commands.generalinformation.UpdateGeneralInformationCommand
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.domain.interfaces.queries.IQueryHandler
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.domain.queries.generalinformation.GetAllGeneralInformationQuery
import org.ptss.support.domain.queries.generalinformation.GetGeneralInformationByIdQuery
import org.ptss.support.infrastructure.handlers.queries.generalinformation.GetAllGeneralInformationQueryHandler
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory
import kotlin.math.ceil

@ApplicationScoped
class GeneralInformationService(
    private val createGeneralInformationHandler: ICommandHandler<CreateGeneralInformationCommand, GeneralInformation>,
    private val getAllGeneralInformationHandler: GetAllGeneralInformationQueryHandler,
    private val getGeneralInformationByIdHandler: IQueryHandler<GetGeneralInformationByIdQuery, GeneralInformation?>,
    private val updateGeneralInformationHandler: ICommandHandler<UpdateGeneralInformationCommand, GeneralInformation>,
    private val deleteGeneralInformationHandler: ICommandHandler<DeleteGeneralInformationCommand, Unit>,
) {
    private val logger = LoggerFactory.getLogger(GeneralInformationService::class.java)

    suspend fun getAllGeneralInformationAsync(cursor: String?, pageSize: Int): PagedResult<GeneralInformation> {
        return logger.executeWithExceptionLoggingAsync(
            operation = {
                getAllGeneralInformationHandler.handleAsync(GetAllGeneralInformationQuery(cursor, pageSize))
            },
            logMessage = "Error retrieving paginated general information",
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

    suspend fun createGeneralInformationAsync(command: CreateGeneralInformationCommand): GeneralInformation {
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

    suspend fun updateGeneralInformationAsync(generalInformationId: String, request: UpdateGeneralInformationRequest): GeneralInformation {
        val command = UpdateGeneralInformationCommand(generalInformationId, request.title, request.content)

        return logger.executeWithExceptionLoggingAsync(
            operation = { updateGeneralInformationHandler.handleAsync(command) },
            logMessage = "Error updating general information $generalInformationId",
            exceptionHandling = { ex ->
                when (ex) {
                    is APIException -> ex
                    else -> APIException(
                        errorCode = ErrorCode.GENERAL_INFORMATION_UPDATE_ERROR,
                        message = "Failed to update general information $generalInformationId",
                    )
                }
            }
        )
    }

    suspend fun deleteGeneralInformationAsync(generalInformationId: String) {
        logger.executeWithExceptionLoggingAsync(
            operation = {
                deleteGeneralInformationHandler.handleAsync(DeleteGeneralInformationCommand(generalInformationId))
            },
            logMessage = "Error deleting general information $generalInformationId",
            exceptionHandling = { ex ->
                when (ex) {
                    is APIException -> ex
                    else -> APIException(
                        errorCode = ErrorCode.GENERAL_INFORMATION_DELETION_ERROR,
                        message = "Unable to delete general information with ID $generalInformationId",
                    )
                }
            }
        )
    }
}