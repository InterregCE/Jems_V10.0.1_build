package io.cloudflight.jems.server.project.repository.contracting.monitoring

import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingPartnerPaymentDateEntity
import io.cloudflight.jems.server.project.repository.contracting.partner.lastPayment.ContractingPartnerPaymentDateRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringOption
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringPartnerLastPayment
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Repository
class ContractingMonitoringPersistenceProvider(
    private val projectContractingMonitoringRepository: ProjectContractingMonitoringRepository,
    private val contractingPartnerPaymentDateRepository: ContractingPartnerPaymentDateRepository,
    private val partnerRepository: ProjectPartnerRepository,
): ContractingMonitoringPersistence {

    @Transactional(readOnly = true)
    override fun getContractingMonitoring(projectId: Long): ProjectContractingMonitoring {
        return projectContractingMonitoringRepository
            .findByProjectId(projectId)
            .let {
                when {
                    it.isPresent -> it.get().toModel()
                    else -> ProjectContractingMonitoring(
                        projectId = projectId,
                        addDates = emptyList(),
                        dimensionCodes = emptyList(),
                        typologyPartnership = ContractingMonitoringOption.No,
                        typologyProv94 = ContractingMonitoringExtendedOption.No,
                        typologyProv95 = ContractingMonitoringExtendedOption.No,
                        typologyStrategic = ContractingMonitoringOption.No,
                        lastPaymentDates = emptyList(),
                    )
                }
            }
    }

    @Transactional(readOnly = true)
    override fun getPartnerPaymentDate(projectId: Long): Map<Long, LocalDate> =
        contractingPartnerPaymentDateRepository.findAllByPartnerProjectId(projectId)
            .associate { it.partnerId to it.lastPaymentDate }

    @Transactional
    override fun updateContractingMonitoring(contractMonitoring: ProjectContractingMonitoring): ProjectContractingMonitoring {
        return projectContractingMonitoringRepository.save(contractMonitoring.toEntity()).toModel()
    }

    @Transactional
    override fun updateClosureDate(projectId: Long, closureDate: LocalDate?): LocalDate? {
        val contracting = projectContractingMonitoringRepository.findByProjectId(projectId).orElse(null) ?: return null

        contracting.closureDate = closureDate
        return contracting.closureDate
    }

    @Transactional
    override fun updatePartnerPaymentDate(projectId: Long, datePerPartner: Map<Long, LocalDate?>): Map<Long, LocalDate> {
        val existingByPartner = contractingPartnerPaymentDateRepository.findAllByPartnerProjectId(projectId)
            .associateBy { it.partnerId }
        val toNotDelete = datePerPartner.filterValues { it != null }.mapValues { it.value!! }

        contractingPartnerPaymentDateRepository.deleteAll(
            existingByPartner.filter { it.key !in toNotDelete.keys }.values
        )

        return toNotDelete.map { (partnerId, date) ->
            val existingEntity = existingByPartner[partnerId]
            if (existingEntity != null) {
                existingEntity.lastPaymentDate = date
                return@map existingEntity
            } else {
                return@map contractingPartnerPaymentDateRepository.save(
                    ProjectContractingPartnerPaymentDateEntity(partnerId, partnerRepository.getReferenceById(partnerId), date)
                )
            }
        }.associate { it.partnerId to it.lastPaymentDate }
    }

    @Transactional(readOnly = true)
    override fun existsSavedInstallment(projectId: Long, lumpSumId: Long, orderNr: Int): Boolean {
        return projectContractingMonitoringRepository.existsSavedInstallment(projectId, lumpSumId, orderNr)
    }
}
