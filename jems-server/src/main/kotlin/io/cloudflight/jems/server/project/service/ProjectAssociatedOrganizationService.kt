package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationCreate
import io.cloudflight.jems.api.project.dto.InputProjectAssociatedOrganizationUpdate
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.OutputProjectAssociatedOrganizationDetail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectAssociatedOrganizationService {
    fun getById(projectId: Long, id: Long): OutputProjectAssociatedOrganizationDetail

    fun findAllByProjectId(projectId: Long, page: Pageable): Page<OutputProjectAssociatedOrganization>

    fun create(projectId: Long, associatedOrganization: InputProjectAssociatedOrganizationCreate): OutputProjectAssociatedOrganizationDetail

    fun update(projectId: Long, associatedOrganization: InputProjectAssociatedOrganizationUpdate): OutputProjectAssociatedOrganizationDetail

    fun delete(projectId: Long, associatedOrganizationId: Long)
}
