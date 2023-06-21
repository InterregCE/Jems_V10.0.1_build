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
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingManagement
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidDeMinimisSection
import io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid.ContractingPartnerStateAidGberSection
import io.cloudflight.jems.server.project.service.contracting.model.reporting.ProjectContractingReportingSchedule
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectContractingDataProviderMapper::class.java)

fun ProjectContractInfo.toDataModel() = mapper.map(this)

fun List<ProjectContractingManagement>.toDataModel() = map { mapper.map(it) }

fun ProjectContractingMonitoring.toDataModel() = mapper.map(this)

fun ContractingPartnerBankingDetails?.toDataModel() = mapper.map(this)

fun List<ContractingPartnerBeneficialOwner>.toBeneficialOwnersDataModel() = map { mapper.map(it) }

fun ContractingPartnerDocumentsLocation.toDataModel() = mapper.map(this)

fun List<ProjectPartnerSummary>.toPartnersDataModel() = map { mapper.map(it) }

fun List<ProjectContractingReportingSchedule>.toReportingSchedulesDataModel() = map { mapper.map(it) }

fun ContractingPartnerStateAidGberSection?.toDataModel() = mapper.map(this)

fun ContractingPartnerStateAidDeMinimisSection?.toDataModel() = mapper.map(this)

@Mapper
interface ProjectContractingDataProviderMapper {

    fun map(model: ProjectContractInfo): ProjectContractInfoData

    fun map(model: ProjectContractingManagement): ProjectContractingManagementData

    fun map(model: ProjectContractingMonitoring): ProjectContractingMonitoringData

    fun map(model: ContractingPartnerBankingDetails?): ProjectContractingPartnerBankingDetailsData?

    fun map(model: ContractingPartnerBeneficialOwner): ProjectContractingPartnerBeneficialOwnerData

    fun map(model: ContractingPartnerDocumentsLocation): ProjectContractingPartnerDocumentsLocationData

    fun map(model: ProjectPartnerSummary): ProjectContractingPartnersSummaryData

    fun map(model: ProjectContractingReportingSchedule): ProjectContractingReportingScheduleData

    fun map(model: ContractingPartnerStateAidGberSection?): ProjectContractingPartnerStateAidGberData?

    fun map(model: ContractingPartnerStateAidDeMinimisSection?): ProjectContractingPartnerStateAidDeMinimisData?
}
