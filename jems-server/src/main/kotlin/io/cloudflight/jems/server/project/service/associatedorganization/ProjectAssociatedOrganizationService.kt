package io.cloudflight.jems.server.project.service.associatedorganization

import io.cloudflight.jems.api.project.dto.associatedorganization.InputProjectAssociatedOrganization
import io.cloudflight.jems.api.project.dto.associatedorganization.OutputProjectAssociatedOrganizationDetail

interface ProjectAssociatedOrganizationService {

    fun create(projectId: Long, associatedOrganization: InputProjectAssociatedOrganization): OutputProjectAssociatedOrganizationDetail

    fun update(projectId: Long, associatedOrganization: InputProjectAssociatedOrganization): OutputProjectAssociatedOrganizationDetail

    fun delete(projectId: Long, associatedOrganizationId: Long)

    fun refreshSortNumbers(projectId: Long)

    fun findAllByProjectId(projectId: Long): Iterable<OutputProjectAssociatedOrganizationDetail>

}
