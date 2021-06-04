package br.com.devcave.store.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.cache.CacheProperties
import org.springframework.boot.autoconfigure.cache.CacheType
import org.springframework.data.redis.core.ReactiveStringRedisTemplate

abstract class CacheService<T>(private val typeParameterClass: Class<T>) {

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var stringRedisTemplate: ReactiveStringRedisTemplate

    @Autowired
    private lateinit var cacheProperties: CacheProperties

    suspend fun verifyAndExecute(key: Any, block: suspend () -> T?): T? {
        if (cacheProperties.type != CacheType.REDIS) {
            return block.invoke()
        }
        val redisKey = "${cacheProperties.redis.keyPrefix}:${typeParameterClass.simpleName}:$key"
        val value = stringRedisTemplate.opsForValue().get(redisKey).awaitFirstOrNull()

        return value?.let { objectMapper.readValue(it, typeParameterClass) }
            ?: block.invoke().also {
                stringRedisTemplate.opsForValue()
                    .set(redisKey, objectMapper.writeValueAsString(it), cacheProperties.redis.timeToLive)
            }
    }
}