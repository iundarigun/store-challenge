package br.com.devcave.store.service

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.boot.autoconfigure.cache.CacheProperties
import org.springframework.boot.autoconfigure.cache.CacheType
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.hasKeyAndAwait
import org.springframework.data.redis.core.rangeAsFlow
import org.springframework.data.redis.core.rightPushAndAwait
import org.springframework.stereotype.Service

@Service
class CacheService(
    private val objectMapper: ObjectMapper,
    private val stringRedisTemplate: ReactiveStringRedisTemplate,
    private val cacheProperties: CacheProperties
) {
    suspend fun <T> verifyAndExecute(key: Any, typeParameterClass: Class<T>, block: suspend () -> T?): T? {
        if (cacheProperties.type != CacheType.REDIS) {
            return block()
        }
        val redisKey = "${cacheProperties.redis.keyPrefix}:${typeParameterClass.simpleName}:$key"
        val value = stringRedisTemplate.opsForValue().get(redisKey).awaitFirstOrNull()

        return value?.let { objectMapper.readValue(it, typeParameterClass) }
            ?: block().also {
                stringRedisTemplate.opsForValue()
                    .set(redisKey, objectMapper.writeValueAsString(it), cacheProperties.redis.timeToLive)
                    .awaitFirstOrNull()
            }
    }

    suspend fun <T> verifyAndExecuteFlow(
        key: Any,
        typeParameterClass: Class<T>,
        block: suspend () -> Flow<T>
    ): Flow<T> {
        if (cacheProperties.type != CacheType.REDIS) {
            return block()
        }
        val redisKey = "${cacheProperties.redis.keyPrefix}:${typeParameterClass.simpleName}:$key"

        return if (stringRedisTemplate.hasKeyAndAwait(redisKey)) {
            stringRedisTemplate.opsForList()
                .rangeAsFlow(redisKey, 0, -1)
                .map { objectMapper.readValue(it, typeParameterClass) }
        } else {
            block().map {
                stringRedisTemplate.opsForList()
                    .rightPushAndAwait(redisKey, objectMapper.writeValueAsString(it))
                it
            }.onCompletion {
                stringRedisTemplate.expire(redisKey, cacheProperties.redis.timeToLive).awaitSingle()
            }
        }
    }
}