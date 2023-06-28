package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
class DemoApplication {

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello from" +
            " ${System.getProperty("os.name")}" +
            " ${System.getProperty("os.version")}" +
            " (${System.getProperty("os.arch")})"
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
