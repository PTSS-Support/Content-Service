package org.ptss.support.infrastructure.repositories

import com.azure.data.tables.TableClient
import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.domain.interfaces.repositories.IEmergencyContactRepository
import org.ptss.support.domain.models.EmergencyContact
import org.ptss.support.infrastructure.config.AzureStorageConfig
import org.ptss.support.infrastructure.persistence.entities.EmergencyContactEntity
import org.ptss.support.infrastructure.util.TableStorageUtil
import java.util.UUID

@ApplicationScoped
class EmergencyContactRepository(
    private val azureConfig: AzureStorageConfig
) : IEmergencyContactRepository {
    private val tableClient: TableClient = TableStorageUtil.create(azureConfig.connectionString())
        .getTableClient(azureConfig.emergencyContactTable())

    override suspend fun getAll(): List<EmergencyContact> {
        val entities = tableClient.listEntities()
        return entities.map { entity ->
            EmergencyContactEntity.fromTableEntity(entity).toDomain().copy(id = UUID.fromString(entity.rowKey))
        }
    }

    override suspend fun update(id: String, name: String, phoneNumber: String, actionLabel: String): EmergencyContact? {
        val entity = tableClient.getEntity("EmergencyContact", id)

        val updatedEntity = entity.apply {
            properties["name"] = name
            properties["phoneNumber"] = phoneNumber
            properties["actionLabel"] = actionLabel
        }

        tableClient.updateEntity(updatedEntity)

        val updatedEmergencyContactEntity = EmergencyContactEntity.fromTableEntity(updatedEntity)
        return updatedEmergencyContactEntity.toDomain().copy(id = UUID.fromString(updatedEntity.rowKey))
    }
}