package io.cloudflight.jems.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(exclude = [MailSenderAutoConfiguration::class])
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = ["io.cloudflight.jems.server", "io.cloudflight.jems.plugin"])
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
