package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAidGberSection

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.api.project.contracting.partner.ContractingPartnerStateAidDeMinimisMeasure
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGberSection
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.ContractingPartnerStateAidGberPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.GberHelper
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.stateAidSectionShouldBeDisplayed
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetContractingPartnerStateAidGberService(
    private val contractingPartnerStateAidGberPersistence: ContractingPartnerStateAidGberPersistence,
    private val getContractingMonitoringService: GetContractingMonitoringService,
    private val partnerPersistence: PartnerPersistence,
    private val partnerBudgetPerFundService: GetPartnerBudgetPerFundService,
    private val gberHelper: GberHelper,
    private val versionPersistence: ProjectVersionPersistence
) {

    @Transactional(readOnly = true)
    fun getGberSection(partnerId: Long): ContractingPartnerStateAidGberSection? {
        val gberData = this.contractingPartnerStateAidGberPersistence.findById(partnerId)
        val projectId = this.partnerPersistence.getProjectIdForPartnerId(partnerId)
        val lastApprovedVersion = this.versionPersistence.getLatestApprovedOrCurrent(projectId)
        val partnerData = this.partnerPersistence.getById(partnerId, lastApprovedVersion)
        val projectContractingMonitoring = getContractingMonitoringService.getProjectContractingMonitoring(projectId)
        val partnerStateAid = this.partnerPersistence.getPartnerStateAid(partnerId, lastApprovedVersion)
        val partnerBudgetPerFund = this.partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(projectId, lastApprovedVersion)
            .filter { it.partner?.id == partnerId }.firstOrNull()
        val fundsSelectedByPartner = gberHelper.getPartnerFunds(partnerId, partnerBudgetPerFund?.budgetPerFund ?: emptySet(), lastApprovedVersion)

        return if(stateAidSectionShouldBeDisplayed(partnerStateAid) && !hasPartnerStateAidMinimisSelected(partnerStateAid.stateAidScheme?.measure)) {
            ContractingPartnerStateAidGberSection(
                partnerId = partnerData.id,
                dateOfGrantingAid =  projectContractingMonitoring.addDates.maxByOrNull { addDate -> addDate.number }?.entryIntoForceDate,
                partnerFunds =  fundsSelectedByPartner,
                totalEligibleBudget = partnerBudgetPerFund?.totalEligibleBudget ?: BigDecimal.ZERO,
                naceGroupLevel = partnerData.nace,
                aidIntensity = gberData?.aidIntensity ?: BigDecimal.ZERO,
                locationInAssistedArea = gberData?.locationInAssistedArea,
                comment = gberData?.comment,
            )
        } else null
    }

    private fun hasPartnerStateAidMinimisSelected(stateAidMeasure: ProgrammeStateAidMeasure?): Boolean =
        ContractingPartnerStateAidDeMinimisMeasure.values().any { it.name == stateAidMeasure?.name }

}
