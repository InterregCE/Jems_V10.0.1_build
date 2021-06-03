package io.cloudflight.jems.server.project.service.workpackage.get_workpackage

import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.project.authorization.CanRetrieveProject
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.toOutputWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.toOutputWorkPackageSimple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetWorkPackage(private val persistence: WorkPackagePersistence) : GetWorkPackageInteractor {

    @CanRetrieveProject
    override fun getWorkPackagesForTimePlanByProjectId(projectId: Long): List<ProjectWorkPackage> =
        persistence.getWorkPackagesWithOutputsAndActivitiesByProjectId(projectId)

    @CanRetrieveProject
    override fun getWorkPackagesByProjectId(projectId: Long, version: String?): List<OutputWorkPackageSimple> =
        persistence.getWorkPackagesByProjectId(projectId, version)

    @CanRetrieveProjectWorkPackage
    override fun getWorkPackageById(workPackageId: Long, version: String?): OutputWorkPackage =
        persistence.getWorkPackageById(workPackageId, version)
}
