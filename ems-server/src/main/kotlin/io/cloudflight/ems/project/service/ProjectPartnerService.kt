package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.InputProjectPartner
import io.cloudflight.ems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.ems.api.project.dto.OutputProjectPartner
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectPartnerService {

    fun getById(id: Long): OutputProjectPartner

    fun findAllByProjectId(projectId: Long, page: Pageable): Page<OutputProjectPartner>

    fun createProjectPartner(projectId: Long, projectPartner: InputProjectPartner): OutputProjectPartner

    fun update(projectId: Long, projectPartner: InputProjectPartnerUpdate): OutputProjectPartner

}
