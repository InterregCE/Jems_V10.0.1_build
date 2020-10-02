package io.cloudflight.ems.client

import io.cloudflight.ems.api.user.UserApi
import org.springframework.cloud.openfeign.FeignClient

/**
 * Auto generate REST access to user entity defined in UserApi.
 */
@FeignClient(name = "user", url = "\${ems.url}")
interface UserClient : UserApi
