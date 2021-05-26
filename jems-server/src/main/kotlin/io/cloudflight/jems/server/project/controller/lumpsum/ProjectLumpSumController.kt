package io.cloudflight.jems.server.project.controller.lumpsum

import io.cloudflight.jems.api.project.dto.lumpsum.ProjectLumpSumDTO
import io.cloudflight.jems.api.project.lumpsum.ProjectLumpSumApi
import io.cloudflight.jems.server.project.service.lumpsum.get_project_lump_sums.GetProjectLumpSumsInteractor
import io.cloudflight.jems.server.project.service.lumpsum.update_project_lump_sums.UpdateProjectLumpSumsInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectLumpSumController(
    private val getProjectLumpSumsInteractor: GetProjectLumpSumsInteractor,
    private val updateProjectLumpSumsInteractor: UpdateProjectLumpSumsInteractor,
) : ProjectLumpSumApi {

    override fun getProjectLumpSums(projectId: Long, version: String?): List<ProjectLumpSumDTO> =
        getProjectLumpSumsInteractor.getLumpSums(projectId, version).toDto()

    override fun updateProjectLumpSums(projectId: Long, lumpSums: List<ProjectLumpSumDTO>): List<ProjectLumpSumDTO> =
        updateProjectLumpSumsInteractor.updateLumpSums(projectId, lumpSums.toModel()).toDto()

}
