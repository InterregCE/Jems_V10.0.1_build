package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.updateStateAidDeMinimisSection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingPartnerStateAid
import io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.toModel
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimis
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimisSection
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.ContractingPartnerStateAidDeMinimisPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateContractingPartnerStateAidDeMinimis(
    private val contractingPartnerStateAidDeMinimisPersistence: ContractingPartnerStateAidDeMinimisPersistence,
    private val getContractingMonitoringService: GetContractingMonitoringService,
    private val partnerBudgetPerFundService: GetPartnerBudgetPerFundService,
    private val partnerPersistence: PartnerPersistence,
    private val validator: ContractingValidator,
): UpdateContractingPartnerStateAidDeMinimisInteractor {
    @CanUpdateProjectContractingPartnerStateAid
    @Transactional
    @ExceptionWrapper(UpdateContractingPartnerStateAidDeMinimisException::class)
    override fun updateDeMinimisSection(
        partnerId: Long,
        deMinimisData: ContractingPartnerStateAidDeMinimis
    ): ContractingPartnerStateAidDeMinimisSection {
        validator.validatePartnerLock(partnerId)

        val updatedDeMinimis = this.contractingPartnerStateAidDeMinimisPersistence.saveDeMinimis(partnerId, deMinimisData)
        val partnerData = this.partnerPersistence.getById(partnerId)
        val projectContractingMonitoring = getContractingMonitoringService.getProjectContractingMonitoring(partnerData.projectId)
        val partnerBudgetPerFund = this.partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(partnerData.projectId, null)
            .filter { it.partner?.id == partnerId }.firstOrNull()

        return ContractingPartnerStateAidDeMinimisSection(
            partnerId = partnerId,
            dateOfGrantingAid = projectContractingMonitoring.addDates.maxByOrNull { addDate -> addDate.number }?.entryIntoForceDate,
            totalEligibleBudget = partnerBudgetPerFund?.totalEligibleBudget ?: BigDecimal.ZERO,
            selfDeclarationSubmissionDate = updatedDeMinimis.selfDeclarationSubmissionDate,
            baseForGranting = updatedDeMinimis.baseForGranting,
            aidGrantedByCountry = updatedDeMinimis.aidGrantedByCountry,
            aidGrantedByCountryCode = updatedDeMinimis.aidGrantedByCountryCode,
            memberStatesGranting = updatedDeMinimis.memberStatesGranting.toModel(),
            comment = updatedDeMinimis.comment
        )
    }
}
