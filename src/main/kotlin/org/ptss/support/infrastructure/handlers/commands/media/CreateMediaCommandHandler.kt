package org.ptss.support.infrastructure.handlers.commands.media

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.core.services.BlobStorageService
import org.ptss.support.domain.commands.media.CreateMediaCommand
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.domain.models.Media
import org.ptss.support.infrastructure.repositories.GeneralInformationRepository
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory
import java.util.*

@ApplicationScoped
class CreateMediaCommandHandler(
    private val generalInformationRepository: GeneralInformationRepository,
    private val blobStorageService: BlobStorageService
) : ICommandHandler<CreateMediaCommand, Media> {

    private val logger = LoggerFactory.getLogger(CreateMediaCommandHandler::class.java)

    override suspend fun handleAsync(command: CreateMediaCommand): Media {
        return logger.executeWithExceptionLoggingAsync(
            operation = {
                val mediaId = UUID.randomUUID()

                val blobUrl = blobStorageService.uploadFileAsync(
                    command.fileData ?: throw APIException(
                        errorCode = ErrorCode.MEDIA_NOT_FOUND,
                        message = "File data is required"
                    )
                )

                val publicUrl = blobStorageService.getPublicBlobUrl(blobUrl.substringAfterLast("/"))

                val media = Media(
                    mediaId = mediaId,
                    url = publicUrl,
                    href = command.href
                )

                generalInformationRepository.createMedia(command.generalInformationId, media)
                    ?: throw APIException(
                        errorCode = ErrorCode.GENERAL_INFORMATION_NOT_FOUND,
                        message = "General information not found"
                    )

                media
            },
            logMessage = "Error creating media for general information ${command.generalInformationId}"
        )
    }
}