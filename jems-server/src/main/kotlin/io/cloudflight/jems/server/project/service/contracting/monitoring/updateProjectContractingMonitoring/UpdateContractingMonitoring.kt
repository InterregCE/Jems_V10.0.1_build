package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.service.model.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.service.model.PaymentToCreate
import io.cloudflight.jems.server.payments.service.monitoringFtlsReadyForPayment
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.fillEndDateWithDuration
import io.cloudflight.jems.server.project.service.contracting.fillLumpSumsList
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.projectContractingMonitoringChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
class UpdateContractingMonitoring(
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val versionPersistence: ProjectVersionPersistence,
    private val projectLumpSumPersistence: ProjectLumpSumPersistence,
    private val validator: ContractingValidator,
    private val auditPublisher: ApplicationEventPublisher,
    private val paymentPersistence: PaymentPersistence,
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
                .fillLumpSumsList ( resolveLumpSums = {
                    versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
                        .let { projectLumpSumPersistence.getLumpSums(projectId = projectId, version = it) }
                } )
            val updated = contractingMonitoringPersistence.updateContractingMonitoring(
                contractMonitoring.copy(projectId = projectId)
            ).fillEndDateWithDuration(resolveDuration = {
                versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
                    .let { projectPersistence.getProject(projectId = projectId, version = it).duration }
            }).apply { fastTrackLumpSums = contractMonitoring.fastTrackLumpSums }

            val lumpSumsOrderNrTobeAdded: MutableSet<Int> = mutableSetOf()
            val lumpSumsOrderNrToBeDeleted: MutableSet<Int> = mutableSetOf()

            updateReadyForPayment(
                projectId = projectId,
                lumpSums = contractMonitoring.fastTrackLumpSums!!,
                savedFastTrackLumpSums = oldMonitoring.fastTrackLumpSums!!,
                orderNrsToBeAdded = lumpSumsOrderNrTobeAdded,
                orderNrsToBeDeleted = lumpSumsOrderNrToBeDeleted
            )
            projectLumpSumPersistence.updateLumpSums(projectId, contractMonitoring.fastTrackLumpSums!!)
            updateApprovedAmountPerPartner(projectSummary, lumpSumsOrderNrTobeAdded, lumpSumsOrderNrToBeDeleted)

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

    private fun updateReadyForPayment(
        projectId: Long,
        lumpSums: List<ProjectLumpSum>,
        savedFastTrackLumpSums: List<ProjectLumpSum>,
        orderNrsToBeAdded: MutableSet<Int>,
        orderNrsToBeDeleted: MutableSet<Int>
    ) {
        lumpSums.forEachIndexed { _, it ->
            val lumpSum = savedFastTrackLumpSums.first { o -> o.orderNr == it.orderNr }
            it.lastApprovedVersionBeforeReadyForPayment = lumpSum.lastApprovedVersionBeforeReadyForPayment
            it.paymentEnabledDate = lumpSum.paymentEnabledDate
            if (lumpSum.readyForPayment != it.readyForPayment) {
                if (contractingMonitoringPersistence
                        .existsSavedInstallment(projectId, lumpSum.programmeLumpSumId, lumpSum.orderNr)) {
                    throw UpdateContractingMonitoringFTLSException()
                }
                if (it.readyForPayment) {
                    orderNrsToBeAdded.add(it.orderNr)
                    it.lastApprovedVersionBeforeReadyForPayment = this.versionPersistence.getLatestApprovedOrCurrent(projectId)
                    it.paymentEnabledDate = ZonedDateTime.now()
                } else {
                    orderNrsToBeDeleted.add(it.orderNr)
                    it.lastApprovedVersionBeforeReadyForPayment = null
                    it.paymentEnabledDate = null
                }
            }
        }
    }

    private fun updateApprovedAmountPerPartner(
        project: ProjectSummary,
        orderNrsToBeAdded: MutableSet<Int>,
        orderNrsToBeDeleted: MutableSet<Int>
    ) {
        val projectId = project.id
        if (orderNrsToBeDeleted.isNotEmpty()) {
            this.paymentPersistence.deleteAllByProjectIdAndOrderNrIn(projectId, orderNrsToBeDeleted)
            orderNrsToBeDeleted.forEach { orderNr ->
                auditPublisher.publishEvent(monitoringFtlsReadyForPayment(this, project, orderNr, false))
            }
        }

        if (orderNrsToBeAdded.isNotEmpty()) {
            val calculatedAmountsToBeAdded = this.paymentPersistence.getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId, orderNrsToBeAdded)

            val paymentsToUpdate = calculatedAmountsToBeAdded.groupBy { PaymentGroupingId(it.orderNr, it.programmeFundId) }
                .mapValues { (_, partnerPayments) ->
                    PaymentToCreate(
                        partnerPayments.first().programmeLumpSumId,
                        partnerPayments.map { o ->
                            PaymentPartnerToCreate(
                                o.partnerId,
                                o.amountApprovedPerPartner
                            )
                        },
                        partnerPayments.sumOf { it.amountApprovedPerPartner }
                    )
                }

            this.paymentPersistence.savePaymentToProjects(projectId, paymentsToUpdate)
            orderNrsToBeAdded.forEach { orderNr ->
                auditPublisher.publishEvent(monitoringFtlsReadyForPayment(this, project, orderNr, true))
            }
        }
    }
}
