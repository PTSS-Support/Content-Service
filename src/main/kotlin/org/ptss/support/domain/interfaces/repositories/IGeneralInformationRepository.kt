package org.ptss.support.domain.interfaces.repositories

import org.ptss.support.domain.models.GeneralInformation

interface IGeneralInformationRepository {
    suspend fun getAll(): List<GeneralInformation>
    suspend fun getById(id: String): GeneralInformation?
    suspend fun create(generalInformation: GeneralInformation): String
    suspend fun update(id: String, title: String, content: String): GeneralInformation?
    suspend fun delete(id: String): GeneralInformation?
}