package io.cloudflight.ems.client

import io.cloudflight.ems.api.call.CallApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "call", url = "\${ems.url}")
interface CallClient: CallApi
