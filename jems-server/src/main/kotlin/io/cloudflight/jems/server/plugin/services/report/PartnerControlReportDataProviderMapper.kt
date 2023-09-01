package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.plugin.contract.models.common.UserSummaryData
import io.cloudflight.jems.plugin.contract.models.report.partner.control.ProjectPartnerReportExpenditureVerificationData
import io.cloudflight.jems.plugin.contract.models.report.partner.control.ReportDesignatedControllerData
import io.cloudflight.jems.plugin.contract.models.report.partner.control.ReportVerificationData
import io.cloudflight.jems.plugin.contract.models.report.partner.control.overview.ControlDeductionOverviewData
import io.cloudflight.jems.plugin.contract.models.report.partner.control.overview.ControlOverviewData
import io.cloudflight.jems.plugin.contract.models.report.partner.control.overview.ControlWorkOverviewData
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlDeductionOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlOverview
import io.cloudflight.jems.server.project.service.report.model.partner.control.overview.ControlWorkOverview
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportDesignatedController
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import io.cloudflight.jems.server.user.service.model.UserSummary
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers


private val mapper = Mappers.getMapper(PartnerControlReportDataMapper::class.java)

fun ReportDesignatedController.toDataModel(
    userSummaries:  Map<Long, UserSummary>
) = ReportDesignatedControllerData(
    controlInstitution = this.controlInstitution,
    controlInstitutionId = this.controlInstitutionId,
    controllerUser = userSummaries[this.controllingUserId]?.toDataModel(),
    jobTitle = this.jobTitle,
    divisionUnit = this.divisionUnit,
    address = this.address,
    countryCode = this.countryCode,
    country = this.country,
    telephone = this.telephone,
    controllerReviewer = userSummaries[this.controllerReviewerId]?.toDataModel()
)
fun ReportVerification.toDataModel() = mapper.map(this)

fun ControlOverview.toDataModel() = mapper.map(this)
fun ControlWorkOverview.toDataModel() = mapper.map(this)

fun List<ProjectPartnerReportExpenditureVerification>.toModelDataList() = map { mapper.map(it) }
fun ControlDeductionOverview.toDataModel() = mapper.map(this)
fun UserSummary.toDataModel() = mapper.map(this)
@Mapper
interface PartnerControlReportDataMapper {

    fun map(model: ControlOverview): ControlOverviewData

    fun map(model: ControlWorkOverview): ControlWorkOverviewData

    fun map(model: ReportVerification): ReportVerificationData

    fun map(model: ProjectPartnerReportExpenditureVerification): ProjectPartnerReportExpenditureVerificationData

    fun map(model: ControlDeductionOverview): ControlDeductionOverviewData

    fun map(userSummary: UserSummary): UserSummaryData
}
