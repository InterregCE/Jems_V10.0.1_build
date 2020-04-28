package io.cloudflight.skeleton.client

import io.cloudflight.skeleton.angular.api.HelloWorldApi
import org.springframework.cloud.openfeign.FeignClient

/**
 * @author Harald Radi (harald.radi@catalysts.cc)
 * @version 1.0
 */
@FeignClient(name = "helloWorld", url = "\${skeleton.url}")
interface HelloWorldClient : HelloWorldApi
