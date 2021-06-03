package br.com.devcave.store

import br.com.devcave.store.configuration.FlywayProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(value = [FlywayProperties::class])
class StoreApplication

fun main(args: Array<String>) {
	runApplication<StoreApplication>(*args)
}
