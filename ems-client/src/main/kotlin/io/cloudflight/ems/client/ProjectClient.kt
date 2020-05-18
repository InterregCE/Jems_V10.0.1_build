package io.cloudflight.ems.client

import io.cloudflight.ems.api.ProjectApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "project", url = "\${ems.url}")
interface ProjectClient : ProjectApi
