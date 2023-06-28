package com.example.demo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest
@AutoConfigureWebTestClient
class DemoApplicationTests {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `GET hello endpoint is serving`() {
        val actual = webTestClient.get()
            .uri("/hello")
            .exchange()
            .expectStatus().isOk
            .expectBody<String>()
            .returnResult()
            .responseBody

        assertThat(actual).isEqualTo(
            "Hello from" +
                " ${System.getProperty("os.name")}" +
                " ${System.getProperty("os.version")}" +
                " (${System.getProperty("os.arch")})"
        )
    }
}
