package org.ptss.support.domain.commands.media

data class DeleteMediaCommand(
    val generalInformationId: String,
    val id: String
)
