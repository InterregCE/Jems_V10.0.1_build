package io.cloudflight.jems.server.project.controller.auditAndControl

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
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.AuditControlCorrectionTypeDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData.CorrectionAvailableFtlsDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData.CorrectionAvailableFundDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData.CorrectionAvailablePartnerDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData.CorrectionAvailablePartnerReportDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData.CorrectionEcPaymentDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.availableData.CorrectionProjectReportDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.AvailableCorrectionsForPaymentDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.ec.AccountingYear
import io.cloudflight.jems.server.payments.model.regular.PaymentEcStatus
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.auditAndControl.base.closeAuditControl.CloseAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.base.createAuditControl.CreateAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.base.getAuditControl.GetAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.base.listAuditControl.ListAuditControlIntetractor
import io.cloudflight.jems.server.project.service.auditAndControl.base.reopenAuditControl.ReopenAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.base.updateAuditControl.UpdateAuditControlInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForModification.GetAvailableCorrectionsForModificationInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getAvailableCorrectionsForPayment.GetAvailableCorrectionsForPaymentInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl.GetPartnerAndPartnerReportDataInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableFtls
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailableFund
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartnerReport
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionEcPayment
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionProjectReport
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.AvailableCorrectionsForPayment
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

class AuditControlControllerTest: UnitTest() {

