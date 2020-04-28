package io.cloudflight.ems.client

import io.cloudflight.ems.api.HelloWorldApi
import org.springframework.cloud.openfeign.FeignClient

/**
 * @author Harald Radi (harald.radi@catalysts.cc)
 * @version 1.0
 */
@FeignClient(name = "helloWorld", url = "\${ems.url}")
interface HelloWorldClient : HelloWorldApi
