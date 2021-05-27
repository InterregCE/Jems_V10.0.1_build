package io.cloudflight.jems.server.project.service.workpackage.get_workpackage

import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface GetWorkPackageInteractor {
    fun getWorkPackagesForTimePlanByProjectId(projectId: Long, pageable: Pageable): Page<ProjectWorkPackage>
}
