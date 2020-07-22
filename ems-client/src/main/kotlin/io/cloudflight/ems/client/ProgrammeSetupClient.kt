package io.cloudflight.ems.client

import io.cloudflight.ems.api.ProgrammeSetupApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "setup", url = "\${ems.url}")
interface ProgrammeSetupClient : ProgrammeSetupApi