    companion object {
        private const val PARTNER_ID = 10L
        private const val PROJECT_ID = 2L
        private const val AUDIT_CONTROL_ID = 3L
        private val startDate = ZonedDateTime.now()
        private val endDate = startDate.plusDays(10)
        private val finalReportDate = startDate.plusDays(12)

        private val ayStart = LocalDate.now()
        private val ayEnd = LocalDate.now().plusDays(5)

        private val correctionEcPayment = CorrectionEcPayment(
            id = 8L,
            status = PaymentEcStatus.Draft,
            accountingYear = AccountingYear(
                id = 19L,
                year = 2021,
                startDate = ayStart,
                endDate = ayEnd,
            ),
        )
        private val expectedCorrectionEcPayment = CorrectionEcPaymentDTO(
            id = 8L,
            status = PaymentEcStatusDTO.Draft,
            accountingYear = AccountingYearDTO(
                id = 19L,
                year = 2021,
                startDate = ayStart,
                endDate = ayEnd,
            ),
        )

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

        private val availableReports = listOf(
            CorrectionAvailablePartnerReport(
                id = 15L,
                reportNumber = 1,
                projectReport = projectReport,

                availableFunds = listOf(
                    CorrectionAvailableFund(ERDF_FUND, correctionEcPayment, true),
                )
            )
        )

        private val availableFtls = listOf(
            CorrectionAvailableFtls(
                orderNr = 4,
                programmeLumpSumId = 19L,
                name = setOf(InputTranslation(SystemLanguage.EL, "EL - name")),

                availableFunds = listOf(
                    CorrectionAvailableFund(ERDF_FUND, correctionEcPayment, false),
                )
            )
        )

        private val availablePartners = listOf(
            CorrectionAvailablePartner(
                partnerId = PARTNER_ID,
                partnerNumber = 1,
                partnerAbbreviation = "PARTNER",
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                partnerDisabled = false,
                availableReports = availableReports,
                availableFtls = availableFtls,
            )
        )
        private val expectedAvailableReports = listOf(
            CorrectionAvailablePartnerReportDTO(
                id = 15L,
                reportNumber = 1,
                projectReport = expectedProjectReport,

                availableFunds = listOf(
                    CorrectionAvailableFundDTO(expectedFund, expectedCorrectionEcPayment, true),
                )
            )
        )
        private val expectedAvailableFtls = listOf(
            CorrectionAvailableFtlsDTO(
                orderNr = 4,
                programmeLumpSumId = 19L,
                name = setOf(InputTranslation(SystemLanguage.EL, "EL - name")),

                availableFunds = listOf(
                    CorrectionAvailableFundDTO(expectedFund, expectedCorrectionEcPayment, false),
                )
            )
        )

        private val expectedAvailablePartners = listOf(
            CorrectionAvailablePartnerDTO(
                partnerId = PARTNER_ID,
                partnerNumber = 1,
                partnerAbbreviation = "PARTNER",
                partnerRole = ProjectPartnerRoleDTO.LEAD_PARTNER,
                partnerDisabled = false,
                availableReports = expectedAvailableReports,
                availableFtls = expectedAvailableFtls,
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

        private val auditControl = AuditControl(
            id = AUDIT_CONTROL_ID,
            number = 1,
            projectId = PROJECT_ID,
            projectCustomIdentifier = "Custom identifier",
            projectAcronym = "Custom acronym",
            status = AuditControlStatus.Ongoing,
            controllingBody = ControllingBody.Controller,
            controlType = AuditControlType.Administrative,
            startDate = startDate,
            endDate = endDate,
            finalReportDate = finalReportDate,
            totalControlledAmount = BigDecimal.TEN,
            totalCorrectionsAmount = BigDecimal.ZERO,
            existsClosed = true,
            existsOngoing = false,

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
            existsClosed = true,
            existsOngoing = false,

            comment = "COMMENT"
        )

        private val auditcontrolCorrection = AuditControlCorrection(
            id = 123L,
            orderNr = 1,
            status = AuditControlStatus.Closed,
            type = AuditControlCorrectionType.LinkedToInvoice,
            auditControlId = AUDIT_CONTROL_ID,
            auditControlNr = 1,
        )

        private val auditcontrolCorrectionDto = AuditControlCorrectionDTO(
            id = 123L,
            orderNr = 1,
            status = AuditStatusDTO.Closed,
            type = AuditControlCorrectionTypeDTO.LinkedToInvoice,
            auditControlId = AUDIT_CONTROL_ID,
            auditControlNumber = 1,
        )
    }

    @MockK
    lateinit var createAuditControlInteractor: CreateAuditControlInteractor

    @MockK
    lateinit var updateProjectAuditControlInteractor: UpdateAuditControlInteractor

    @MockK
    lateinit var listProjectAuditsInteractor: ListAuditControlIntetractor

    @MockK
    lateinit var getAuditDetailsInteractor: GetAuditControlInteractor

    @MockK
    lateinit var closeProjectAuditControlInteractor: CloseAuditControlInteractor

    @MockK
    lateinit var partnerDataInteractor: GetPartnerAndPartnerReportDataInteractor

    @MockK
    lateinit var reopenAuditControlInteractor: ReopenAuditControlInteractor

    @MockK
    lateinit var getCorrectionsForModification: GetAvailableCorrectionsForModificationInteractor

    @MockK
    lateinit var getCorrectionsForPayment: GetAvailableCorrectionsForPaymentInteractor


    @InjectMockKs
    lateinit var controller: AuditControlController

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
        every { updateProjectAuditControlInteractor.updateAudit(AUDIT_CONTROL_ID, auditControlUpdateDTO.toModel()) } returns auditControl
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
        every { getAuditDetailsInteractor.getDetails(auditControlId = AUDIT_CONTROL_ID) } returns auditControl

        assertThat(controller.getAuditDetail(PROJECT_ID, AUDIT_CONTROL_ID)).isEqualTo(expectedAudit)
    }

    @Test
    fun closeAuditControl() {
        every { closeProjectAuditControlInteractor.closeAuditControl(auditControlId = AUDIT_CONTROL_ID) } returns
                AuditControlStatus.Closed

        assertThat(controller.closeAuditControl(PROJECT_ID, AUDIT_CONTROL_ID)).isEqualTo(AuditStatusDTO.Closed)
    }

    @Test
    fun reopenAuditControl() {
        every { reopenAuditControlInteractor.reopenAuditControl(auditControlId = AUDIT_CONTROL_ID) } returns
            AuditControlStatus.Ongoing

        assertThat(controller.reopenAuditControl(PROJECT_ID, AUDIT_CONTROL_ID)).isEqualTo(AuditStatusDTO.Ongoing)
    }

    @Test
    fun getAvailableCorrectionsForModification() {
        every { getCorrectionsForModification.getAvailableCorrections(PROJECT_ID) } returns listOf(auditcontrolCorrection)
        assertThat(controller.getAvailableCorrectionsForModification(PROJECT_ID)).isEqualTo(
            listOf(auditcontrolCorrectionDto)
        )
    }

    @Test
    fun getAvailableCorrectionsForPayment() {
        val paymentId = 51L
        every { getCorrectionsForPayment.getAvailableCorrections(paymentId) } returns listOf(
            AvailableCorrectionsForPayment(2L, listOf(auditcontrolCorrection))
        )

        assertThat(controller.getAvailableCorrectionsForPayment(PROJECT_ID, paymentId)).isEqualTo(
            listOf(AvailableCorrectionsForPaymentDTO(2L, listOf(auditcontrolCorrectionDto)))
        )
    }
}
