package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.api.project.contracting.partner.ContractingPartnerStateAidDeMinimisMeasure
import io.cloudflight.jems.server.programme.service.ProgrammeDataServiceImpl
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.budget.get_partner_budget_per_funds.GetPartnerBudgetPerFundService
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimis
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimisSection
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.MemberStateForGranting
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.ContractingPartnerStateAidDeMinimisPersistence
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.stateAidSectionShouldBeDisplayed
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetContractingPartnerStateAidDeMinimisService(
    private val contractingPartnerStateAidDeMinimisPersistence: ContractingPartnerStateAidDeMinimisPersistence,
    private val getContractingMonitoringService: GetContractingMonitoringService,
    private val partnerPersistence: PartnerPersistence,
    private val partnerBudgetPerFundService: GetPartnerBudgetPerFundService,
    private val programmeDataService: ProgrammeDataServiceImpl,
    private val versionPersistence: ProjectVersionPersistence
) {

    @Transactional(readOnly = true)
    fun getDeMinimisSection(partnerId: Long): ContractingPartnerStateAidDeMinimisSection? {
        val deMinimisData = this.contractingPartnerStateAidDeMinimisPersistence.findById(partnerId)
        val projectId = this.partnerPersistence.getProjectIdForPartnerId(partnerId)
        val lastApprovedVersion = this.versionPersistence.getLatestApprovedOrCurrent(projectId)
        val projectContractingMonitoring = getContractingMonitoringService.getProjectContractingMonitoring(projectId)
        val partnerStateAid = this.partnerPersistence.getPartnerStateAid(partnerId, lastApprovedVersion)
        val partnerBudgetPerFund = this.partnerBudgetPerFundService.getProjectPartnerBudgetPerFund(projectId, lastApprovedVersion)
            .filter { it.partner?.id == partnerId }.firstOrNull()

        return if(stateAidSectionShouldBeDisplayed(partnerStateAid) && hasPartnerStateAidMinimisSelected(partnerStateAid.stateAidScheme?.measure) ) {
            ContractingPartnerStateAidDeMinimisSection(
                partnerId = partnerId,
                dateOfGrantingAid = projectContractingMonitoring.addDates.maxByOrNull { addDate -> addDate.number }?.entryIntoForceDate,
                amountGrantingAid = deMinimisData?.amountGrantingAid ?: (partnerBudgetPerFund?.totalEligibleBudget ?: BigDecimal.ZERO),
                selfDeclarationSubmissionDate = deMinimisData?.selfDeclarationSubmissionDate,
                baseForGranting = deMinimisData?.baseForGranting,
                aidGrantedByCountry = deMinimisData?.aidGrantedByCountry,
                memberStatesGranting = getStatesGrantingData(partnerId, deMinimisData),
                comment = deMinimisData?.comment,
            )
        } else null
    }

    private fun hasPartnerStateAidMinimisSelected(stateAidMeasure: ProgrammeStateAidMeasure?): Boolean =
        ContractingPartnerStateAidDeMinimisMeasure.values().any { it.name == stateAidMeasure?.name }


    private fun getStatesGrantingData(
        partnerId: Long,
        savedDeMinimisData: ContractingPartnerStateAidDeMinimis?
    ): Set<MemberStateForGranting> {
        val selectedProgrammeNuts =
            programmeDataService.getProgrammeDataOrThrow().programmeNuts.map { it.region2.region1.country }.toSet()
        val membersStates = mutableSetOf<MemberStateForGranting>()

        selectedProgrammeNuts.forEach { country ->
            membersStates.add(
                MemberStateForGranting(
                    partnerId = partnerId,
                    countryCode = country.id,
                    country = country.title,
                    selected = savedDeMinimisData?.memberStatesGranting?.firstOrNull { it.countryCode == country.id }?.selected ?: false,
                    amountInEur = savedDeMinimisData?.memberStatesGranting?.firstOrNull { it.countryCode == country.id }?.amountInEur ?: BigDecimal.ZERO
                )
            )
        }

        return membersStates
    }
}
