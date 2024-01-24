package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingPartnerPaymentDate

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosure
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureLastPaymentDate
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureUpdate
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateContractingPartnerPaymentDate(
    private val partnerPersistence: PartnerPersistence,
    private val contractingMonitoringPersistence: ContractingMonitoringPersistence,
    private val versionPersistence: ProjectVersionPersistence,
) : UpdateContractingPartnerPaymentDateInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(UpdateContractingPartnerPaymentDateException::class)
    override fun updatePartnerPaymentDate(projectId: Long, closure: ContractingClosureUpdate): ContractingClosure {
        val datesPerPartner = closure.lastPaymentDates.associateBy({ it.partnerId }, { it.lastPaymentDate })

        val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
        val allPartners = partnerPersistence.findAllByProjectIdForDropdown(projectId, Sort.by(Sort.Order.asc("sortNumber")), version)
        val allPartnerIds = allPartners.mapTo(HashSet()) { it.id!! }

        val missingPartnerIds = allPartnerIds.minus(datesPerPartner.keys)
        val toUpdate = datesPerPartner.filter { it.key in allPartnerIds }
            .plus(missingPartnerIds.associateWith { null })

        val closureDate = contractingMonitoringPersistence.updateClosureDate(projectId, closure.closureDate)
        val datePerPartnerUpdated = contractingMonitoringPersistence.updatePartnerPaymentDate(projectId, toUpdate)

        return ContractingClosure(
            closureDate = closureDate,
            lastPaymentDates = allPartners.map {
                ContractingClosureLastPaymentDate(
                    partnerId = it.id!!,
                    partnerNumber = it.sortNumber!!,
                    partnerAbbreviation = it.abbreviation,
                    partnerRole = it.role,
                    partnerDisabled = !it.active,
                    lastPaymentDate = datePerPartnerUpdated[it.id],
                )
            },
        )
    }

}
