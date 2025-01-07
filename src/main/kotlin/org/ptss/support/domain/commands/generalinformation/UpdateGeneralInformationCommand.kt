package org.ptss.support.domain.commands.generalinformation

data class UpdateGeneralInformationCommand(
    val id: String,
    val title: String,
    val content: String
)
