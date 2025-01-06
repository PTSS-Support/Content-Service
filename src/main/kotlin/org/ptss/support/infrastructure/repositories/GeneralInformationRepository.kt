package org.ptss.support.infrastructure.repositories

import com.azure.data.tables.TableClient
import com.azure.data.tables.TableServiceClientBuilder
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.enums.ErrorCode
import org.ptss.support.domain.interfaces.repositories.IGeneralInformationRepository
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.infrastructure.config.AzureStorageConfig
import org.ptss.support.infrastructure.persistence.entities.GeneralInformationEntity
import org.ptss.support.infrastructure.util.PaginationUtils
import org.ptss.support.infrastructure.util.PaginationUtils.paginate
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.math.ceil

@ApplicationScoped
class GeneralInformationRepository(
    private val azureConfig: AzureStorageConfig
) : IGeneralInformationRepository {
    private val logger = LoggerFactory.getLogger(GeneralInformationRepository::class.java)
    private lateinit var tableClient: TableClient

    @PostConstruct
    fun initialize() {
        try {
            val tableServiceClient = TableServiceClientBuilder()
                .connectionString(azureConfig.connectionString())
                .buildClient()
            tableServiceClient.createTableIfNotExists(azureConfig.tableName())
            tableClient = tableServiceClient.getTableClient(azureConfig.tableName())
        } catch (e: Exception) {
            logger.error("Failed to initialize Azure Table Storage", e)
            throw APIException(
                errorCode = ErrorCode.SERVICE_UNAVAILABLE,
                message = "Failed to initialize storage service",
            )
        }
    }

    override suspend fun getAll(cursor: String?, pageSize: Int): PagedResult<GeneralInformation> {
        val entities = tableClient.listEntities()
            .map { entity ->
                GeneralInformationEntity.fromTableEntity(entity)
                    .toDomain()
                    .copy(id = UUID.fromString(entity.rowKey))
            }
            .toList()

        return entities.paginate(pageSize, cursor) { it.id.toString() }
    }

    override suspend fun getById(id: String): GeneralInformation? {
        val entity = tableClient.getEntity("GENERAL_INFORMATION", id)
        val generalInformationEntity = GeneralInformationEntity.fromTableEntity(entity)
        return generalInformationEntity.toDomain().copy(id = UUID.fromString(entity.rowKey))
    }


    override suspend fun create(generalInformation: GeneralInformation): String {
        val generalInformationEntity = GeneralInformationEntity(
            title = generalInformation.title,
            content = generalInformation.content
        )
        val tableEntity = generalInformationEntity.toTableEntity(generalInformation)
        tableClient.createEntity(tableEntity)
        return generalInformation.id.toString()
    }

    override suspend fun update(id: String, title: String, content: String, ): GeneralInformation? {
        val entity = tableClient.getEntity("GENERAL_INFORMATION", id)

        val updatedEntity = entity.apply {
            properties["title"] = title
            properties["content"] = content
        }

        tableClient.updateEntity(updatedEntity)

        val updatedGeneralInformationEntity = GeneralInformationEntity.fromTableEntity(updatedEntity)
        return updatedGeneralInformationEntity.toDomain().copy(id = UUID.fromString(updatedEntity.rowKey))
    }

    override suspend fun delete(id: String): GeneralInformation? {
        val entity = tableClient.getEntity("GENERAL_INFORMATION", id)
        val generalInformationEntity = GeneralInformationEntity.fromTableEntity(entity)
        tableClient.deleteEntity("GENERAL_INFORMATION", id)
        return generalInformationEntity.toDomain().copy(id = UUID.fromString(entity.rowKey))
    }
}