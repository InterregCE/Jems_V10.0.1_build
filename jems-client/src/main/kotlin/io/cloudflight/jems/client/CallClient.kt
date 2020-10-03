package io.cloudflight.jems.client

import io.cloudflight.jems.api.call.CallApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "call", url = "\${ems.url}")
interface CallClient: CallApi
