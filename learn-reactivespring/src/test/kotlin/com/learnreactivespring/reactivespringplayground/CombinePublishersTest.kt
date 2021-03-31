package com.learnreactivespring.reactivespringplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Duration

class CombinePublishersTest {

    object TestConstants {
        val FLUX_1 = Flux.just("Unreal", "Unity").delayElements(Duration.ofSeconds(1))
        val FLUX_2 = Flux.just("libgdx", "XNA").delayElements(Duration.ofSeconds(1))
    }

    @Test
    fun merge() {
        val flux = Flux.merge(TestConstants.FLUX_1, TestConstants.FLUX_2).log()

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNextCount(4)
            .verifyComplete()
    }

    @Test
    fun concat() {
        val flux = Flux.concat(TestConstants.FLUX_1, TestConstants.FLUX_2).log()

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext("Unreal", "Unity", "libgdx", "XNA")
            .verifyComplete()
    }

    @Test
    fun zip() {
        val flux = Flux
            .zip(TestConstants.FLUX_1, TestConstants.FLUX_2, { f1, f2 -> "$f1 $f2" })
            .log()

        StepVerifier.create(flux)
            .expectSubscription()
            .expectNext("Unreal libgdx", "Unity XNA")
            .verifyComplete()
    }
}
