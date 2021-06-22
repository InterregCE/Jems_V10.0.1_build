package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProjectData
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.OutputProjectSimple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectService {

    fun update(id: Long, projectData: InputProjectData): ProjectDetailDTO

}
