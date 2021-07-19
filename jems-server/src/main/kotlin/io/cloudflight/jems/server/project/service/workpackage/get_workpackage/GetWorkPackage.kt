package io.cloudflight.jems.server.project.service.workpackage.get_workpackage

import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackage
import io.cloudflight.jems.api.project.dto.workpackage.OutputWorkPackageSimple
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectWorkPackage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.ProjectWorkPackage
import org.springframework.stereotype.Service

@Service
class GetWorkPackage(private val persistence: WorkPackagePersistence) : GetWorkPackageInteractor {

    @CanRetrieveProjectForm
    override fun getWorkPackagesForTimePlanByProjectId(projectId: Long, version: String?): List<ProjectWorkPackage> =
        persistence.getWorkPackagesWithOutputsAndActivitiesByProjectId(projectId, version)

    @CanRetrieveProjectForm
    override fun getWorkPackagesByProjectId(projectId: Long, version: String?): List<OutputWorkPackageSimple> =
        persistence.getWorkPackagesByProjectId(projectId, version)

    @CanRetrieveProjectWorkPackage
    override fun getWorkPackageById(projectId: Long, workPackageId: Long, version: String?): OutputWorkPackage =
        persistence.getWorkPackageById(workPackageId, projectId, version)
}
