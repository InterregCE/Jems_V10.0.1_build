package io.cloudflight.ems.client.autoconfigure

import io.cloudflight.ems.client.CallClient
import io.cloudflight.ems.client.ProjectClient
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

/**
 * Please note that in case you require authentication on your feign clients on the client side
 * (i.e. OAuth Request Interceptors) it may make sense to configure that here in that auto configuration already, but
 * you can also leave that up for the clients
 */
@Configuration
@EnableFeignClients(basePackageClasses = [ProjectClient::class, CallClient::class])
class EmsClientAutoConfiguration
