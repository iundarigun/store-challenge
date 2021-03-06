package br.com.devcave.store.configuration

import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

@Configuration
class DockerConfiguration {

    companion object {
        val postgres: GenericContainer<Nothing> = GenericContainer<Nothing>("postgres").also {
            it.withExposedPorts(5432)
            it.portBindings.add("55432:5432")
            it.addEnv("POSTGRES_USER", "test")
            it.addEnv("POSTGRES_PASSWORD", "test")
            it.waitingFor(Wait.forListeningPort())
            it.start()
        }

        val redis: GenericContainer<Nothing> = GenericContainer<Nothing>("redis").also {
            it.withExposedPorts(6379)
            it.portBindings.add("16379:6379")
            it.waitingFor(Wait.forListeningPort())
            it.start()
        }
    }
}