package io.cloudflight.jems.server.project.controller.auditAndControl

import io.cloudflight.jems.api.payments.dto.PaymentApplicationToEcDTO
import io.cloudflight.jems.api.payments.dto.PaymentEcStatusDTO
import io.cloudflight.jems.api.payments.dto.applicationToEc.AccountingYearDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditControlDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditControlTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.AuditStatusDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ControllingBodyDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.ProjectAuditControlUpdateDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionAvailablePartnerDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionAvailablePartnerReportDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionAvailablePaymentDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.CorrectionProjectReportDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.ec.PaymentApplicationToEc
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.auditAndControl.closeProjectAudit.CloseProjectAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailablePartnerReport
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailablePayment
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionProjectReport
import io.cloudflight.jems.server.project.service.auditAndControl.createProjectAudit.CreateProjectAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getPartnerAndPartnerReportData.GetPartnerAndPartnerReportDataInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getProjectAuditDetails.GetProjectAuditControlDetailsInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.listProjectAudits.ListProjectAuditsIntetractor
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.updateProjectAudit.UpdateProjectAuditControlInteractor
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime

class ProjectAuditControllerTest: UnitTest() {

    companion object {
        private const val PARTNER_ID = 10L
        private const val PROJECT_ID = 2L
        private const val AUDIT_CONTROL_ID = 3L
        private val startDate = ZonedDateTime.now()
        private val endDate = startDate.plusDays(10)
        private val finalReportDate = startDate.plusDays(12)
        private const val paymentApplicationsToEcId = 51L
        private val accountingYear = AccountingYear(2L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))
        private val expectedAccountingYear = AccountingYearDTO(2L, 2021, LocalDate.of(2021, 1, 1), LocalDate.of(2022, 6, 30))

        private val projectReport =
            CorrectionProjectReport(
                id = 7L,
                number = 1,
            )
        private val expectedProjectReport =
            CorrectionProjectReportDTO(
                id = 7L,
                number = 1,
            )

        private val ERDF_FUND = ProgrammeFund(
            id = 1L,
            type = ProgrammeFundType.ERDF, selected = true,
            abbreviation = setOf(
                InputTranslation(
                    SystemLanguage.EN, "EN ERDF"
                ),
                InputTranslation(SystemLanguage.SK, "SK ERDF")
            ),
            description = setOf(
                InputTranslation(SystemLanguage.EN, "EN desc"),
                InputTranslation(SystemLanguage.SK, "SK desc")
            )
        )

        private val expectedFund = ProgrammeFundDTO(
            id = 1L,
            selected = true,
            type = ProgrammeFundTypeDTO.ERDF,
            abbreviation = setOf(
                InputTranslation(
                    SystemLanguage.EN, "EN ERDF"
                ),
                InputTranslation(SystemLanguage.SK, "SK ERDF")
            ),
            description = setOf(
                InputTranslation(SystemLanguage.EN, "EN desc"),
                InputTranslation(SystemLanguage.SK, "SK desc")
            )
        )

        private val paymentApplicationToEc = PaymentApplicationToEc(
            id = paymentApplicationsToEcId,
            programmeFund = ERDF_FUND,
            accountingYear = accountingYear,
            status = PaymentEcStatus.Draft
        )

        private val expectedPaymentApplicationToEc = PaymentApplicationToEcDTO(
            id = paymentApplicationsToEcId,
            programmeFund = expectedFund,
            accountingYear = expectedAccountingYear,
            status = PaymentEcStatusDTO.Draft
        )
        private val correctionAvailablePayment = CorrectionAvailablePayment(
            fund = ERDF_FUND,
            ecPayment = paymentApplicationToEc

        )

        private val expectedCorrectionAvailablePayment = CorrectionAvailablePaymentDTO(
            fund = expectedFund,
            ecPayment = expectedPaymentApplicationToEc

        )

        private val availableReports = listOf(
            CorrectionAvailablePartnerReport(
                id = 15L,
                reportNumber = 1,
                projectReport = projectReport,

                availableReportFunds = listOf(ERDF_FUND),
                availablePayments = listOf(correctionAvailablePayment)
            )
        )

        private val availablePartners = listOf(
            CorrectionAvailablePartner(
                partnerId = PARTNER_ID,
                partnerNumber = 1,
                partnerAbbreviation = "PARTNER",
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                partnerDisabled = false,
                availableReports = availableReports
            )
        )
        private val expectedAvailableReports = listOf(
            CorrectionAvailablePartnerReportDTO(
                id = 15L,
                reportNumber = 1,
                projectReport = expectedProjectReport,

                availableReportFunds = listOf(expectedFund),
                availablePayments = listOf(expectedCorrectionAvailablePayment)
            )
        )

