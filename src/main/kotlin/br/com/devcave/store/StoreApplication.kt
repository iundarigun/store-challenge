package br.com.devcave.store

import br.com.devcave.store.configuration.FlywayProperties
import kotlinx.coroutines.debug.CoroutinesBlockHoundIntegration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import reactor.blockhound.BlockHound
import reactor.blockhound.integration.BlockHoundIntegration

@SpringBootApplication
@EnableConfigurationProperties(value = [FlywayProperties::class])
class StoreApplication {
    init {
        val javaUUIDIntegration = BlockHoundIntegration { builder ->
            builder.allowBlockingCallsInside("java.util.UUID", "randomUUID")
                .allowBlockingCallsInside("java.io.InputStream", "readNBytes")
                .allowBlockingCallsInside("java.io.FilterInputStream", "read")
        }
        BlockHound.install(CoroutinesBlockHoundIntegration(), javaUUIDIntegration)
    }
}

fun main(args: Array<String>) {
    runApplication<StoreApplication>(*args)
}
