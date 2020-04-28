package io.cloudflight.ems.controller

import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.api.HelloWorldApi
import io.cloudflight.ems.api.dto.OutputGreeting
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController(
    private val securityService: SecurityService
) : HelloWorldApi {

    override fun getHello(): OutputGreeting {
        return OutputGreeting("Hello ;)", "Pleasure to meet you!")
    }

    override fun getHelloAdmin(name: String): OutputGreeting {
        securityService.assertAdminAccess()
        return OutputGreeting(
            "Hello $name ;)",
            "Pleasure to meet you $name!"
        )
    }
}
