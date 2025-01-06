package org.ptss.support.infrastructure.handlers.queries.generalinformation

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.domain.interfaces.queries.IQueryHandler
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.domain.queries.generalinformation.GetAllGeneralInformationQuery
import org.ptss.support.infrastructure.repositories.GeneralInformationRepository
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory

@ApplicationScoped
class GetAllGeneralInformationQueryHandler(
    private val generalInformationRepository: GeneralInformationRepository
) : IQueryHandler<GetAllGeneralInformationQuery, List<GeneralInformation>> {
    private val logger = LoggerFactory.getLogger(GetAllGeneralInformationQueryHandler::class.java)

    override suspend fun handleAsync(query: GetAllGeneralInformationQuery): List<GeneralInformation> {
        return logger.executeWithExceptionLoggingAsync(
            operation = {
                generalInformationRepository.getAll()
            },
            logMessage = "Error retrieving all general information"
        )
    }
}