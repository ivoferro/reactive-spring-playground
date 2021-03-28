package com.learnreactivespring.reactivespringplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.function.Supplier

class FluxAndMonoFactoryTest {
    object TestConstants {
        val NAMES = listOf("Ivo", "Ola", "Delgado", "Xico")
    }

    @Test
    fun fluxFromList() {
        val flux = Flux.fromIterable(TestConstants.NAMES).log()

        StepVerifier.create(flux)
            .expectNext("Ivo", "Ola", "Delgado", "Xico")
            .verifyComplete()
    }

    @Test
    fun fluxFromArray() {
        val flux = Flux.fromArray(TestConstants.NAMES.toTypedArray()).log()

        StepVerifier.create(flux)
            .expectNext("Ivo", "Ola", "Delgado", "Xico")
            .verifyComplete()
    }

    @Test
    fun fluxFromStream() {
        val flux = Flux.fromStream(TestConstants.NAMES.stream()).log()

        StepVerifier.create(flux)
            .expectNext("Ivo", "Ola", "Delgado", "Xico")
            .verifyComplete()
    }

    @Test
    fun monoJustOrEmpty() {
        val mono = Mono.justOrEmpty<String>(null).log()

        StepVerifier.create(mono)
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun monoFromSupplier() {
        val supplier = Supplier { "hello" }
        val mono = Mono.fromSupplier(supplier).log()

        StepVerifier.create(mono)
            .expectNext("hello")
            .verifyComplete()
    }

    @Test
    fun fluxFromRange() {
        val flux = Flux.range(1, 5).log()

        StepVerifier.create(flux)
            .expectNext(1, 2, 3 ,4 ,5)
            .verifyComplete()
    }
}