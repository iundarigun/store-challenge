package br.com.devcave.store.exception

import org.springframework.http.HttpStatus

class ApplicationException(val status: HttpStatus, message: String) : RuntimeException(message)
