package org.ptss.support.infrastructure.persistence.entities

import com.azure.data.tables.models.TableEntity
import org.ptss.support.domain.models.EmergencyContact
import java.util.UUID

data class EmergencyContactEntity(
    var name: String = "",
    var phoneNumber: String = "",
    var actionLabel: String = ""
) {
    fun toTableEntity(emergencyContact: EmergencyContact): TableEntity {
        return TableEntity("EmergencyContact", emergencyContact.id.toString()).apply {
            properties["name"] = emergencyContact.name
            properties["phoneNumber"] = emergencyContact.phoneNumber
            properties["actionLabel"] = emergencyContact.actionLabel
        }
    }

    companion object {
        fun fromTableEntity(entity: TableEntity): EmergencyContactEntity {
            return EmergencyContactEntity(
                name = entity.properties["name"] as String,
                phoneNumber = entity.properties["phoneNumber"] as String,
                actionLabel = entity.properties["actionLabel"] as String
            )
        }
    }

    fun toDomain(): EmergencyContact = EmergencyContact(
        id = UUID.randomUUID(),
        name = name,
        phoneNumber = phoneNumber,
        actionLabel = actionLabel
    )
}