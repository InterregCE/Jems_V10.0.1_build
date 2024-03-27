package io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring

import io.cloudflight.jems.server.payments.repository.applicationToEc.linkToPayment.PaymentApplicationToEcLinkPersistenceProvider
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.fillClosureLastPaymentDates
import io.cloudflight.jems.server.project.service.contracting.fillEndDateWithDuration
import io.cloudflight.jems.server.project.service.contracting.fillLumpSumsList
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class GetContractingMonitoringService(
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val versionPersistence: ProjectVersionPersistence,
    private val projectLumpSumPersistence: ProjectLumpSumPersistence,
    private val paymentToEcPersistenceProvider: PaymentApplicationToEcLinkPersistenceProvider,
    private val partnerPersistence: PartnerPersistence,
) {

    @Transactional(readOnly = true)
    fun getContractingMonitoring(projectId: Long): ProjectContractingMonitoring {
        projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            ContractingValidator.validateProjectStatusForModification(projectSummary)
        }

        val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
        val allPartners = partnerPersistence
            .findAllByProjectIdForDropdown(projectId, Sort.by(Sort.Order.asc("sortNumber")), version)

        return contractingMonitoringPersistence.getContractingMonitoring(projectId)
            .fillEndDateWithDuration { projectPersistence.getProject(projectId, version).duration }
            .fillLumpSumsList(projectLumpSumPersistence.getLumpSums(projectId = projectId, version))
            .fillClosureLastPaymentDates(allPartners, contractingMonitoringPersistence.getPartnerPaymentDate(projectId))
            .also {
                val ftlsIdToEcPaymentId =
                    paymentToEcPersistenceProvider.getFtlsIdLinkToEcPaymentIdByProjectId(projectId)
                it.fastTrackLumpSums?.forEach { projectLumpSum ->
                    projectLumpSum.installmentsAlreadyCreated = contractingMonitoringPersistence.existsSavedInstallment(
                        projectId = projectId,
                        lumpSumId = projectLumpSum.programmeLumpSumId,
                        orderNr = projectLumpSum.orderNr
                    )
                    projectLumpSum.linkedToEcPaymentId = ftlsIdToEcPaymentId[projectLumpSum.orderNr]
                }
            }
    }

    @Transactional(readOnly = true)
    fun getContractMonitoringDates(projectId: Long): Pair<LocalDate, LocalDate?>? {
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
        val contractMonitoring = contractingMonitoringPersistence.getContractingMonitoring(projectId)
            .fillEndDateWithDuration { projectPersistence.getProject(projectId, version).duration }
        return contractMonitoring.startDate?.let {
            Pair(it, contractMonitoring.endDate)
        }
    }

    @Transactional(readOnly = true)
    fun getProjectContractingMonitoring(projectId: Long): ProjectContractingMonitoring =
        contractingMonitoringPersistence.getContractingMonitoring(projectId)
            .fillEndDateWithDuration(resolveDuration = {
                versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
                    .let { projectPersistence.getProject(projectId = projectId, version = it).duration }
            })


}
