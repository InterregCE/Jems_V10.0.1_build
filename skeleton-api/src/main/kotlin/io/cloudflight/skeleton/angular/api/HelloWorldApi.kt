package io.cloudflight.skeleton.angular.api

import io.cloudflight.skeleton.angular.api.dto.OutputGreeting
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("HelloWorld")
@RequestMapping("/api")
interface HelloWorldApi {

    @ApiOperation("Returns a greeting from the server")
    @GetMapping("/hello")
    fun getHello(): OutputGreeting

    @ApiOperation("Returns a greeting from the admin")
    @GetMapping("/hello-admin")
    fun getHelloAdmin(@RequestParam() name: String): OutputGreeting
}
