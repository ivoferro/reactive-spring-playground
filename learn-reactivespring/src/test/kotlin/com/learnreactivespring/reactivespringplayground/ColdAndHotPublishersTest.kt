package com.learnreactivespring.reactivespringplayground

import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import java.time.Duration

class ColdAndHotPublishersTest {
    @Test
    fun coldPublisherTest() {
        val flux = Flux.just("A", "B", "C", "D", "E", "F")
            .delayElements(Duration.ofMillis(500))
            .log()

        flux.subscribe { println("sub1 - $it") }

        Thread.sleep(1000)

        flux.subscribe { println("sub2 - $it") }

        Thread.sleep(6000)
    }

    @Test
    fun hotPublisherTest() {
        val flux = Flux.just("A", "B", "C", "D", "E", "F")
            .delayElements(Duration.ofMillis(500))
            .log()

        val connectableFlux = flux.publish()
        connectableFlux.connect()

        connectableFlux.subscribe { println("sub1 - $it") }

        Thread.sleep(1000)

        connectableFlux.subscribe { println("sub2 - $it") }

        Thread.sleep(6000)
    }
}