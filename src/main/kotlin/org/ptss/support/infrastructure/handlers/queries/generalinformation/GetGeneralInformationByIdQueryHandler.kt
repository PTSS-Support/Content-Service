package org.ptss.support.infrastructure.handlers.queries.generalinformation

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.domain.interfaces.queries.IQueryHandler
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.domain.models.Product
import org.ptss.support.domain.queries.GetProductByIdQuery
import org.ptss.support.domain.queries.generalinformation.GetGeneralInformationByIdQuery
import org.ptss.support.infrastructure.handlers.queries.product.GetProductByIdQueryHandler
import org.ptss.support.infrastructure.repositories.GeneralInformationRepository
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory

@ApplicationScoped
class GetGeneralInformationByIdQueryHandler(
    private val generalInformationRepository: GeneralInformationRepository
) : IQueryHandler<GetGeneralInformationByIdQuery, GeneralInformation?> {
    private val logger = LoggerFactory.getLogger(GetGeneralInformationByIdQueryHandler::class.java)

    override suspend fun handleAsync(query: GetGeneralInformationByIdQuery): GeneralInformation? {
        return logger.executeWithExceptionLoggingAsync(
            operation = {
                generalInformationRepository.getById(query.id)
            },
            logMessage = "Error retrieving general information with ID: ${query.id}"
        )
    }
}