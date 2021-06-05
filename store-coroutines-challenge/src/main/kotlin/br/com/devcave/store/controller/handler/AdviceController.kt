package br.com.devcave.store.controller.handler

import br.com.devcave.store.exception.ApplicationException
import brave.Tracer
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.validation.ConstraintViolationException

@ControllerAdvice
class AdviceController(
    private val tracer: Tracer
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(exception: ConstraintViolationException): HttpEntity<Any?> {
        logger.warn("handleConstraintViolationException", exception)
        val messages = exception.constraintViolations.map { cv ->
            cv.propertyPath.toString()
                .split(".")
                .toMutableList().also {
                    it.removeAt(0)
                }.joinToString(".") + ": ${cv.message}"
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                tracer.currentSpan().context().traceIdString(),
                messages
            )
        )
    }

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(
        exception: ApplicationException
    ): HttpEntity<Any?> {
        logger.warn("handleApplicationException", exception)
        return ResponseEntity
            .status(exception.status)
            .body(
                ErrorResponse(
                    tracer.currentSpan().context().traceIdString(),
                    listOf(exception.message)
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(
        exception: Exception
    ): HttpEntity<Any?> {
        logger.warn("handleException", exception)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ErrorResponse(
                    tracer.currentSpan().context().traceIdString(),
                    listOf(exception.message ?: "Unexpected error")
                )
            )
    }
}
