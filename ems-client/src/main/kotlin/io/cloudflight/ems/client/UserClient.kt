package io.cloudflight.ems.client

import io.cloudflight.ems.api.UserApi
import org.springframework.cloud.openfeign.FeignClient

/**
 * @author Harald Radi (harald.radi@catalysts.cc)
 * @version 1.0
 */
@FeignClient(name = "user", url = "\${ems.url}")
interface UserClient : UserApi
