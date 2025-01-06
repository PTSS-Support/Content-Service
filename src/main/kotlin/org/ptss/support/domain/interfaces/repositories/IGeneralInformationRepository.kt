package org.ptss.support.domain.interfaces.repositories

import org.ptss.support.domain.models.GeneralInformation
import org.ptss.support.domain.models.Product

interface IGeneralInformationRepository {
    suspend fun create(generalInformation: GeneralInformation): String
    suspend fun getAll(): List<GeneralInformation>
}