package io.cloudflight.jems.client

import io.cloudflight.jems.api.authentication.AuthenticationApi
import org.springframework.cloud.openfeign.FeignClient

/**
 * @author Mihai Rinzis
 */
@FeignClient(name = "auth", url = "\${ems.url}")
interface AuthenticationClient : AuthenticationApi
