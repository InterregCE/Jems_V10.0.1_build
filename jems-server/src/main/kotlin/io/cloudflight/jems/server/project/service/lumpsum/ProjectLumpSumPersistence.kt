package io.cloudflight.jems.server.project.service.lumpsum

import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum

interface ProjectLumpSumPersistence {

    fun getLumpSums(projectId: Long): List<ProjectLumpSum>
    fun updateLumpSums(projectId: Long, lumpSums: List<ProjectLumpSum>): List<ProjectLumpSum>

}
