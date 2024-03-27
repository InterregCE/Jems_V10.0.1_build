package io.cloudflight.jems.server.project.repository.lumpsum

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.costoption.ProgrammeLumpSumRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectLumpSumPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectRepository: ProjectRepository,
    private val projectLumpSumRepository: ProjectLumpSumRepository,
    private val programmeLumpSumRepository: ProgrammeLumpSumRepository,
    private val projectPartnerRepository: ProjectPartnerRepository,
) : ProjectLumpSumPersistence {

    @Transactional(readOnly = true)
    override fun getLumpSums(projectId: Long, version: String?): List<ProjectLumpSum> =
        projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = { getProjectOrThrow(projectId).lumpSums.toModel() },
            previousVersionFetcher = { timestamp ->
                projectLumpSumRepository.findAllByProjectIdAsOfTimestamp(projectId, timestamp).toProjectLumpSumHistoricalData()
            }
        ) ?: emptyList()

    @Transactional
    override fun updateLumpSums(projectId: Long, lumpSums: List<ProjectLumpSum>): List<ProjectLumpSum> {
        if (lumpSums.isNotEmpty())
            updateLumpSumsOrderNr(lumpSums)

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

    @Transactional
    override fun updateLumpSumsReadyForPayment(projectId: Long, lumpSums: List<ProjectLumpSum>) {
        val lumpSumByOrderNr = lumpSums.associateBy { it.orderNr }

        getProjectOrThrow(projectId).lumpSums.onEach { lumpSum ->
            lumpSumByOrderNr[lumpSum.id.orderNr]?.let { updated ->
                lumpSum.isReadyForPayment = updated.readyForPayment
                lumpSum.paymentEnabledDate = updated.paymentEnabledDate
                lumpSum.lastApprovedVersionBeforeReadyForPayment = updated.lastApprovedVersionBeforeReadyForPayment
            }
        }
    }

    @Transactional(readOnly = true)
    override fun isFastTrackLumpSumReadyForPayment(programmeLumpSumId: Long) =
        projectLumpSumRepository.findAllByProgrammeLumpSumId(programmeLumpSumId).any { it.readyForPayment }

    private fun updateLumpSumsOrderNr(lumpSums: List<ProjectLumpSum>) {
        var lastAvailableOrderNr: Int = lumpSums.maxOf { it.orderNr }

        lumpSums.filter { it.orderNr == 0 }
            .forEach {
                it.orderNr = ++lastAvailableOrderNr
            }
    }

    private fun getProjectOrThrow(projectId: Long) =
        projectRepository.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

    private fun getProgrammeLumpSumOrThrow(programmeLumpSumId: Long) =
        programmeLumpSumRepository.findById(programmeLumpSumId)
            .orElseThrow { ResourceNotFoundException("programmeLumpSum") }

    private fun getProjectPartnerOrThrow(projectId: Long, partnerId: Long) =
        projectPartnerRepository.findFirstByProjectIdAndId(projectId, partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

}
