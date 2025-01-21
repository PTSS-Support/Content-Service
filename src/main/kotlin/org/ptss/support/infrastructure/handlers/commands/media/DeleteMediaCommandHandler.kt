package org.ptss.support.infrastructure.handlers.commands.media

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.core.services.BlobStorageService
import org.ptss.support.domain.commands.media.DeleteMediaCommand
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.infrastructure.repositories.GeneralInformationRepository
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory
import java.net.URI

@ApplicationScoped
class DeleteMediaCommandHandler(
    private val generalInformationRepository: GeneralInformationRepository,
    private val blobStorageService: BlobStorageService
) : ICommandHandler<DeleteMediaCommand, Unit> {
    private val logger = LoggerFactory.getLogger(DeleteMediaCommandHandler::class.java)

    override suspend fun handleAsync(command: DeleteMediaCommand) {
        return logger.executeWithExceptionLoggingAsync(
            operation = {
                val mediaUrl = generalInformationRepository.deleteMedia(command.generalInformationId, command.id)
                    ?: throw APIException(
                        errorCode = ErrorCode.MEDIA_NOT_FOUND,
                        message = "Media with ID ${command.id} not found"
                    )

                val blobName = extractBlobName(mediaUrl.url)
                blobStorageService.deleteFileAsync(blobName)
            },
            logMessage = "Error deleting media ${command.id}"
        )
    }

    private fun extractBlobName(url: String): String {
        val uri = URI(url)
        return uri.path.substringAfterLast("/")
    }
}