package org.ptss.support.infrastructure.util

import org.slf4j.Logger

suspend fun <T> Logger.executeWithExceptionLoggingAsync(
    operation: suspend () -> T,
    logMessage: String,
    exceptionHandling: ((Exception) -> Exception)? = null,
    vararg args: Any,
): T {
    // Sanitize the arguments before logging them to prevent log injection
    val sanitizedArgs = args.map { sanitizeForLogging(it.toString()) }.toTypedArray()

    val sanitizedLogMessage = sanitizeForLogging(logMessage)
    return try {
        operation()
    } catch (ex: Exception) {
        // Format and log the sanitized message
        this.error(sanitizedLogMessage.format(*sanitizedArgs), ex)
        throw exceptionHandling?.invoke(ex) ?: ex
    }
}

// Utility function to sanitize user input for logging
fun sanitizeForLogging(input: String): String {
    return input.replace("\n", " ").replace("\r", " ")
}
