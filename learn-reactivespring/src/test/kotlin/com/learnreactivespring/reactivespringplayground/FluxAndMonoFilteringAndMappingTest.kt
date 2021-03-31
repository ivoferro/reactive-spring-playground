package com.learnreactivespring.reactivespringplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers.parallel
import reactor.test.StepVerifier

class FluxAndMonoFilteringAndMappingTest {
    object TestConstants {
        val NAMES = listOf("Ivo", "Ola", "Delgado", "Xico")
    }

    @Test
    fun filterFlux() {
        val flux = Flux.fromIterable(TestConstants.NAMES)
            .filter { name -> name.length > 3 }
            .log()

        StepVerifier.create(flux)
            .expectNext("Delgado", "Xico")
            .verifyComplete()
    }

    @Test
    fun mapFlux() {
        val flux = Flux.fromIterable(TestConstants.NAMES)
            .filter { name -> name.length > 3 }
            .map { it.toUpperCase() }
            .log()

        StepVerifier.create(flux)
            .expectNext("DELGADO", "XICO")
            .verifyComplete()
    }

    @Test
    fun repeatFlux() {
        val flux = Flux.fromIterable(TestConstants.NAMES)
            .filter { name -> name.length > 3 }
            .map { it.toUpperCase() }
            .repeat(2)
            .log()

        StepVerifier.create(flux)
            .expectNext("DELGADO", "XICO", "DELGADO", "XICO", "DELGADO", "XICO")
            .verifyComplete()
    }

    @Test
    fun flatMap() {
        val flux = Flux.fromIterable(TestConstants.NAMES)
            .flatMap { name ->
                Flux.fromIterable(delayedTransform(name))
            }
            .log()

        StepVerifier.create(flux)
            .expectNextCount(8)
            .verifyComplete()
    }

    @Test
    fun flatMapParallel() {
        val flux = Flux.fromIterable(TestConstants.NAMES)
            .window(2)
            .flatMap { namesBatch ->
                namesBatch.map(this::delayedTransform).subscribeOn(parallel())
            }
            .flatMap { Flux.fromIterable(it) }
            .log()

        StepVerifier.create(flux)
            .expectNextCount(8)
            .verifyComplete()
    }

    @Test
    fun concatMapParallel() {
        val flux = Flux.fromIterable(TestConstants.NAMES)
            .window(2)
            .concatMap { namesBatch ->
                namesBatch.map(this::delayedTransform).subscribeOn(parallel())
            }
            .flatMap { Flux.fromIterable(it) }
            .log()

        StepVerifier.create(flux)
            .expectNextCount(8)
            .verifyComplete()
    }

    @Test
    fun flatMapSequentialParallel() {
        val flux = Flux.fromIterable(TestConstants.NAMES)
            .window(2)
            .flatMapSequential { namesBatch ->
                namesBatch.map(this::delayedTransform).subscribeOn(parallel())
            }
            .flatMap { Flux.fromIterable(it) }
            .log()

        StepVerifier.create(flux)
            .expectNextCount(8)
            .verifyComplete()
    }

    private fun delayedTransform(toTransform: String): List<String> {
        Thread.sleep(1000)
        return listOf("$toTransform good", "$toTransform impec")
    }
}
