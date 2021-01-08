package io.cloudflight.jems.server.project.service.lumpsum.update_project_lump_sums

import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum

interface UpdateProjectLumpSumsInteractor {
    fun updateLumpSums(projectId: Long, lumpSums: List<ProjectLumpSum>): List<ProjectLumpSum>
}
