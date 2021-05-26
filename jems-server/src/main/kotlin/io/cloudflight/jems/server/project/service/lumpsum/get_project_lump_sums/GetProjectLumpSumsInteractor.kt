package io.cloudflight.jems.server.project.service.lumpsum.get_project_lump_sums

import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum

interface GetProjectLumpSumsInteractor {
    fun getLumpSums(projectId: Long, version: String? = null): List<ProjectLumpSum>
}
