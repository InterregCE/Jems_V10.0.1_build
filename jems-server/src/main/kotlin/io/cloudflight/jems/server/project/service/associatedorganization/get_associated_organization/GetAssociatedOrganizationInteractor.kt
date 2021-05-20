package io.cloudflight.jems.server.project.service.associatedorganization.get_associated_organization

import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetAssociatedOrganizationInteractor {
    fun getById(projectId: Long, id: Long, version: String? = null): OutputProjectAssociatedOrganizationDetail

    fun findAllByProjectId(projectId: Long, page: Pageable, version: String? = null): Page<OutputProjectAssociatedOrganization>

}