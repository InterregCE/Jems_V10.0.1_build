package io.cloudflight.ems.client

import io.cloudflight.ems.api.project.ProjectStatusApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "projectstatus", url = "\${ems.url}")
interface ProjectStatusClient : ProjectStatusApi
