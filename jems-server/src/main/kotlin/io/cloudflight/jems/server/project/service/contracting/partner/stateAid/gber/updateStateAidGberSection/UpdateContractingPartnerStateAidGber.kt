package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.updateStateAidGberSection

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanUpdateProjectContractingPartnerStateAid
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGber
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGberSection
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.ContractingPartnerStateAidGberPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.GberHelper
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class UpdateContractingPartnerStateAidGber(
    private val contractingPartnerStateAidGberPersistence: ContractingPartnerStateAidGberPersistence,
    private val getContractingMonitoringService: GetContractingMonitoringService,
    private val partnerBudgetPerFundService: GetPartnerBudgetPerFundService,
    private val partnerPersistence: PartnerPersistence,
    private val versionPersistence: ProjectVersionPersistence,
    private val gberHelper: GberHelper,
    private val validator: ContractingValidator
    ): UpdateContractingPartnerStateAidGberInteractor {

    @CanUpdateProjectContractingPartnerStateAid
    @Transactional
    @ExceptionWrapper(UpdateContractingPartnerStateAidGberException::class)
    override fun updateGberSection(
        partnerId: Long,
        gberData: ContractingPartnerStateAidGber
    ): ContractingPartnerStateAidGberSection {
        validator.validatePartnerLock(partnerId)

        val updatedGberEntity = this.contractingPartnerStateAidGberPersistence.saveGber(partnerId, gberData)
        val projectId = this.partnerPersistence.getProjectIdForPartnerId(partnerId)
        val lastApprovedVersion = this.versionPersistence.getLatestApprovedOrCurrent(projectId)
        val partnerData = this.partnerPersistence.getById(partnerId, lastApprovedVersion)
        val projectContractingMonitoring = getContractingMonitoringService.getProjectContractingMonitoring(projectId)
        val partnerBudgetPerFund = this.partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(projectId, lastApprovedVersion)
            .filter { it.partner?.id == partnerId }.firstOrNull()
        val fundsSelectedByPartner = gberHelper.getPartnerFunds(partnerId, partnerBudgetPerFund?.budgetPerFund ?: emptySet(), lastApprovedVersion)

        return ContractingPartnerStateAidGberSection(
            partnerId = updatedGberEntity.partnerId,
            dateOfGrantingAid = projectContractingMonitoring.addDates.maxByOrNull { addDate -> addDate.number }?.entryIntoForceDate,
            partnerFunds = fundsSelectedByPartner,
            totalEligibleBudget = partnerBudgetPerFund?.totalEligibleBudget ?: BigDecimal.ZERO,
            naceGroupLevel = partnerData.nace,

            aidIntensity = updatedGberEntity.aidIntensity,
            locationInAssistedArea = updatedGberEntity.locationInAssistedArea,
            comment = updatedGberEntity.comment
        )
    }
}
