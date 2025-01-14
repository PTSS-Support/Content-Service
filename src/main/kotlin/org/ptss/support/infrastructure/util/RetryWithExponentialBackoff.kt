package org.ptss.support.infrastructure.util

import kotlinx.coroutines.delay
import java.io.IOException

suspend fun <T> retryWithExponentialBackoff(
    maxRetries: Int = 3,
    initialDelay: Long = 500L,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay

    repeat(maxRetries - 1) {
        try {
            return block()
        } catch (e: IOException) {
            delay(currentDelay)
            currentDelay *= 2
        }
    }

    return block() // Final attempt
}
