package br.com.devcave.store.controller.handler

data class ErrorResponse(
    val traceId: String,
    val messages: List<String?>
)