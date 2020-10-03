package io.cloudflight.jems.client

import io.cloudflight.jems.api.user.UserApi
import org.springframework.cloud.openfeign.FeignClient

/**
 * Auto generate REST access to user entity defined in UserApi.
 */
@FeignClient(name = "user", url = "\${ems.url}")
interface UserClient : UserApi
