package io.cloudflight.jems.client

import io.cloudflight.jems.api.project.ProjectFileApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "projectfile", url = "\${ems.url}")
interface ProjectFileClient : ProjectFileApi
