package io.cloudflight.jems.server.project.controller.report.expenditureCosts

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.ProjectPartnerReportExpenditureCostDTO
import io.cloudflight.jems.api.project.report.ProjectPartnerReportExpenditureCostsApi
import io.cloudflight.jems.server.project.controller.report.toDto
import io.cloudflight.jems.server.project.controller.report.toProjectFile
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure.GetProjectPartnerReportExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure.UpdateProjectPartnerReportExpenditureInteractor
import io.cloudflight.jems.server.project.service.report.partner.expenditure.uploadFileToProjectPartnerReportExpenditure.UploadFileToProjectPartnerReportExpenditureInteractor
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class ProjectPartnerReportExpenditureCostsController(
    private val getProjectPartnerReportExpenditureInteractor: GetProjectPartnerReportExpenditureInteractor,
    private val updateProjectPartnerReportExpenditureInteractor: UpdateProjectPartnerReportExpenditureInteractor,
    private val uploadFileToExpenditure: UploadFileToProjectPartnerReportExpenditureInteractor,
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
}
