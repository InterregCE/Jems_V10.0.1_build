package io.cloudflight.jems.client

import io.cloudflight.jems.api.project.ProjectStatusApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "projectstatus", url = "\${ems.url}")
interface ProjectStatusClient : ProjectStatusApi
