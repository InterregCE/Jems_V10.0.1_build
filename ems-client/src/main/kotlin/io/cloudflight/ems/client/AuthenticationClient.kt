package io.cloudflight.ems.client

import io.cloudflight.ems.api.AuthenticationApi
import org.springframework.cloud.openfeign.FeignClient

/**
 * @author Mihai Rinzis
 */
@FeignClient(name = "auth", url = "\${ems.url}")
interface AuthenticationClient : AuthenticationApi
