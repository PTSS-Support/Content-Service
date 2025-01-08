package org.ptss.support.infrastructure.persistence.entities

import com.azure.data.tables.models.TableEntity
import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.domain.models.Media
import java.util.*

data class GeneralInformationEntity(
    var id: UUID? = null,
    var title: String = "",
    var content: String = "",
    var mediaUrl: String? = null,
    var mediaHref: String? = null,
    var mediaId: String? = null
) {
    fun toTableEntity(generalInformation: GeneralInformation): TableEntity {
        return TableEntity("GeneralInformation", generalInformation.id.toString()).apply {
            properties.apply {
                put("title", title)
                put("content", content)
                generalInformation.media?.let {
                    put("mediaId", it.mediaId.toString())
                    put("mediaUrl", it.url)
                    put("mediaHref", it.href)
                }
            }
        }
    }

    companion object {
        fun fromTableEntity(entity: TableEntity): GeneralInformationEntity {
            return GeneralInformationEntity(
                id = UUID.fromString(entity.rowKey),  // ✅ Set ID here
                title = entity.properties["title"] as String,
                content = entity.properties["content"] as String,
                mediaId = entity.properties["mediaId"] as String?,
                mediaUrl = entity.properties["mediaUrl"] as String?,
                mediaHref = entity.properties["mediaHref"] as String?
            )
        }
    }

    fun toDomain(): GeneralInformation = GeneralInformation(
        id = id ?: UUID.randomUUID(),  // ✅ Use the `id` property
        title = title,
        content = content,
        media = if (mediaId != null && mediaUrl != null) {
            Media(
                mediaId = UUID.fromString(mediaId),
                url = mediaUrl!!,
                href = mediaHref
            )
        } else null
    )
}

