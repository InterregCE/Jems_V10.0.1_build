package io.cloudflight.jems.server.payments.service.advance.getContractedProjects

import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.data.domain.Page

interface GetContractedProjectsInteractor {

    fun getContractedProjects(searchId: String): Page<ProjectSummary>
}
