package io.cloudflight.jems.server.project.service.lumpsum

import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum

interface ProjectLumpSumPersistence {

    fun getLumpSums(projectId: Long, version: String? = null): List<ProjectLumpSum>

    fun updateLumpSums(projectId: Long, lumpSums: List<ProjectLumpSum>): List<ProjectLumpSum>

    fun updateLumpSumsReadyForPayment(projectId: Long, lumpSums: List<ProjectLumpSum>)

    fun isFastTrackLumpSumReadyForPayment(programmeLumpSumId: Long): Boolean
}
