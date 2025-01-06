package org.ptss.support.infrastructure.persistence.entities

import com.azure.data.tables.models.TableEntity
import org.ptss.support.domain.models.GeneralInformation
import java.util.*

data class GeneralInformationEntity(
    var title: String = "",
    var content: String = ""
) {
    fun toTableEntity(generalInformation: GeneralInformation): TableEntity {
        return TableEntity("GENERAL_INFORMATION", generalInformation.id.toString()).apply {
            properties.apply {
                put("title", title)
                put("content", content)
            }
        }
    }

    companion object {
        fun fromTableEntity(entity: TableEntity): GeneralInformationEntity {
            return GeneralInformationEntity(
                title = entity.properties["title"] as String,
                content = entity.properties["content"] as String
            )
        }
    }

    fun toDomain(): GeneralInformation = GeneralInformation(
        id = UUID.randomUUID(),
        title = title,
        content = content
    )
}
