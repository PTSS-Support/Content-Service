package org.ptss.support.domain.commands.media

import java.io.InputStream

data class CreateMediaCommand(
    val generalInformationId: String,
    val fileData: InputStream?,
    val href: String?
)
