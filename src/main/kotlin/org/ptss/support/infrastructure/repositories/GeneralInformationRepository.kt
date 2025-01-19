package org.ptss.support.infrastructure.repositories

import com.azure.data.tables.TableClient
import com.azure.data.tables.models.TableEntityUpdateMode
import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.api.dtos.responses.pagination.PagedResult
import org.ptss.support.domain.interfaces.repositories.IGeneralInformationRepository
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.domain.models.Media
import org.ptss.support.infrastructure.config.AzureStorageConfig
import org.ptss.support.infrastructure.persistence.entities.GeneralInformationEntity
import org.ptss.support.infrastructure.util.PaginationUtils.paginate
import org.ptss.support.infrastructure.util.TableStorageUtil
import java.util.UUID

@ApplicationScoped
class GeneralInformationRepository(
    private val azureConfig: AzureStorageConfig
) : IGeneralInformationRepository {
    private val tableClient: TableClient = TableStorageUtil.create(azureConfig.connectionString())
        .getTableClient(azureConfig.generalInformationTable())

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
        val entity = tableClient.getEntity("GeneralInformation", id)
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
        val entity = tableClient.getEntity("GeneralInformation", id)

        val updatedEntity = entity.apply {
            properties["title"] = title
            properties["content"] = content
        }

        tableClient.updateEntity(updatedEntity)

        val updatedGeneralInformationEntity = GeneralInformationEntity.fromTableEntity(updatedEntity)
        return updatedGeneralInformationEntity.toDomain().copy(id = UUID.fromString(updatedEntity.rowKey))
    }

    override suspend fun delete(id: String): GeneralInformation? {
        val entity = tableClient.getEntity("GeneralInformation", id)
        val generalInformationEntity = GeneralInformationEntity.fromTableEntity(entity)
        tableClient.deleteEntity("GeneralInformation", id)
        return generalInformationEntity.toDomain().copy(id = UUID.fromString(entity.rowKey))
    }

    override suspend fun createMedia(id: String, media: Media?): GeneralInformation? {
        val entity = tableClient.getEntity("GeneralInformation", id)
        val updatedEntity = entity.apply {
            if (media != null) {
                properties["mediaId"] = media.mediaId.toString()
                properties["mediaUrl"] = media.url
                properties["mediaHref"] = media.href
            }
        }
        tableClient.updateEntity(updatedEntity)

        val updatedGeneralInformationEntity = GeneralInformationEntity.fromTableEntity(updatedEntity)
        return updatedGeneralInformationEntity.toDomain().copy(id = UUID.fromString(updatedEntity.rowKey))
    }

    override suspend fun deleteMedia(generalInformationId: String, mediaId: String): Media? {
        val entity = tableClient.getEntity("GeneralInformation", generalInformationId)
        val media = GeneralInformationEntity.fromTableEntity(entity).toDomain().media

        tableClient.updateEntity(
            entity.apply {
                listOf("mediaId", "mediaUrl", "mediaHref").forEach {
                    properties.remove(it)
                }
            },
            TableEntityUpdateMode.REPLACE
        )

        return media
    }
}