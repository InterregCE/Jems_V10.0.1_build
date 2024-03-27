package io.cloudflight.jems.server.call.controller

import feign.auth.BasicAuthRequestInterceptor
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.cloud.openfeign.FeignClientsConfiguration
import org.springframework.context.annotation.Bean

@AutoConfiguration
class CustomFeignClientConfiguration : FeignClientsConfiguration() {
    @Bean
    fun basicAuthRequestInterceptor(): BasicAuthRequestInterceptor {
        return BasicAuthRequestInterceptor("admin@jems.eu", "Jems@2020admin@jems.eu")
    }
}
