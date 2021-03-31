package com.learnreactivespring.reactivespringplayground

import org.junit.jupiter.api.Test
import reactor.core.Exceptions
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import reactor.util.retry.Retry
import java.time.Duration


class PublishersErrorHandling {

    @Test
    fun onErrorResume() {
        val flux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(RuntimeException("Very big bug")))
            .concatWith(Flux.just("D"))
            .onErrorResume { e ->
                println("Bad error: $e")
                Flux.just("fallback")
            }
            .log()

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext("A", "B", "C", "fallback")
            .verifyComplete()
    }

    @Test
    fun onErrorReturn() {
        val flux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(RuntimeException("Very big bug")))
            .concatWith(Flux.just("D"))
            .onErrorReturn("fallback")
            .log()

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext("A", "B", "C", "fallback")
            .verifyComplete()
    }

    @Test
    fun onErrorMap() {
        val flux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(RuntimeException("Very big bug")))
            .concatWith(Flux.just("D"))
            .onErrorMap { e -> IllegalArgumentException(e) }
            .log()

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectError(IllegalArgumentException::class.java)
            .verify()
    }

    @Test
    fun onErrorMapWithRetry() {
        val flux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(RuntimeException("Very big bug")))
            .concatWith(Flux.just("D"))
            .onErrorMap { e -> IllegalArgumentException(e) }
            .retry(2)
            .log()

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectError(IllegalArgumentException::class.java)
            .verify()
    }

    @Test
    fun onErrorMapWithRetryBackoff() {
        val flux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(RuntimeException("Very big bug")))
            .concatWith(Flux.just("D"))
            .retryWhen(Retry.backoff(2, Duration.ofSeconds(2)))
            .onErrorMap { e -> IllegalArgumentException(e) }
            .log()

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectError(IllegalArgumentException::class.java)
            .verify()
    }

    @Test
    fun onErrorMapWithRetrySpec() {
        val retrySpec: Retry = Retry.fixedDelay(2, Duration.ofMillis(1000))
            .filter { ex -> ex is RuntimeException }
            .onRetryExhaustedThrow { _, retrySignal -> Exceptions.propagate(retrySignal.failure()) }

        val flux = Flux.just("A", "B", "C")
            .concatWith(Flux.error(RuntimeException("Very big bug")))
            .concatWith(Flux.just("D"))
            .retryWhen(retrySpec)
            .onErrorMap { e -> IllegalArgumentException(e) }
            .log()

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectNext("A", "B", "C")
            .expectError(IllegalArgumentException::class.java)
            .verify()
    }
}
