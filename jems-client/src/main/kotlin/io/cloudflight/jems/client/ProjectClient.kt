package io.cloudflight.jems.client

import io.cloudflight.jems.api.project.ProjectApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "project", url = "\${ems.url}")
interface ProjectClient : ProjectApi
