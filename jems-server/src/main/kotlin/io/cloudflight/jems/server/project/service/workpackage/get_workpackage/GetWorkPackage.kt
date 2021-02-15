package io.cloudflight.jems.server.project.service.workpackage.get_workpackage

import io.cloudflight.jems.server.project.authorization.CanReadProject
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class GetWorkPackage(private val persistence: WorkPackagePersistence) : GetWorkPackageInteractor {

    @CanReadProject
    override fun getRichWorkPackagesByProjectId(projectId: Long, pageable: Pageable): Page<ProjectWorkPackage> =
        persistence.getRichWorkPackagesByProjectId(projectId, pageable)

}
