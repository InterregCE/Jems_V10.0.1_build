package io.cloudflight.jems.server.plugin.services

import io.cloudflight.jems.plugin.contract.models.project.contracting.contracts.ProjectContractInfoData
import io.cloudflight.jems.plugin.contract.models.project.contracting.management.ProjectContractingManagementData
import io.cloudflight.jems.plugin.contract.models.project.contracting.monitoring.ProjectContractingMonitoringData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.ProjectContractingPartnersSummaryData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.bankingDetails.ProjectContractingPartnerBankingDetailsData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.beneficialOwner.ProjectContractingPartnerBeneficialOwnerData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.deMinimis.ProjectContractingPartnerStateAidDeMinimisData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.documentsLocation.ProjectContractingPartnerDocumentsLocationData
import io.cloudflight.jems.plugin.contract.models.project.contracting.partner.gber.ProjectContractingPartnerStateAidGberData
import io.cloudflight.jems.plugin.contract.models.project.contracting.reporting.ProjectContractingReportingScheduleData
import io.cloudflight.jems.plugin.contract.services.ProjectContractingDataProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo.GetContractInfoService
import io.cloudflight.jems.server.project.service.contracting.management.getProjectContractingManagement.GetContractingManagementService
import io.cloudflight.jems.server.project.service.contracting.monitoring.getProjectContractingMonitoring.GetContractingMonitoringService
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails.GetContractingPartnerBankingDetailsService
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.getBeneficialOwners.GetContractingPartnerBeneficialOwnersService
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.getDocumentsLocation.GetContractingPartnerDocumentsLocationService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection.GetContractingPartnerStateAidDeMinimisService
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAidGberSection.GetContractingPartnerStateAidGberService
import io.cloudflight.jems.server.project.service.contracting.reporting.getContractingReporting.GetContractingReportingService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ProjectContractingDataProviderImpl(
    private val getContractingMonitoringService: GetContractingMonitoringService,
    private val versionPersistence: ProjectVersionPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val getContractInfoService: GetContractInfoService,
    private val getContractingManagementService: GetContractingManagementService,
    private val getContractingPartnerBankingDetailsService: GetContractingPartnerBankingDetailsService,
    private val getContractingPartnerBeneficialOwnersService: GetContractingPartnerBeneficialOwnersService,
    private val getContractingPartnerDocumentsLocationService: GetContractingPartnerDocumentsLocationService,
    private val getContractingReportingService: GetContractingReportingService,
    private val getContractingPartnerStateAidDeMinimisService: GetContractingPartnerStateAidDeMinimisService,
    private val getContractingPartnerStateAidGberService: GetContractingPartnerStateAidGberService
): ProjectContractingDataProvider {

    override fun getContractInfo(projectId: Long): ProjectContractInfoData =
        getContractInfoService.getContractInfo(projectId).toDataModel()

    override fun getContractingManagementInfo(projectId: Long): List<ProjectContractingManagementData> =
        getContractingManagementService.getContractingManagement(projectId).toDataModel()

    override fun getContractingMonitoring(projectId: Long): ProjectContractingMonitoringData =
        getContractingMonitoringService.getContractingMonitoring(projectId).toDataModel()

    override fun getContractingPartnerBankingDetails(
        partnerId: Long
    ): ProjectContractingPartnerBankingDetailsData? =
        getContractingPartnerBankingDetailsService.getBankingDetails(partnerId).toDataModel()

    override fun getContractingPartnerBeneficialOwners(
        partnerId: Long
    ): List<ProjectContractingPartnerBeneficialOwnerData> =
        getContractingPartnerBeneficialOwnersService.getBeneficialOwners(partnerId).toBeneficialOwnersDataModel()

    override fun getContractingPartnerDocumentsLocation(
        partnerId: Long
    ): ProjectContractingPartnerDocumentsLocationData =
        getContractingPartnerDocumentsLocationService.getDocumentsLocation(partnerId).toDataModel()

    override fun getContractingPartnerStateAidDeMinimis(partnerId: Long): ProjectContractingPartnerStateAidDeMinimisData? =
        getContractingPartnerStateAidDeMinimisService.getDeMinimisSection(partnerId).toDataModel()

    override fun getContractingPartnerStateAidGber(partnerId: Long): ProjectContractingPartnerStateAidGberData? =
        getContractingPartnerStateAidGberService.getGberSection(partnerId).toDataModel()

    override fun getContractingPartners(projectId: Long): List<ProjectContractingPartnersSummaryData> {
        val version = versionPersistence.getLatestApprovedOrCurrent(projectId = projectId)
        val partners = partnerPersistence.findAllByProjectIdForDropdown(projectId, Sort.by(Sort.Order.asc("sortNumber")), version)
        return partners.toPartnersDataModel()
    }

    override fun getContractingReporting(projectId: Long): List<ProjectContractingReportingScheduleData> =
        getContractingReportingService.getReportingSchedule(projectId).toReportingSchedulesDataModel()

}
