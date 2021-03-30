package com.learnreactivespring.reactivespringplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.context.Context
import reactor.util.context.ContextView

class ContextualTest {
    @Test
    fun contextReadWrite() {
        val tenantKey = "X-Tenant"
        val tenantValue = "en_us"

        val mono = Flux.just("Operation", "Another operation")
            .flatMap { s: String ->
                Mono.deferContextual { ctx: ContextView ->
                    Mono.just("$s on tenant " + ctx.get(tenantKey))
                }
            }
            .contextWrite { ctx: Context ->
                ctx.put(tenantKey, tenantValue)
            }

        StepVerifier.create(mono)
            .expectNext("Operation on tenant en_us")
            .expectNext("Another operation on tenant en_us")
            .verifyComplete()
    }
}
