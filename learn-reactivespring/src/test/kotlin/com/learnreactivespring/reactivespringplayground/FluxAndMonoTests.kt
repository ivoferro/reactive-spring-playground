package com.learnreactivespring.reactivespringplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class FluxAndMonoTests {

    @Test
    fun fluxPlayground() {
        val flux: Flux<String> = Flux.just("This", "is", "SPARTAAAA")
            .concatWith(Flux.error(RuntimeException("TANATARANTA TANATARANTUM TANATARANTUn")))
            .concatWith(Flux.just("THIS IS SPARTA", "THIS IS SPARTAAAAAAAAAAAAA"))
            .log()

        flux.subscribe(
            System.out::println,
            System.err::println,
            System.err::println)
    }

    @Test
    fun readFlux() {
        val flux = Flux.just("This", "is", "SPARTAAAA")

        StepVerifier.create(flux)
            .expectNext("This")
            .expectNext("is")
            .expectNext("SPARTAAAA")
            .verifyComplete()

        StepVerifier.create(flux)
            .expectNext("This", "is", "SPARTAAAA")
            .verifyComplete()

        StepVerifier.create(flux)
            .expectNextCount(3)
            .verifyComplete()
    }

    @Test
    fun readFlux_runtimeException() {
        val flux: Flux<String> = Flux.just("This", "is", "SPARTAAAA")
            .concatWith(Flux.error(RuntimeException("TANATARANTA TANATARANTUM TANATARANTUn")))
            .concatWith(Flux.just("THIS IS SPARTA", "THIS IS SPARTAAAAAAAAAAAAA"))
            .log()

        StepVerifier.create(flux)
            .expectNext("This")
            .expectNext("is")
            .expectNext("SPARTAAAA")
            .expectError(RuntimeException::class.java)
            .verify()

        StepVerifier.create(flux)
            .expectNext("This", "is", "SPARTAAAA")
            .expectErrorMessage("TANATARANTA TANATARANTUM TANATARANTUn")
            .verify()
    }

    @Test
    fun readMono() {
        val mono = Mono.just("SPARTA").log()

        StepVerifier.create(mono)
            .expectNext("SPARTA")
            .verifyComplete()

        StepVerifier.create(mono)
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun readMono_runtimeException() {
        val mono = Mono.error<String>(RuntimeException("TANATARANTA TANATARANTUM TANATARANTUn")).log()

        StepVerifier.create(mono)
            .expectNextCount(0)
            .expectError(RuntimeException::class.java)
            .verify()

        StepVerifier.create(mono)
            .expectNextCount(0)
            .expectErrorMessage("TANATARANTA TANATARANTUM TANATARANTUn")
            .verify()
    }
}
