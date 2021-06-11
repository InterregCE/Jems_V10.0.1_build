package io.cloudflight.jems.server.project.service.result

import io.cloudflight.jems.server.project.service.result.model.ProjectResult

interface ProjectResultPersistence {

    fun getResultsForProject(projectId: Long, version: String?): List<ProjectResult>
    fun updateResultsForProject(projectId: Long, projectResults: List<ProjectResult>): List<ProjectResult>
    fun getAvailablePeriodNumbers(projectId: Long): Set<Int>

}
