package org.ptss.support.infrastructure.config


import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithName

@ConfigMapping(prefix = "azure.storage")
interface AzureStorageConfig {
    @WithName("connection-string")
    fun connectionString(): String

    @WithName("general-information-table")
    fun generalInformationTable(): String

    @WithName("emergency-contact-table")
    fun emergencyContactTable(): String
}