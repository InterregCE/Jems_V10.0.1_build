package io.cloudflight.skeleton.client

import io.cloudflight.skeleton.angular.api.UserApi
import org.springframework.cloud.openfeign.FeignClient

/**
 * @author Harald Radi (harald.radi@catalysts.cc)
 * @version 1.0
 */
@FeignClient(name = "user", url = "\${skeleton.url}")
interface UserClient : UserApi
