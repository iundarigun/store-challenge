package br.com.devcave.store.configuration

import br.com.devcave.store.configuration.FlywayProperties
import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FlywayConfiguration(
    private val flywayProperties: FlywayProperties
) {
    @Bean(initMethod = "migrate")
    fun flyway(): Flyway {
        val config = Flyway
            .configure()
            .dataSource(
                flywayProperties.url,
                flywayProperties.username,
                flywayProperties.password
            )
        return Flyway(config)
    }
}