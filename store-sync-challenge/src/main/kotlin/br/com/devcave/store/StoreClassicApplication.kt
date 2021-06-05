package br.com.devcave.store

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.redis.core.StringRedisTemplate

@EnableCaching
@SpringBootApplication
class StoreClassicApplication(redisTemplate: StringRedisTemplate?) {
    init {
        // Only for develop propose
        redisTemplate?.delete(redisTemplate.keys("*"))
    }
}

fun main(args: Array<String>) {
    runApplication<StoreClassicApplication>(*args)
}
