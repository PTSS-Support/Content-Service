package org.ptss.support.domain.interfaces.repositories

import org.ptss.support.domain.models.GeneralInformation

interface IGeneralInformationRepository {
    suspend fun create(generalInformation: GeneralInformation): String
}