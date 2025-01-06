package org.ptss.support.domain.queries.generalinformation

data class GetAllGeneralInformationQuery(
    val cursor: String? = null,
    val pageSize: Int = 20
)