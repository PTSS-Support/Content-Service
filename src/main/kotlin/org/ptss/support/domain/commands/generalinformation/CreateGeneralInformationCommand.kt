package org.ptss.support.domain.commands.generalinformation

data class CreateGeneralInformationCommand(
    val title: String,
    val content: String
)
