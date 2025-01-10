package org.ptss.support.infrastructure.handlers.queries.emergencycontact

import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.domain.interfaces.queries.IQueryHandler
import org.ptss.support.domain.models.EmergencyContact
import org.ptss.support.domain.queries.emergencycontact.GetAllEmergencyContactsQuery
import org.ptss.support.infrastructure.repositories.EmergencyContactRepository
import org.ptss.support.infrastructure.util.executeWithExceptionLoggingAsync
import org.slf4j.LoggerFactory

@ApplicationScoped
class GetAllEmergencyContactsQueryHandler(
    private val emergencyContactRepository: EmergencyContactRepository
) : IQueryHandler<GetAllEmergencyContactsQuery, List<EmergencyContact>> {
    private val logger = LoggerFactory.getLogger(GetAllEmergencyContactsQueryHandler::class.java)

    override suspend fun handleAsync(query: GetAllEmergencyContactsQuery): List<EmergencyContact> {
        return logger.executeWithExceptionLoggingAsync(
            operation = {
                emergencyContactRepository.getAll()
            },
            logMessage = "Error retrieving all emergency contacts"
        )
    }
}