package io.cloudflight.jems.server.project.controller.report.partner.expenditureCosts

import io.cloudflight.jems.api.project.dto.partner.budget.ProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportExpenditureCostDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportInvestmentDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportLumpSumDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportUnitCostDTO
import io.cloudflight.jems.api.project.report.partner.ProjectPartnerReportExpenditureCostsApi
import io.cloudflight.jems.server.project.controller.partner.budget.toProjectPartnerBudgetOptionsDto
import io.cloudflight.jems.server.project.controller.report.partner.toDto
import io.cloudflight.jems.server.project.controller.report.partner.toProjectFile
import io.cloudflight.jems.server.project.service.report.partner.expenditure.deleteParkedExpenditure.DeleteParkedExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableBudgetOptionsForReport.GetAvailableBudgetOptionsForReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableInvestmentsForReport.GetAvailableInvestmentsForReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableLumpSumsForReport.GetAvailableLumpSumsForReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableParkedExpenditureList.GetAvailableParkedExpenditureListInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableUnitCostsForReport.GetAvailableUnitCostsForReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure.GetProjectPartnerReportExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.reincludeParkedExpenditure.ReIncludeParkedExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure.UpdateProjectPartnerReportExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.uploadFileToProjectPartnerReportExpenditure.UploadFileToProjectPartnerReportExpenditureInteractor
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectPartnerReportExpenditureCostsController(
    private val getProjectPartnerReportExpenditureInteractor: GetProjectPartnerReportExpenditureInteractor,
    private val updateProjectPartnerReportExpenditureInteractor: UpdateProjectPartnerReportExpenditureInteractor,
    private val uploadFileToExpenditure: UploadFileToProjectPartnerReportExpenditureInteractor,
    private val getAvailableLumpSumsForReportInteractor: GetAvailableLumpSumsForReportInteractor,
    private val getAvailableUnitCostsForReportInteractor: GetAvailableUnitCostsForReportInteractor,
    private val getAvailableInvestmentsForReportInteractor: GetAvailableInvestmentsForReportInteractor,
    private val getAvailableBudgetOptionsForReportInteractor: GetAvailableBudgetOptionsForReportInteractor,
    private val getAvailableParkedExpenditureListInteractor: GetAvailableParkedExpenditureListInteractor,
    private val reIncludeParkedExpenditureInteractor: ReIncludeParkedExpenditureInteractor,
    private val deleteParkedExpenditureInteractor: DeleteParkedExpenditureInteractor,
) : ProjectPartnerReportExpenditureCostsApi {

    override fun getProjectPartnerReports(
        partnerId: Long, reportId: Long
    ): List<ProjectPartnerReportExpenditureCostDTO> =
        getProjectPartnerReportExpenditureInteractor.getExpenditureCosts(
            partnerId = partnerId,
            reportId = reportId,
        ).toDto()

    override fun updatePartnerReportExpenditures(
        partnerId: Long,
        reportId: Long,
        expenditureCosts: List<ProjectPartnerReportExpenditureCostDTO>
    ): List<ProjectPartnerReportExpenditureCostDTO> =
        updateProjectPartnerReportExpenditureInteractor.updatePartnerReportExpenditureCosts(
            partnerId = partnerId,
            reportId = reportId,
            expenditureCosts = expenditureCosts.toModel()
        ).toDto()

    override fun uploadFileToExpenditure(
        partnerId: Long,
        reportId: Long,
        expenditureId: Long,
        file: MultipartFile,
    ) =
        uploadFileToExpenditure
            .uploadToExpenditure(partnerId, reportId, expenditureId = expenditureId, file.toProjectFile())
            .toDto()

    override fun getAvailableLumpSums(partnerId: Long, reportId: Long): List<ProjectPartnerReportLumpSumDTO> =
        getAvailableLumpSumsForReportInteractor.getLumpSums(partnerId = partnerId, reportId = reportId).toLumpSumDto()

    override fun getAvailableUnitCosts(partnerId: Long, reportId: Long): List<ProjectPartnerReportUnitCostDTO> =
        getAvailableUnitCostsForReportInteractor.getUnitCosts(partnerId = partnerId, reportId = reportId).toUnitCostDto()

    override fun getAvailableInvestments(partnerId: Long, reportId: Long): List<ProjectPartnerReportInvestmentDTO> =
        getAvailableInvestmentsForReportInteractor.getInvestments(partnerId, reportId).toInvestmentDto()

    override fun getAvailableBudgetOptions(partnerId: Long, reportId: Long): ProjectPartnerBudgetOptionsDto =
        getAvailableBudgetOptionsForReportInteractor.getBudgetOptions(partnerId, reportId).toProjectPartnerBudgetOptionsDto()

    override fun getAvailableParkedExpenditures(partnerId: Long, reportId: Long, pageable: Pageable) =
        getAvailableParkedExpenditureListInteractor.getParked(partnerId = partnerId, reportId, pageable).toDto()

    override fun reIncludeParkedExpenditure(partnerId: Long, reportId: Long, expenditureId: Long) =
        reIncludeParkedExpenditureInteractor.reIncludeParkedExpenditure(partnerId = partnerId, reportId, expenditureId)

    override fun deleteParkedExpenditure(partnerId: Long,  reportId: Long, expenditureId: Long) =
        deleteParkedExpenditureInteractor.deleteParkedExpenditure(partnerId = partnerId, reportId, expenditureId)

}
