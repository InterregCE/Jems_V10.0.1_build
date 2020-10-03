package io.cloudflight.jems.client

import io.cloudflight.jems.api.programme.ProgrammeDataApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "programmedata", url = "\${ems.url}")
interface ProgrammeDataClient : ProgrammeDataApi
