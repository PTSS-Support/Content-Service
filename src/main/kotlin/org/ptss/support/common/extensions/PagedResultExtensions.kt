package org.ptss.support.common.extensions

import org.ptss.support.api.dtos.responses.pagination.PagedResult

fun <T, R> PagedResult<T>.mapData(transform: (T) -> R): PagedResult<R> =
    PagedResult(
        data = data.map(transform),
        nextCursor = nextCursor,
        pageSize = pageSize,
        totalItems = totalItems,
        totalPages = totalPages
    )