package org.ptss.support.infrastructure.util

import com.azure.data.tables.TableClient
import com.azure.data.tables.TableServiceClient
import com.azure.data.tables.TableServiceClientBuilder
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import org.ptss.support.common.exceptions.APIException
import org.ptss.support.domain.enums.ErrorCode
import org.slf4j.LoggerFactory

class TableStorageUtil private constructor(connectionString: String) {

    private val logger = LoggerFactory.getLogger(TableStorageUtil::class.java)
    private val tableServiceClient: TableServiceClient

    init {
        tableServiceClient = TableServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient()
    }

    fun getTableClient(tableName: String): TableClient {
        return try {
            tableServiceClient.createTableIfNotExists(tableName)
            tableServiceClient.getTableClient(tableName)
        } catch (e: Exception) {
            logger.error("Failed to get Table Client for table: $tableName", e)
            throw APIException(
                errorCode = ErrorCode.SERVICE_UNAVAILABLE,
                message = "Failed to get table client"
            )
        }
    }

    companion object {
        fun create(connectionString: String): TableStorageUtil {
            return TableStorageUtil(connectionString)
        }
    }
}
