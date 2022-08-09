package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.fillEndDateWithDuration
import io.cloudflight.jems.server.project.service.contracting.fillFTLumpSumsList
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.projectContractingMonitoringChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateContractingMonitoring(
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val versionPersistence: ProjectVersionPersistence,
    private val projectLumpSumPersistence: ProjectLumpSumPersistence,
    private val validator: ContractingValidator,
    private val auditPublisher: ApplicationEventPublisher
): UpdateContractingMonitoringInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(UpdateContractingMonitoringException::class)
    override fun updateContractingMonitoring(
        projectId: Long,
        contractMonitoring: ProjectContractingMonitoring
    ): ProjectContractingMonitoring {
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            validator.validateProjectStatusForModification(projectSummary)
            validator.validateMonitoringInput(contractMonitoring)

            // load old data for audit once the project is already contracted
            val oldMonitoring = contractingMonitoringPersistence.getContractingMonitoring(projectId)
                .fillFTLumpSumsList ( resolveLumpSums = {
                    versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
                        .let { projectLumpSumPersistence.getLumpSums(projectId = projectId, version = it)
                            .filter { lumpSum -> lumpSum.isFastTrack == true } }
                } )
            val updated = contractingMonitoringPersistence.updateContractingMonitoring(
                contractMonitoring.copy(projectId = projectId)
            ).fillEndDateWithDuration(resolveDuration = {
                versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
                    .let { projectPersistence.getProject(projectId = projectId, version = it).duration }
            })

            projectLumpSumPersistence.updateLumpSums(projectId, contractMonitoring.fastTrackLumpSums!!)

            if (projectSummary.status.isAlreadyContracted()) {
                auditPublisher.publishEvent(
                    projectContractingMonitoringChanged(
                        context = this,
                        project = projectSummary,
                        oldMonitoring = oldMonitoring,
                        newMonitoring = contractMonitoring
                    )
                )
            }
            return updated
        }
    }
}
