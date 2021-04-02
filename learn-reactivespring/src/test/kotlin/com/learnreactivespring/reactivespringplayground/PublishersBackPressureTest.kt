package com.learnreactivespring.reactivespringplayground

import org.junit.jupiter.api.Test
import org.reactivestreams.Subscription
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.Flux
import reactor.core.publisher.SignalType
import reactor.test.StepVerifier
import java.time.Duration
import kotlin.math.pow

class PublishersBackPressureTest {

    @Test
    fun infiniteTest() {
        val flux = Flux.interval(Duration.ofMillis(500))
            .map { num -> 2.0.pow(num.plus(1).toDouble()) }
            .take(12)
            .log()

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNextCount(12)
            .verifyComplete()
    }

    @Test
    fun backPressureTest() {
        val flux = Flux.interval(Duration.ofMillis(100))
            .map { num -> 2.0.pow(num.plus(1).toDouble()) }
            .log()

        StepVerifier.create(flux)
            .expectSubscription()
            .thenRequest(2)
            .expectNext(2.0, 4.0)
            .thenRequest(1)
            .expectNext(8.0)
            .thenCancel()
            .verify()
    }

    @Test
    fun backPressureManually() {
        Flux.interval(Duration.ofMillis(100))
            .map { num -> 2.0.pow(num.plus(1).toDouble()) }
            .log()
            .subscribe(
                { value -> println("value is $value") },
                { error -> println("Bad error occurred: $error") },
                { println("completed") }
            )

        Thread.sleep(5000)
    }

    @Test
    fun backPressureManuallySubscribeWith() {
        Flux.interval(Duration.ofMillis(100))
            .map { num -> 2.0.pow(num.plus(1).toDouble()) }
            .log()
            .subscribeWith(object : BaseSubscriber<Double>() {
                var currentRequestCount = 0
                val totalRequests = 6L;

                override fun hookOnSubscribe(subscription: Subscription) {
                    request(totalRequests)
                }

                override fun hookOnNext(value: Double) {
                    currentRequestCount++

                    println("value is $value")

                    if (currentRequestCount >= totalRequests) {
                        cancel()
                    }
                }

                override fun hookOnError(throwable: Throwable) {
                    println("Bad error occurred: $throwable")
                }

                override fun hookOnComplete() {
                    println("Completed")
                }

                override fun hookFinally(type: SignalType) {
                    println("Finally bro: $type")
                }
            })

        Thread.sleep(5000)
    }
}
