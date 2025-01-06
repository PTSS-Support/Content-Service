package org.ptss.support.infrastructure.util

import org.ptss.support.api.dtos.responses.pagination.PagedResult
import kotlin.math.ceil

object PaginationUtils {
    fun <T> List<T>.paginate(pageSize: Int, cursor: String?, idSelector: (T) -> String): PagedResult<T> {
        val startIndex = this.indexOfFirst { idSelector(it) == cursor }.let { if (it == -1) 0 else it }
        val items = this.drop(startIndex).take(pageSize)
        val nextCursor = if (this.size > startIndex + pageSize) {
            idSelector(this[startIndex + pageSize])
        } else null

        return PagedResult(
            data = items,
            nextCursor = nextCursor,
            pageSize = pageSize,
            totalItems = this.size,
            totalPages = ceil(this.size.toDouble() / pageSize).toInt()
        )
    }
}