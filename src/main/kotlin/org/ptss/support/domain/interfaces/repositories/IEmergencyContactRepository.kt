package org.ptss.support.domain.interfaces.repositories

import org.ptss.support.domain.models.EmergencyContact

interface IEmergencyContactRepository {
    suspend fun getAll(): List<EmergencyContact>
    suspend fun update(id: String, name: String, phoneNumber: String, actionLabel: String): EmergencyContact?
}