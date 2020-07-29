package io.cloudflight.ems.client

import io.cloudflight.ems.api.programme.ProgrammeDataApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "programmedata", url = "\${ems.url}")
interface ProgrammeDataClient : ProgrammeDataApi
