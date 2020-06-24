package io.cloudflight.ems.client

import io.cloudflight.ems.api.AccountApi
import org.springframework.cloud.openfeign.FeignClient

/**
 * @version 1.0
 */
@FeignClient(name = "account", url = "\${ems.url}")
interface AccountClient : AccountApi