        private val expectedAvailablePartners = listOf(
            CorrectionAvailablePartnerDTO(
                partnerId = PARTNER_ID,
                partnerNumber = 1,
                partnerAbbreviation = "PARTNER",
                partnerRole = ProjectPartnerRoleDTO.LEAD_PARTNER,
                partnerDisabled = false,
                availableReports = expectedAvailableReports
            )
        )

        private val auditControlUpdateDTO = ProjectAuditControlUpdateDTO(
            controllingBody = ControllingBodyDTO.Controller,
            controlType = AuditControlTypeDTO.Administrative,
            startDate = startDate,
            endDate = endDate,
            finalReportDate = finalReportDate,
            totalControlledAmount = BigDecimal.TEN,
            comment = "COMMENT"
        )

        private val auditControl = ProjectAuditControl(
            id = AUDIT_CONTROL_ID,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "Custom identifier",
            status = AuditStatus.Ongoing,
            controllingBody = ControllingBody.Controller,
            controlType = AuditControlType.Administrative,
            startDate = startDate,
            endDate = endDate,
            finalReportDate = finalReportDate,
            totalControlledAmount = BigDecimal.TEN,
            totalCorrectionsAmount = BigDecimal.ZERO,

            comment = "COMMENT"
        )

        private val expectedAudit = AuditControlDTO(
            id = AUDIT_CONTROL_ID,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "Custom identifier",
            status = AuditStatusDTO.Ongoing,
            controllingBody = ControllingBodyDTO.Controller,
            controlType = AuditControlTypeDTO.Administrative,
            startDate = startDate,
            endDate = endDate,
            finalReportDate = finalReportDate,
            totalControlledAmount = BigDecimal.TEN,
            totalCorrectionsAmount = BigDecimal.ZERO,

            comment = "COMMENT"
        )
    }

    @MockK
    lateinit var createAuditControlInteractor: CreateProjectAuditControlInteractor

    @MockK
    lateinit var updateProjectAuditControlInteractor: UpdateProjectAuditControlInteractor

    @MockK
    lateinit var listProjectAuditsInteractor: ListProjectAuditsIntetractor

    @MockK
    lateinit var getAuditDetailsInteractor: GetProjectAuditControlDetailsInteractor

    @MockK
    lateinit var closeProjectAuditControlInteractor: CloseProjectAuditControlInteractor

    @MockK
    lateinit var partnerDataInteractor: GetPartnerAndPartnerReportDataInteractor

    @InjectMockKs
    lateinit var controller: ProjectAuditController

    @Test
    fun getPartnerAndPartnerReportData() {
        every {
            partnerDataInteractor.getPartnerAndPartnerReportData(
                PROJECT_ID,
            )
        } returns availablePartners

        assertThat(
            controller.getPartnerAndPartnerReportData(
                PROJECT_ID,
            )
        ).isEqualTo(expectedAvailablePartners)
    }

    @Test
    fun createProjectAudit() {
        every { createAuditControlInteractor.createAudit(PROJECT_ID, auditControlUpdateDTO.toModel()) } returns auditControl
        assertThat(controller.createProjectAudit(PROJECT_ID, auditControlUpdateDTO)).isEqualTo(expectedAudit)
    }

    @Test
    fun updateProjectAudit() {
        every { updateProjectAuditControlInteractor.updateAudit(PROJECT_ID, AUDIT_CONTROL_ID, auditControlUpdateDTO.toModel()) } returns auditControl
        assertThat(controller.updateProjectAudit(PROJECT_ID, AUDIT_CONTROL_ID, auditControlUpdateDTO)).isEqualTo(expectedAudit)
    }

    @Test
    fun listAuditsForProject() {
        every { listProjectAuditsInteractor.listForProject(PROJECT_ID, Pageable.unpaged()) } returns PageImpl(listOf(
            auditControl)
        )

        assertThat(controller.listAuditsForProject(PROJECT_ID, Pageable.unpaged())).isEqualTo(PageImpl(listOf(
            expectedAudit))
        )
    }

    @Test
    fun getAuditDetail() {
        every { getAuditDetailsInteractor.getDetails(projectId = PROJECT_ID, auditId = AUDIT_CONTROL_ID) } returns auditControl

        assertThat(controller.getAuditDetail(PROJECT_ID, AUDIT_CONTROL_ID)).isEqualTo(expectedAudit)
    }

    @Test
    fun closeAuditControl() {
        every { closeProjectAuditControlInteractor.closeAuditControl(projectId = PROJECT_ID, auditControlId = AUDIT_CONTROL_ID) } returns AuditStatus.Closed

        assertThat(controller.closeAuditControl(PROJECT_ID, AUDIT_CONTROL_ID)).isEqualTo(AuditStatusDTO.Closed)
    }
}
