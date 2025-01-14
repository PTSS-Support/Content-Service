package org.ptss.support.core.services

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.api.dtos.requests.generalinformation.UpdateGeneralInformationRequest
import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.commands.generalinformation.CreateGeneralInformationCommand
import org.ptss.support.domain.commands.generalinformation.DeleteGeneralInformationCommand
import org.ptss.support.domain.commands.generalinformation.UpdateGeneralInformationCommand
import org.ptss.support.domain.commands.media.CreateMediaCommand
import org.ptss.support.domain.commands.media.DeleteMediaCommand
import org.ptss.support.domain.constants.FileSizeConstants.MAX_FILE_SIZE
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.domain.interfaces.queries.IQueryHandler
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.domain.models.Media
import org.ptss.support.domain.queries.generalinformation.GetAllGeneralInformationQuery
import org.ptss.support.domain.queries.generalinformation.GetGeneralInformationByIdQuery
import org.ptss.support.infrastructure.handlers.queries.generalinformation.GetAllGeneralInformationQueryHandler
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.util.UUID

@ApplicationScoped
class GeneralInformationService(
    private val createGeneralInformationHandler: ICommandHandler<CreateGeneralInformationCommand, GeneralInformation>,
    private val getAllGeneralInformationHandler: GetAllGeneralInformationQueryHandler,
    private val getGeneralInformationByIdHandler: IQueryHandler<GetGeneralInformationByIdQuery, GeneralInformation?>,
    private val updateGeneralInformationHandler: ICommandHandler<UpdateGeneralInformationCommand, GeneralInformation>,
    private val deleteGeneralInformationHandler: ICommandHandler<DeleteGeneralInformationCommand, Unit>,
    private val createGeneralInformationMediaHandler: ICommandHandler<CreateMediaCommand, Media>,
    private val deleteGeneralInformationMediaHandler: ICommandHandler<DeleteMediaCommand, Unit>,
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

    suspend fun createGeneralInformationMedia(generalInformationId: String, command: CreateMediaCommand): Media {
        validateMediaCommand(command)
        return logger.executeWithExceptionLoggingAsync(
            operation = { createGeneralInformationMediaHandler.handleAsync(command) },
            logMessage = "Error uploading media for ${command.generalInformationId}",
            exceptionHandling = { ex ->
                APIException(
                    errorCode = ErrorCode.MEDIA_CREATION_ERROR,
                    message = "Failed to upload media for ${command.generalInformationId}",
                )
            }
        )
    }

    suspend fun deleteGeneralInformationMediaAsync(generalInformationId: String, mediaId: String) {
        logger.executeWithExceptionLoggingAsync(
            operation = {
                deleteGeneralInformationMediaHandler.handleAsync(DeleteMediaCommand(generalInformationId, mediaId))
            },
            logMessage = "Error deleting media $mediaId for $generalInformationId",
            exceptionHandling = { ex ->
                when (ex) {
                    is APIException -> ex
                    else -> APIException(
                        errorCode = ErrorCode.MEDIA_DELETION_ERROR,
                        message = "Unable to delete media with ID $mediaId for $generalInformationId",
                    )
                }
            }
        )
    }

    suspend fun validateMediaCommand(command: CreateMediaCommand) {
        val fileSize = command.fileData?.available()?.toLong() ?: 0

        if (fileSize > MAX_FILE_SIZE) {
            throw APIException(
                errorCode = ErrorCode.FILE_SIZE_EXCEEDED,
                message = "File size exceeds the maximum allowed size of ${MAX_FILE_SIZE / (1024 * 1024)}MB"
            )
        }
    }

    suspend fun detectFileTypeAndContentType(fileStream: InputStream): Pair<String, String> {
        val buffer = ByteArray(8)
        fileStream.mark(8)
        fileStream.read(buffer)
        fileStream.reset()

        val fileType = when {
            buffer.startsWith(byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())) -> ".jpg" to "image/jpeg"
            buffer.startsWith(byteArrayOf(0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte())) -> ".png" to "image/png"
            buffer.startsWith("RIFF".toByteArray()) && buffer.slice(8..11).toByteArray().contentEquals("WEBP".toByteArray()) -> ".webp" to "image/webp"
            buffer.startsWith(byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x18.toByte(), 0x66.toByte(), 0x74.toByte(), 0x79.toByte(), 0x70.toByte())) -> ".mp4" to "video/mp4"
            buffer.startsWith(byteArrayOf(0x1A.toByte(), 0x45.toByte(), 0xDF.toByte(), 0xA3.toByte())) -> ".mkv" to "video/x-matroska"
            buffer.startsWith("%PDF".toByteArray()) -> ".pdf" to "application/pdf"
            else -> throw APIException(
                errorCode = ErrorCode.MEDIA_CREATION_ERROR,
                message = "Unsupported file type"
            )
        }

        return fileType
    }


    private fun ByteArray.startsWith(prefix: ByteArray): Boolean =
        this.take(prefix.size).toByteArray().contentEquals(prefix)


    suspend fun generateFileName(fileType: String): String {
        return "${UUID.randomUUID()}$fileType"
    }
}