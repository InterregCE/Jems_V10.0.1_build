package io.cloudflight.jems.server.project.repository.lumpsum

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectLumpSumPersistenceProvider(
    private val projectRepository: ProjectRepository,
    private val programmeLumpSumRepository: ProgrammeLumpSumRepository,
    private val projectPartnerRepository: ProjectPartnerRepository,
) : ProjectLumpSumPersistence {

    @Transactional(readOnly = true)
    override fun getLumpSums(projectId: Long): List<ProjectLumpSum> =
        getProjectOrThrow(projectId).lumpSums.toModel()

    @Transactional
    override fun updateLumpSums(projectId: Long, lumpSums: List<ProjectLumpSum>): List<ProjectLumpSum> {
        return projectRepository.save(
            getProjectOrThrow(projectId).copy(
                lumpSums = lumpSums.toEntity(
                    projectId = projectId,
                    getProgrammeLumpSum = { getProgrammeLumpSumOrThrow(it) },
                    getProjectPartner = { getProjectPartnerOrThrow(projectId, it) },
                )
            )
        ).lumpSums.toModel()
    }

    private fun getProjectOrThrow(projectId: Long) =
        projectRepository.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

    private fun getProgrammeLumpSumOrThrow(programmeLumpSumId: Long) =
        programmeLumpSumRepository.findById(programmeLumpSumId).orElseThrow { ResourceNotFoundException("programmeLumpSum") }

    private fun getProjectPartnerOrThrow(projectId: Long, partnerId: Long) =
        projectPartnerRepository.findFirstByProjectIdAndId(projectId, partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

}
