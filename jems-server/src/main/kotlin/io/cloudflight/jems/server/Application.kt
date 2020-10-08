package io.cloudflight.jems.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableCaching
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
