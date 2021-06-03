package br.com.devcave.store.controller.handler

import br.com.devcave.store.exception.ApplicationException
import brave.Tracer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.validation.ConstraintViolationException

@ControllerAdvice
class AdviceController(
    private val tracer: Tracer
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handlerMethodArgumentNotValidException(
        exception: MethodArgumentNotValidException
    ): HttpEntity<Any?> {
        logger.warn("handlerMethodArgumentNotValidException", exception)
        val messages = if (exception.bindingResult.allErrors.isNotEmpty()) {
            exception.bindingResult.allErrors.map {
                "${(it as FieldError).field}: ${it.defaultMessage}"
            }
        } else {
            listOf("Bad request")
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                tracer.currentSpan().context().traceIdString(),
                messages
            )
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handlerHttpMessageNotReadableException(
        exception: HttpMessageNotReadableException
    ): HttpEntity<Any?> {
        logger.warn("handlerHttpMessageNotReadableException", exception)
        val messages = mutableListOf<String>()
        val cause = exception.cause

        if (cause is JsonMappingException) {
            val messageCause = when (cause) {
                is MissingKotlinParameterException -> "is missing"
                is InvalidFormatException -> "can not parse"
                is MismatchedInputException -> "Invalid content"
                else -> "is invalid"
            }
            messages.addAll(cause.path.filter { it.fieldName != null }
                .map {
                    "${it.fieldName}: $messageCause"
                })
            messages.ifEmpty { messages.add(messageCause) }
        }
        messages.ifEmpty { messages.add("Bad request") }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                tracer.currentSpan().context().traceIdString(),
                messages
            )
        )
    }

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
