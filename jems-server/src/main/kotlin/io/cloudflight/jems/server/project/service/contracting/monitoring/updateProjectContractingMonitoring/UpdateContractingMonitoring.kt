package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.entity.toEntity
import io.cloudflight.jems.server.payments.repository.PaymentRepository
import io.cloudflight.jems.server.programme.repository.fund.ProgrammeFundRepository
import io.cloudflight.jems.server.programme.service.fund.ProgrammeFundPersistence
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.fillEndDateWithDuration
import io.cloudflight.jems.server.project.service.contracting.fillFTLumpSumsList
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.projectContractingMonitoringChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class UpdateContractingMonitoring(
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val projectRepository: ProjectRepository,
    private val versionPersistence: ProjectVersionPersistence,
    private val projectLumpSumPersistence: ProjectLumpSumPersistence,
    private val validator: ContractingValidator,
    private val auditPublisher: ApplicationEventPublisher,
    private val paymentPersistence: PaymentPersistence,
    private val paymentRepository: PaymentRepository,
    private val fundsPersistence: ProgrammeFundPersistence,
    private val fundRepository: ProgrammeFundRepository
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
            val lumpSumsOrderNrTobeAdded: MutableSet<Int> = mutableSetOf()
            val lumpSumsOrderNrToBeDeleted: MutableSet<Int> = mutableSetOf()

            updateReadyForPayment(projectId, contractMonitoring.fastTrackLumpSums!!, oldMonitoring.fastTrackLumpSums!!, lumpSumsOrderNrTobeAdded, lumpSumsOrderNrToBeDeleted)
            updateApprovedAmountPerFund(projectId, lumpSumsOrderNrTobeAdded, lumpSumsOrderNrToBeDeleted)
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

    private fun updateReadyForPayment(projectId: Long, lumpSums: List<ProjectLumpSum>, savedFastTrackLumpSums: List<ProjectLumpSum>, orderNrsToBeAdded: MutableSet<Int>, orderNrsToBeDeleted: MutableSet<Int>) {

        lumpSums.forEachIndexed { index, it ->
            val lumpSum = savedFastTrackLumpSums.first { savedLumpSum -> savedLumpSum.programmeLumpSumId == it.programmeLumpSumId }
            it.lastApprovedVersionBeforeReadyForPayment = lumpSum.lastApprovedVersionBeforeReadyForPayment
            it.paymentEnabledDate = lumpSum.paymentEnabledDate
            if ( lumpSum.readyForPayment != it.readyForPayment) {
                if(it.readyForPayment) {
                    orderNrsToBeAdded.add(index + 1)
                    it.lastApprovedVersionBeforeReadyForPayment = this.versionPersistence.getLatestApprovedOrCurrent(projectId)
                    it.paymentEnabledDate = ZonedDateTime.now()
                } else {
                    orderNrsToBeDeleted.add(index + 1)
                    it.lastApprovedVersionBeforeReadyForPayment = null
                    it.paymentEnabledDate = null
                }
            }
        }
    }

    private fun updateApprovedAmountPerFund(
        projectId: Long,
        orderNrsToBeAdded: MutableSet<Int>,
        orderNrsToBeDeleted: MutableSet<Int>
    ) {
        if (orderNrsToBeDeleted.isNotEmpty()) {
            this.paymentPersistence.deleteAllByProjectIdAndOrderNrIn(projectId, orderNrsToBeDeleted)
        }

        if (orderNrsToBeAdded.isNotEmpty()) {
            val projectLumpSums = this.projectLumpSumPersistence.getByProjectId(projectId)
            val projectEntity = projectRepository.getById(projectId)

            val calculatedAmountsToBeAdded =
                this.paymentRepository.getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId, orderNrsToBeAdded)
                    .toEntity(
                        project = projectEntity,
                        projectLumpSums = projectLumpSums,
                        getProgrammeFund = { fundRepository.getById(it) }
                    )
            this.paymentRepository.saveAll(calculatedAmountsToBeAdded)
        }

    }
}
