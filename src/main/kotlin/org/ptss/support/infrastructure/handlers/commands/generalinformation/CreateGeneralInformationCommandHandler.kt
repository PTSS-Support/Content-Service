package org.ptss.support.infrastructure.handlers.commands.generalinformation

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.domain.commands.generalinformation.CreateGeneralInformationCommand
import org.ptss.support.domain.interfaces.commands.ICommandHandler
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.infrastructure.repositories.GeneralInformationRepository
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory
import java.util.UUID

@ApplicationScoped
class CreateGeneralInformationCommandHandler(
    private val generalInformationRepository: GeneralInformationRepository
) : ICommandHandler<CreateGeneralInformationCommand, GeneralInformation> {
    private val logger = LoggerFactory.getLogger(CreateGeneralInformationCommandHandler::class.java)

    override suspend fun handleAsync(command: CreateGeneralInformationCommand): GeneralInformation {
        return logger.executeWithExceptionLoggingAsync(
            operation = {
                val generalInformation = GeneralInformation(
                    id = UUID.randomUUID(),
                    title = command.title,
                    content = command.content
                )
                generalInformationRepository.create(generalInformation)
                generalInformation
            },
            logMessage = "Error creating general information with title: ${command.title}",
        )
    }
}