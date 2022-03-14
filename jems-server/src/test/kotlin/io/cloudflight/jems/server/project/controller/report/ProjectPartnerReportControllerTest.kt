package io.cloudflight.jems.server.project.controller.report

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusDTO
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusTypeDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.description.ProjectTargetGroupDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerVatRecoveryDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportDTO
import io.cloudflight.jems.api.project.dto.report.ProjectPartnerReportSummaryDTO
import io.cloudflight.jems.api.project.dto.report.ReportStatusDTO
import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportIdentificationCoFinancingDTO
import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportIdentificationDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportSummary
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.createProjectPartnerReport.CreateProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.getProjectPartnerReport.GetProjectPartnerReportInteractor
import io.cloudflight.jems.server.project.service.report.partner.partnerReportExpenditureCosts.PartnerReportExpenditureCostsInteractor
import io.cloudflight.jems.server.project.service.report.partner.submitProjectPartnerReport.SubmitProjectPartnerReportInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime

internal class ProjectPartnerReportControllerTest : UnitTest() {

    companion object {
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)

        private val reportSummary = ProjectPartnerReportSummary(
            id = 754,
            reportNumber = 1,
            status = ReportStatus.Draft,
            version = "6.1",
            firstSubmission = null,
            createdAt = YESTERDAY,
        )

        private val reportSummaryDTO = ProjectPartnerReportSummaryDTO(
            id = reportSummary.id,
            reportNumber = reportSummary.reportNumber,
            status = ReportStatusDTO.Draft,
            linkedFormVersion = reportSummary.version,
            firstSubmission = null,
            createdAt = reportSummary.createdAt,
        )

        private val report = ProjectPartnerReport(
            id = reportSummary.id,
            reportNumber = reportSummary.reportNumber,
            status = reportSummary.status,
            version = reportSummary.version,
            identification = PartnerReportIdentification(
                projectIdentifier = "projectIdentifier",
                projectAcronym = "projectAcronym",
                partnerNumber = 4,
                partnerAbbreviation = "partnerAbbreviation",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = "nameInOriginalLanguage",
                nameInEnglish = "nameInEnglish",
                legalStatus = ProgrammeLegalStatus(
                    id = 7L,
                    type = ProgrammeLegalStatusType.PUBLIC,
                    description = setOf(InputTranslation(SystemLanguage.EN, "en desc"))
                ),
                partnerType = ProjectTargetGroup.Egtc,
                vatRecovery = ProjectPartnerVatRecovery.Yes,
                coFinancing = listOf(
                    ProjectPartnerCoFinancing(
                        fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                        percentage = BigDecimal.ONE,
                        fund = ProgrammeFund(
                            id = 766L,
                            selected = true,
                            type = ProgrammeFundType.ERDF,
                        )
                    ),
                ),
            ),
        )

        private val reportDTO = ProjectPartnerReportDTO(
            id = reportSummary.id,
            reportNumber = reportSummary.reportNumber,
            status = ReportStatusDTO.Draft,
            linkedFormVersion = reportSummary.version,
            identification = PartnerReportIdentificationDTO(
                projectIdentifier = report.identification.projectIdentifier,
                projectAcronym = report.identification.projectAcronym,
                partnerNumber = report.identification.partnerNumber,
                partnerAbbreviation = report.identification.partnerAbbreviation,
                partnerRole = ProjectPartnerRoleDTO.PARTNER,
                nameInOriginalLanguage = report.identification.nameInOriginalLanguage,
                nameInEnglish = report.identification.nameInEnglish,
                legalStatus = ProgrammeLegalStatusDTO(
                    id = 7L,
                    type = ProgrammeLegalStatusTypeDTO.PUBLIC,
                    description = setOf(InputTranslation(SystemLanguage.EN, "en desc"))
                ),
                partnerType = ProjectTargetGroupDTO.Egtc,
                vatRecovery = ProjectPartnerVatRecoveryDTO.Yes,
                coFinancing = listOf(
                    PartnerReportIdentificationCoFinancingDTO(
                        fund = ProgrammeFundDTO(
                            id = 766L,
                            selected = true,
                            type = ProgrammeFundTypeDTO.ERDF,
                        ),
                        percentage = BigDecimal.ONE,
                    )
                )
            )
        )
    }

    @MockK
    lateinit var createPartnerReport: CreateProjectPartnerReportInteractor

    @MockK
    lateinit var submitPartnerReport: SubmitProjectPartnerReportInteractor

    @MockK
    lateinit var getPartnerReport: GetProjectPartnerReportInteractor

    @MockK
    lateinit var partnerReportExpenditureCostsInteractor: PartnerReportExpenditureCostsInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerReportController

    @Test
    fun getProjectPartnerReports() {
        every { getPartnerReport.findAll(14, Pageable.unpaged()) } returns PageImpl(listOf(reportSummary))
        assertThat(
            controller.getProjectPartnerReports(
                14,
                Pageable.unpaged()
            ).content
        ).containsExactly(reportSummaryDTO)
    }

    @Test
    fun getProjectPartnerReport() {
        every { getPartnerReport.findById(14, 240) } returns report
        assertThat(controller.getProjectPartnerReport(14, 240)).isEqualTo(reportDTO)
    }

    @Test
    fun `getProjectPartnerReport - with optional values`() {
        every { getPartnerReport.findById(14, 240) } returns report.copy(
            identification = report.identification.copy(
                legalStatus = null,
                partnerType = null,
                vatRecovery = null,
            )
        )
        assertThat(controller.getProjectPartnerReport(14, 240)).isEqualTo(
            reportDTO.copy(
                identification = reportDTO.identification.copy(
                    legalStatus = null,
                    partnerType = null,
                    vatRecovery = null,
                )
            )
        )
    }

    @Test
    fun createProjectPartnerReport() {
        every { createPartnerReport.createReportFor(18) } returns reportSummary
        assertThat(controller.createProjectPartnerReport(18)).isEqualTo(reportSummaryDTO)
    }

    @Test
    fun submitProjectPartnerReport() {
        val thisMoment = ZonedDateTime.now()
        every { submitPartnerReport.submit(18, 310) } returns reportSummary.copy(
            status = ReportStatus.Submitted,
            firstSubmission = thisMoment,
        )
        assertThat(controller.submitProjectPartnerReport(18, 310)).isEqualTo(
            reportSummaryDTO.copy(
                status = ReportStatusDTO.Submitted,
                firstSubmission = thisMoment,
            )
        )
    }
}
