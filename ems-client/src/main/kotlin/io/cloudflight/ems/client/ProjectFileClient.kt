package io.cloudflight.ems.client

import io.cloudflight.ems.api.ProjectFileApi
import org.springframework.cloud.openfeign.FeignClient

@FeignClient(name = "projectfile", url = "\${ems.url}")
interface ProjectFileClient : ProjectFileApi
